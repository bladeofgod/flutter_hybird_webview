

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/src/remote_webview/remote_texture.dart';

///This is not concern the paint or layout for now, just for handle
///flutter's motion event.
class RemoteTextureBox extends TextureBox with _RemotePlatformViewGestureMixin{

  RemoteTextureBox({
    required TextureAndroidRemoteController viewController,
    required PlatformViewHitTestBehavior hitTestBehavior,
    required Set<Factory<OneSequenceGestureRecognizer>> gestureRecognizers,
    required int textureId,
    FilterQuality filterQuality = FilterQuality.low,
  }) : _viewController = viewController,
      super(textureId: textureId, filterQuality: filterQuality) {
    //_viewController.pointTransformer = (Offset offset) => globalToLocal(offset);
    this.hitTestBehavior = hitTestBehavior;
    updateGestureRecognizers(gestureRecognizers);
    //do not need
    //_viewController.addOnPlatformViewCreatedListener(_onPlatformViewCreated);
  }


  /// The Android view controller for the Android view associated with this render object.
  TextureAndroidRemoteController get viewcontroller => _viewController;
  TextureAndroidRemoteController _viewController;

  set viewController(TextureAndroidRemoteController controller) {
    if ( _viewController == controller) {
      return;
    }
    final bool needsSemanticsUpdate = _viewController.viewId != controller.viewId;
    _viewController = controller;
    markNeedsPaint();
    if (needsSemanticsUpdate) {
      markNeedsSemanticsUpdate();
    }
  }


  /// {@template flutter.rendering.RenderAndroidView.updateGestureRecognizers}
  /// Updates which gestures should be forwarded to the platform view.
  ///
  /// Gesture recognizers created by factories in this set participate in the gesture arena for each
  /// pointer that was put down on the render box. If any of the recognizers on this list wins the
  /// gesture arena, the entire pointer event sequence starting from the pointer down event
  /// will be dispatched to the Android view.
  ///
  /// The `gestureRecognizers` property must not contain more than one factory with the same [Factory.type].
  ///
  /// Setting a new set of gesture recognizer factories with the same [Factory.type]s as the current
  /// set has no effect, because the factories' constructors would have already been called with the previous set.
  /// {@endtemplate}
  ///
  /// Any active gesture arena the Android view participates in is rejected when the
  /// set of gesture recognizers is changed.
  void updateGestureRecognizers(Set<Factory<OneSequenceGestureRecognizer>> gestureRecognizers) {
    _updateGestureRecognizersWithCallBack(gestureRecognizers, _viewController.dispatchPointerEvent);
  }

}

typedef _HandlePointerEvent = Future<void> Function(PointerEvent event);


/// The Mixin handling the pointer events and gestures of a platform view render box.
mixin _RemotePlatformViewGestureMixin on RenderBox implements MouseTrackerAnnotation {

  /// How to behave during hit testing.
  // Changing _hitTestBehavior might affect which objects are considered hovered over.
  set hitTestBehavior(PlatformViewHitTestBehavior value) {
    if (value != _hitTestBehavior) {
      _hitTestBehavior = value;
      if (owner != null)
        markNeedsPaint();
    }
  }
  PlatformViewHitTestBehavior? _hitTestBehavior;

  _HandlePointerEvent? _handlePointerEvent;

  /// {@macro  flutter.rendering.RenderAndroidView.updateGestureRecognizers}
  ///
  /// Any active gesture arena the `PlatformView` participates in is rejected when the
  /// set of gesture recognizers is changed.
  void _updateGestureRecognizersWithCallBack(Set<Factory<OneSequenceGestureRecognizer>> gestureRecognizers, _HandlePointerEvent handlePointerEvent) {
    assert(gestureRecognizers != null);
    assert(
    _factoriesTypeSet(gestureRecognizers).length == gestureRecognizers.length,
    'There were multiple gesture recognizer factories for the same type, there must only be a single '
        'gesture recognizer factory for each gesture recognizer type.',);
    if (_factoryTypesSetEquals(gestureRecognizers, _gestureRecognizer?.gestureRecognizerFactories)) {
      return;
    }
    _gestureRecognizer?.dispose();
    _gestureRecognizer = _PlatformViewGestureRecognizer(handlePointerEvent, gestureRecognizers);
    _handlePointerEvent = handlePointerEvent;
  }

  _PlatformViewGestureRecognizer? _gestureRecognizer;

  @override
  bool hitTest(BoxHitTestResult result, { required Offset position }) {
    if (_hitTestBehavior == PlatformViewHitTestBehavior.transparent || !size.contains(position)) {
      return false;
    }
    result.add(BoxHitTestEntry(this, position));
    return _hitTestBehavior == PlatformViewHitTestBehavior.opaque;
  }

  @override
  bool hitTestSelf(Offset position) => _hitTestBehavior != PlatformViewHitTestBehavior.transparent;

  @override
  PointerEnterEventListener? get onEnter => null;

  @override
  PointerExitEventListener? get onExit => null;

  @override
  MouseCursor get cursor => MouseCursor.uncontrolled;

  @override
  bool get validForMouseTracker => true;

  @override
  void handleEvent(PointerEvent event, HitTestEntry entry) {
    if (event is PointerDownEvent) {
      _gestureRecognizer!.addPointer(event);
    }
    if (event is PointerHoverEvent) {
      _handlePointerEvent?.call(event);
    }
  }

  @override
  void detach() {
    _gestureRecognizer!.reset();
    super.detach();
  }
}


// This recognizer constructs gesture recognizers from a set of gesture recognizer factories
// it was give, adds all of them to a gesture arena team with the _PlatformViewGestureRecognizer
// as the team captain.
// As long as the gesture arena is unresolved, the recognizer caches all pointer events.
// When the team wins, the recognizer sends all the cached pointer events to `_handlePointerEvent`, and
// sets itself to a "forwarding mode" where it will forward any new pointer event to `_handlePointerEvent`.
class _PlatformViewGestureRecognizer extends OneSequenceGestureRecognizer {
  _PlatformViewGestureRecognizer(
      _HandlePointerEvent handlePointerEvent,
      this.gestureRecognizerFactories, {
        PointerDeviceKind? kind,
      }) : super(kind: kind) {
    team = GestureArenaTeam()
      ..captain = this;
    _gestureRecognizers = gestureRecognizerFactories.map(
          (Factory<OneSequenceGestureRecognizer> recognizerFactory) {
        final OneSequenceGestureRecognizer gestureRecognizer = recognizerFactory.constructor();
        gestureRecognizer.team = team;
        // The below gesture recognizers requires at least one non-empty callback to
        // compete in the gesture arena.
        // https://github.com/flutter/flutter/issues/35394#issuecomment-562285087
        if (gestureRecognizer is LongPressGestureRecognizer) {
          gestureRecognizer.onLongPress ??= (){};
        } else if (gestureRecognizer is DragGestureRecognizer) {
          gestureRecognizer.onDown ??= (_){};
        } else if (gestureRecognizer is TapGestureRecognizer) {
          gestureRecognizer.onTapDown ??= (_){};
        }
        return gestureRecognizer;
      },
    ).toSet();
    _handlePointerEvent = handlePointerEvent;
  }

  late _HandlePointerEvent _handlePointerEvent;

  // Maps a pointer to a list of its cached pointer events.
  // Before the arena for a pointer is resolved all events are cached here, if we win the arena
  // the cached events are dispatched to `_handlePointerEvent`, if we lose the arena we clear the cache for
  // the pointer.
  final Map<int, List<PointerEvent>> cachedEvents = <int, List<PointerEvent>>{};

  // Pointer for which we have already won the arena, events for pointers in this set are
  // immediately dispatched to `_handlePointerEvent`.
  final Set<int> forwardedPointers = <int>{};

  // We use OneSequenceGestureRecognizers as they support gesture arena teams.
  // TODO(amirh): get a list of GestureRecognizers here.
  // https://github.com/flutter/flutter/issues/20953
  final Set<Factory<OneSequenceGestureRecognizer>> gestureRecognizerFactories;
  late Set<OneSequenceGestureRecognizer> _gestureRecognizers;

  @override
  void addAllowedPointer(PointerDownEvent event) {
    //super.addAllowedPointer(event);
    startTrackingPointer(event.pointer, event.transform);
    for (final OneSequenceGestureRecognizer recognizer in _gestureRecognizers) {
      recognizer.addPointer(event);
    }
  }

  @override
  String get debugDescription => 'Remote Platform view';

  @override
  void didStopTrackingLastPointer(int pointer) { }

  @override
  void handleEvent(PointerEvent event) {
    if (!forwardedPointers.contains(event.pointer)) {
      _cacheEvent(event);
    } else {
      _handlePointerEvent(event);
    }
    stopTrackingIfPointerNoLongerDown(event);
  }

  @override
  void acceptGesture(int pointer) {
    _flushPointerCache(pointer);
    forwardedPointers.add(pointer);
  }

  @override
  void rejectGesture(int pointer) {
    stopTrackingPointer(pointer);
    cachedEvents.remove(pointer);
  }

  void _cacheEvent(PointerEvent event) {
    if (!cachedEvents.containsKey(event.pointer)) {
      cachedEvents[event.pointer] = <PointerEvent> [];
    }
    cachedEvents[event.pointer]!.add(event);
  }

  void _flushPointerCache(int pointer) {
    cachedEvents.remove(pointer)?.forEach(_handlePointerEvent);
  }

  @override
  void stopTrackingPointer(int pointer) {
    super.stopTrackingPointer(pointer);
    forwardedPointers.remove(pointer);
  }

  void reset() {
    forwardedPointers.forEach(super.stopTrackingPointer);
    forwardedPointers.clear();
    cachedEvents.keys.forEach(super.stopTrackingPointer);
    cachedEvents.clear();
    resolve(GestureDisposition.rejected);
  }
}


Set<Type> _factoriesTypeSet<T>(Set<Factory<T>> factories) {
  return factories.map<Type>((Factory<T> factory) => factory.type).toSet();
}


bool _factoryTypesSetEquals<T>(Set<Factory<T>>? a, Set<Factory<T>>? b) {
  if (a == b) {
    return true;
  }
  if (a == null ||  b == null) {
    return false;
  }
  return setEquals(_factoriesTypeSet(a), _factoriesTypeSet(b));
}














