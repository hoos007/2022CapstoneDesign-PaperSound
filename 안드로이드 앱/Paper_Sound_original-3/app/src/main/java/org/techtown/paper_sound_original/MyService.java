package org.techtown.paper_sound_original;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service{

    private static final String TAG = "MyService";

    public MyService(){

    }

    // 서비스가 호출되었을 때 한번만 호출
    @Override
    public void onCreate(){
        super.onCreate();
    }

    // 서비스가 호출될때마다 호출 (음악재생)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            return Service.START_STICKY; // 서비스가 종료되어도 자동으로 다시 실행
        } else {
            // intent가 null이 아닐경우 정상 작동중인거니 해야 할 일 시킴

        }
        return super.onStartCommand(intent, flags, startId);
    }

    // 서비스가 종료될 때
    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public IBinder onBind(Intent intent){
        throw new UnsupportedOperationException("Not yet Implemented"); //자동으로 작성되는 코드
    }
}