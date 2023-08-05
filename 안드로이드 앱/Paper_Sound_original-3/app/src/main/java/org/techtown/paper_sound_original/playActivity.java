package org.techtown.paper_sound_original;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;


public class playActivity extends AppCompatActivity {
    TextView title;
    Button pause;

    String bookid;

    int position;
    boolean isPaused;

    String[] curr;
    String[] prev;
    String url;

    MediaPlayer mediaPlayer;
    WifiManager.WifiLock wifiLock;

    static Handler mBluetoothHandlerPlay; // 블루투스로 전송받은 내용을 받는 핸들러 선언
    final static int BT_MESSAGE_READ = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        MainActivity.isPlayActivityForeground = true;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        wifiLock.acquire();

        position = 0;
        isPaused = false;

        prev = new String[]{"null", "null"};

        pause = findViewById(R.id.button);

        title = findViewById(R.id.textView3);

        Intent intent = getIntent();

        title.setText(intent.getStringExtra("title"));
        bookid = intent.getStringExtra("bookid");

        mBluetoothHandlerPlay = new Handler(){          //핸들러
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //여기서 받은 코드와 책번호를 조합해 서버에 요청한다.
                    if(readMessage.substring(0,3).equals("qr:"))
                    {
                        String result1 = readMessage.substring(3);
                        curr = result1.split("-|\\n");              // 수정 필요함

                        if(!(prev[0].equals(curr[0]) && prev[1].equals(curr[1])))
                        {
                            url = "<서버주소>/FileDown/sound?bookid="+bookid+"&chapter="+Integer.parseInt(curr[0])+"&track="+Integer.parseInt(curr[1]);
                            title.setText(Integer.parseInt(curr[0])+"챕터 - "+Integer.parseInt(curr[1])+"트랙");
                            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                            }
                            soundPlay();
                            pause.setText("pause");
                            prev = curr;
                        }
                    }
                }
            }
        };

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pause.getText().equals("pause"))
                {
                    if(mediaPlayer != null && mediaPlayer.isPlaying()){
                        position = mediaPlayer.getCurrentPosition();
                        mediaPlayer.pause();
                        isPaused = true;
                        pause.setText("play");
                    }
                }
                else if(pause.getText().equals("play"))
                {
                    if(mediaPlayer != null && !mediaPlayer.isPlaying() && isPaused == true)
                    {
                        mediaPlayer.start();
                        mediaPlayer.seekTo(position);
                        isPaused = false;
                        pause.setText("pause");
                    }
                }
            }
        });
    }

    void soundPlay()
    {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.isPlayActivityForeground = false;
        mediaPlayer.release();
        mediaPlayer = null;
        wifiLock.release();
    }
}