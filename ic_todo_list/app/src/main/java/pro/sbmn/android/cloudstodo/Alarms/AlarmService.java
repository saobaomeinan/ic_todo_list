package pro.sbmn.android.cloudstodo.Alarms;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import java.util.Date;

import pro.sbmn.android.cloudstodo.Activity.MainActivity;
import pro.sbmn.android.cloudstodo.R;

public class AlarmService extends Service {
    //记录alertdialog出现次数
    public int number = 0;
    AlarmManager manager;
    PendingIntent pi;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmService.this);
                builder.setTitle("待办提醒！");
                builder.setMessage("您设置的时间到了！" + (number - 1));
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        number = 0;
                        manager.cancel(pi);
                        stopSelf();
                    }
                });
                final AlertDialog dialog = builder.create();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                } else {
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                }
                dialog.show();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("res", "服务开启执行了" + number);
        if (number != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("res", "开始：" + new Date().toString());
                    mHandler.sendEmptyMessage(1);
                }
            }).start();
        }
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = intent.getLongExtra("Time", 0);
        Intent it = new Intent(this, AlarmReceiver.class);
        pi = PendingIntent.getBroadcast(this, 0, it, PendingIntent.FLAG_IMMUTABLE);
        manager.set(AlarmManager.RTC_WAKEUP, time, pi);
        number++;
        Log.d("res", "服务开启快结束了" + number);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.cancel(pi);
    }
}

