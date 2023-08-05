package org.techtown.paper_sound_original;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {

    static BluetoothAdapter mBluetoothAdapter;
    static Set<BluetoothDevice> mPairedDevices;
    static List<String> mListPairedDevices;

    static ConnectedBluetoothThread mThreadConnectedBluetooth;
    static BluetoothDevice mBluetoothDevice;
    static BluetoothSocket mBluetoothSocket;

    Handler toastHandler;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Context appContext;

    public BluetoothService(Context appContext) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.appContext = appContext;
        toastHandler =new Handler(Looper.getMainLooper());
    }

    @Override
    public void onCreate() {
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return null;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            MainActivity.mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
            Toast.makeText(appContext, selectedDeviceName+" 장치와 연결되었습니다.", Toast.LENGTH_LONG).show();
            MainActivity.connDeviceText.setText(selectedDeviceName);
        } catch (Exception e) {
            Toast.makeText(appContext, "블루투스 연결을 실패했습니다.", Toast.LENGTH_LONG).show();
            MainActivity.connDeviceText.setText("");
        }
    }

    public boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(appContext, "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                MainActivity.connDeviceText.setText("");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        if(MainActivity.isPlayActivityForeground == true)
                        playActivity.mBluetoothHandlerPlay.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                    if(!isConnected(mBluetoothDevice))
                    {
                        toastHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(appContext, "장치 연결이 끊어졌습니다.", Toast.LENGTH_LONG).show();
                                MainActivity.connDeviceText.setText("");
                            }
                        });
                        break;
                    }
                    if(!mBluetoothAdapter.isEnabled())
                    {
                        mBluetoothSocket.close();
                        toastHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(appContext, "장치 연결이 끊어졌습니다.", Toast.LENGTH_LONG).show();
                                MainActivity.connDeviceText.setText("");
                            }
                        });
                        break;
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}