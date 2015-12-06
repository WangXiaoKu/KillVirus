package androidstudio.killvirus;

/**
 * Writer : 王苦苦
 * <p/>
 * Copyright : 王宏伟
 * <p/>
 * Describe :
 * <p/>
 * Created : 2015 ; 18701 ; 2015/12/4.
 */
public class AppInfo {
    private String packageName;
    private String appName;
    private String virus;
    private int progress;

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public String getVirus() {
        return virus;
    }

    public void setVirus(String virus) {
        this.virus = virus;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
