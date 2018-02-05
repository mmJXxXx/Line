package com.line;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.sax.EndElementListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
    APP主活动
 */
public class mainactivity extends AppCompatActivity {
    private List<Msg> msgList = new ArrayList<>();
    private MsgAdapter adapter;
    private RecyclerView msgRecyclerView;
    private EditText inputText;
    static String msghead = "***";
    public static final int UPDATE_TEXT=1;
  //  static String sn = android.os.Build.SERIAL;
    BufferedWriter writer = null;
    static Handler hander;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.resetnum:
                final EditText et = new EditText(mainactivity.this);
                et.setHint("重新输入密码：");
                new AlertDialog.Builder(mainactivity.this)
                        .setView(et)
                        //“确认”button
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                msghead = "***" + et.getText().toString();
                                if (msghead.equals("***")) {
                                    msghead = "***";
                                    Toast.makeText(mainactivity.this, "已默认", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                } else {
                                    Toast.makeText(mainactivity.this, "已重置", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        })
                        //“取消”button
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                msghead = "***";
                                Toast.makeText(mainactivity.this, "已默认", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();//选择后关闭！

                            }
                        })
                        .create().show();
            break;
            case R.id.remove_item:
                Toast.makeText(mainactivity.this,"Click Remove",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String tempData=inputText.getText().toString();

    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.layout_main );
        if(savedInstanceState!=null){
            String tempData=savedInstanceState.getString("data_key");
            inputText.setText(tempData);
        }
        inputText = (EditText) findViewById( R.id.input_text );
        Button send = (Button) findViewById( R.id.send );
        msgRecyclerView = (RecyclerView) findViewById( R.id.msg_recycler_view );
        LinearLayoutManager layoutManager = new LinearLayoutManager( this );
        msgRecyclerView.setLayoutManager( layoutManager );
        adapter = new MsgAdapter( msgList );
        msgRecyclerView.setAdapter( adapter );
        Intent startIntent = new Intent(mainactivity.this,MyService.class);
        startService(startIntent);
        hander = new Handler(){
            public void handleMessage(Message msg){
                switch(msg.what){
                    case UPDATE_TEXT:
                            Msg msgr = new Msg(MyService.valuebuff, Msg.TYPE_RECEIVED);
                            msgList.add(msgr);
                            adapter.notifyItemInserted(msgList.size() - 1);
                            msgRecyclerView.scrollToPosition(msgList.size() - 1);
                        break;
                    default:
                        break;

                }
            }
        };
        if(MyService.getnumback()==0) {
            final EditText et = new EditText(mainactivity.this);
            et.setHint("请输入密码：");
            new AlertDialog.Builder(mainactivity.this)
                    .setView(et)
                    //“确认”button
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            msghead = "***" + et.getText().toString();
                            if (msghead.equals("***")) {
                                msghead = "***";
                                Toast.makeText(mainactivity.this, "已默认", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            } else {
                                Toast.makeText(mainactivity.this, "连接成功！", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    })
                    //“取消”button
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            msghead = "***";
                            Toast.makeText(mainactivity.this, "已默认", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();//选择后关闭！

                        }
                    })
                    .create().show();
                    MyService.setnumback();
        }
        send.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            writer = new BufferedWriter( new OutputStreamWriter(
                                   MyService.socket.getOutputStream(), "utf-8" ) );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final String content;
                        content = inputText.getText().toString();
                        if(!content.equals( "" )){
                            Msg msgp = new Msg( content, Msg.TYPE_SENT );
                            msgList.add( msgp );
                            adapter.notifyItemInserted( msgList.size() - 1 );
                            msgRecyclerView.scrollToPosition( msgList.size() - 1 );
                            inputText.getText().clear();
                            new Thread(new Runnable(){
                                @Override
                                public void run(){
                                    try {
                                        String sendData =msghead+content.replace("\n","");
                                        writer.write(sendData + "\n");//必须加上换行
                                        writer.flush();
                                    } catch (IOException e) {
                                        Toast.makeText(mainactivity.this, "发送失败！", Toast.LENGTH_SHORT)
                                                .show();
                                        e.printStackTrace();
                                    }

                            }
                            }).start();Toast.makeText( mainactivity.this, "已发送", Toast.LENGTH_SHORT ).show();
                        }else {
                            Toast.makeText( mainactivity.this, "发送数据不能为空", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );

    }
}






