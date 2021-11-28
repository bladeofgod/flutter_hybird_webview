package remote_webview.service.manager;

import java.util.HashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJiaqi
 * @date 2021/11/28
 * Description: Request model.  {@link RemoteViewModuleManager}
 */
public abstract class PatrolRequest implements Delayed {

    RemoteViewModuleManager manager;
    //unit: milliseconds
    //patrolTime + System.currentTimeMillis()
    long reportTime;

    int questType;

    PatrolRequest(RemoteViewModuleManager moduleManager,
                  long patrolTime,
                  int questType) {
        this.manager = moduleManager;
        this.reportTime = System.currentTimeMillis() + patrolTime;
        this.questType = questType;
    }
    
    public abstract HashMap doCheck();

    @Override
    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert(reportTime - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        PatrolRequest r = (PatrolRequest) delayed;
        return Long.compare(this.reportTime, r.reportTime);
    }
}