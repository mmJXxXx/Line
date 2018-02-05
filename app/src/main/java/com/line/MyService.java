package com.line;
/**
 服务类
 */
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import org.litepal.LitePal;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service {
    BufferedWriter writer = null;
    BufferedReader reader = null;
    static String valuebuff=null;
    NotificationManager manager;
    Notification notification;
    static int checknum=0;
//    SimpleDateFormat  formatter  =  new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");
//    Date curDate = new Date(System.currentTimeMillis());
    static String msgdatanum="0";
    static Socket socket;
    private  MessageBinder mBinder = new MessageBinder();
    public static int getnumback(){
        return checknum;
    }
    public static void setnumback(){
        checknum=1;
    }


    class MessageBinder extends Binder{
      public void startmsg(){

      }
      public String msgback(){
          return "";
      }
    }
    public MyService() {
    }
    @Override//onCreate 服务创建时调用
    public void onCreate(){
        Intent intent = new Intent(MyService.this,mainactivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new NotificationCompat.Builder(MyService.this)
                .setContentTitle("Line收到新消息")
                .setContentText("点击查看")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round))
                .setContentIntent(pi)
                .setVibrate(new long[]{0,1000,1000})
                .setAutoCancel(true)
                .build();

        super.onCreate();
    }
    @SuppressLint("StaticFieldLeak")
    @Override//onCreate 服务启动时调用
    public int onStartCommand(Intent intent,int flags,int startId){
//        adapter = new MsgAdapter( msgList );
//        msgRecyclerView.setAdapter( adapter );
//        new Thread( new Runnable() {
//            @Override
//            public void run() {
//
//
//                //stopSelf();
//            }
//        } ).start();
        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    socket = new Socket("192.168.1.4", 7400);
                    writer = new BufferedWriter( new OutputStreamWriter(
                            socket.getOutputStream(), "utf-8" ) );
                    reader = new BufferedReader( new InputStreamReader(
                            socket.getInputStream(), "utf-8" ) );
                    publishProgress( "@Sucess" ); // 链接成功
                } catch (IOException e) {
                    publishProgress( "@Failure" ); // 链接失败
                    Toast.makeText(MyService.this, "与服务器连接失败", Toast.LENGTH_LONG).show();
                    stopSelf();
                    e.printStackTrace();
                }
                // 监听服务器发来的数据
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {//死循环
                        publishProgress( line );
                    }
                } catch (IOException e) {
                    Toast.makeText(MyService.this, "接收数据失败！",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onProgressUpdate(String... values){
                switch (values[0]) {
                    case "@Sucess":
                        Toast.makeText(MyService.this, "建立连接成功！",
                                Toast.LENGTH_SHORT).show();
                        // listAdapter.add("建立连接成功！");
                        break;
                    case "@Failure":
                        Toast.makeText(MyService.this, "建立连接失败！",
                                Toast.LENGTH_SHORT).show();
                        // listAdapter.add("建立连接失败！");
                        break;
                    default:
//                        Toast.makeText(mainactivity.this, "收到数据！",
//                                Toast.LENGTH_SHORT).show();
                        //  listAdapter.add("别人说：" + values[0]);
                        //String msghead = mainactivity.msghead;
                        if (values[0].substring(0, mainactivity.msghead.length()).equals(mainactivity.msghead)) {
                            valuebuff = values[0].substring(mainactivity.msghead.length());
                           // String   str   =   formatter.format(curDate);
                            setnumback();
                        if (manager != null) {
                            manager.notify(0, notification);
                        }
                            Message message = new Message();
                            message.what = 1;
                            mainactivity.hander.sendMessage(message);
                        }
                        break;
                }
            }
        }.execute();
        return super.onStartCommand(intent,flags,startId);
    }
    @Override//服务销毁时调用
    public void onDestroy(){
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
      return mBinder;
    }
}
