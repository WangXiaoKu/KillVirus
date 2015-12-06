package androidstudio.killvirus;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /**
     * 扫描开始
     */
    private static final int BEGIN = 0;
    /**
     * 扫描中
     */
    private static final int SCANING = 1;
    /**
     * 扫描结束
     */
    private static final int FINISH = 2;
    private final String TAG = "MainActivity";
    private ProgressBar pb_kill_virus;
    private TextView tv_kill_virus;
    private int progress = 0;
    private int progressMax = 0;
    private int program_number = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BEGIN:
                    iv_kill_virus.startAnimation(rotateAnimation);
                    pb_kill_virus.setMax(progressMax);
                    pb_kill_virus.setProgress(progress);
                    break;
                case SCANING:
                    pb_kill_virus.setMax(progressMax);
                    pb_kill_virus.setProgress(progress);

                    TextView textView = new TextView(MainActivity.this);
                    AppInfo appInfo = (AppInfo) msg.obj;
                    if (appInfo.getVirus().equals("未发现病毒")) {
                        textView.setTextColor(Color.WHITE);
                        textView.setText(appInfo.getAppName());
                        program_number++;
                        tv_kill_virus.setText("正在杀毒...已扫描" + program_number + "个程序");
                    } else {
                        textView.setTextColor(Color.RED);
                        textView.setText(appInfo.getAppName());
                    }
                    ll_text.addView(textView);
                    sv_kill_virus.fullScroll(sv_kill_virus.FOCUS_DOWN);
                    break;
                case FINISH:
                    tv_kill_virus.setText("杀毒完成");
                    iv_kill_virus.clearAnimation();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private Message message;
    private ImageView iv_kill_virus;
    private RotateAnimation rotateAnimation;
    private LinearLayout ll_text;
    private ScrollView sv_kill_virus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        copyDB("antivirus.db");

        new Thread(new Runnable() {
            @Override
            public void run() {
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

                progressMax = installedPackages.size();
                SystemClock.sleep(2000);
                message = new Message();
                message.what = BEGIN;
                handler.sendMessage(message);
                List<AppInfo> appLists = new ArrayList<AppInfo>();
                for (PackageInfo packageInfo : installedPackages) {
                    AppInfo appInfo = new AppInfo();
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();//获取已经安装的所有APP的名字
                    appInfo.setAppName(appName);
//                    Log.i(TAG, appName);

                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    String appMD5 = MD5Utils.getAppMD5(sourceDir);
                    String checkVirus = VirusDB.checkVirus(appMD5);
                    //如果返回的信息为空，则说明没有病毒
                    if (checkVirus == null) {
                        appInfo.setVirus("未发现病毒");
                    } else {
                        appInfo.setVirus(checkVirus);
                    }

                    String packageName = packageInfo.applicationInfo.packageName;
                    appInfo.setPackageName(packageName);
//                    Log.i(TAG, packageName);

                    progress++;
                    appInfo.setProgress(progress);

                    appLists.add(appInfo);

                    SystemClock.sleep(50);

                    message = Message.obtain();
                    message.what = SCANING;
                    message.obj = appInfo;
                    handler.sendMessage(message);
                }

                message = Message.obtain();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 拷贝数据库
     *
     * @param dbName 数据库的名字
     */
    private void copyDB(final String dbName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(getFilesDir(), dbName);
                if (file.exists() && file.length() > 0) {
                    Log.i(TAG, "数据库已存在，无需拷贝");
                    return;
                }
                try {
                    InputStream is = getAssets().open(dbName);
                    FileOutputStream fos = openFileOutput(dbName, MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                    Log.i(TAG, "数据库拷贝成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        setContentView(R.layout.activity_main);

        pb_kill_virus = (ProgressBar) findViewById(R.id.pb_kill_virus);
        tv_kill_virus = (TextView) findViewById(R.id.tv_kill_virus);
        iv_kill_virus = (ImageView) findViewById(R.id.iv_kill_virus);
        ll_text = (LinearLayout) findViewById(R.id.ll_text);
        sv_kill_virus = (ScrollView)findViewById(R.id.sv_kill_virus);
        /**
         * 设置旋转动画
         * 第一个参数表示从多少度角开始旋转
         * 第二个参数表示旋转多少角度
         * 第三个参数表示相对于谁旋转
         * 第四个参数表示旋转点
         * 第五个参数也表示相对于谁旋转
         * 第六个参数也表示旋转点
         */
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(3600);//设置动画的持续时间
        rotateAnimation.setRepeatCount(Animation.INFINITE);//设置循环模式为无限循环

    }
}
