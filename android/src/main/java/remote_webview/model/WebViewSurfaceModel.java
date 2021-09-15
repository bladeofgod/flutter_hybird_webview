package remote_webview.model;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.DisplayMetrics;
import android.view.Surface;

import io.flutter.view.TextureRegistry;

public class WebViewSurfaceModel extends ViewSurfaceModel {

    protected WebViewSurfaceModel(int id, Surface surface) {
        super(id, surface);
    }

    public static class Builder{

        private final Context mPppContext;

        public Builder(Context appContext) {
            this.mPppContext = appContext;
            userScreenSize();
        }


        private int id;

        private SurfaceTexture mSurfaceTexture;

        private Surface mSurface;

        public Builder init(TextureRegistry.SurfaceTextureEntry textureEntry) {
            mSurfaceTexture = textureEntry.surfaceTexture();
            mSurface = new Surface(mSurfaceTexture);

            return this;
        }

        /**
         * The surfaceTexture's DefaultBufferSize
         *
         * Use screen's width and height in default.
         */
        private int width;
        private int height;

        /**
         * Set surfaceTexture's DefaultBufferSize
         * @param width
         * @param height
         * @return
         */
        public Builder setSurfaceDefaultBufferSize(int width, int height) {
            mSurfaceTexture.setDefaultBufferSize(width, height);
            return this;
        }

        public WebViewSurfaceModel build() {
            return new WebViewSurfaceModel(id, mSurface);
        }

        private void userScreenSize() {
            DisplayMetrics dm = mPppContext.getResources().getDisplayMetrics();
            width = dm.widthPixels;
            height = dm.heightPixels;
        }


    }
}
