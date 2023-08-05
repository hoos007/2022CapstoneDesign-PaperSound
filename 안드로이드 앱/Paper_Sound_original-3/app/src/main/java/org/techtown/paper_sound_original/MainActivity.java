package org.techtown.paper_sound_original;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    BookAdapter adapter;
    static TextView connDeviceText;
    String currBookId;
    Button mBtnConnect;
    static BluetoothService bluetoothService;
    static Handler mBluetoothHandler;

    static boolean isPlayActivityForeground = false;

    BookDBRequest bookDBRequest;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 로딩 호출
        Intent loadingintent = new Intent(this, LoadingActivity.class); // 로딩엑티비티 호출
        startActivity(loadingintent);

        // 앱 실행시 Background Service 실행
        Intent serviceintent = new Intent( this, MyService.class );
        startService(serviceintent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT


                    },
                    1);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH

                    },
                    1);
        }

        connDeviceText = findViewById(R.id.connDeviceText);

        recyclerView = findViewById(R.id.RecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BookAdapter();

        bookDBRequest = new BookDBRequest();

        jsonArray = bookDBRequest.getArray();
        try {
            for(int i=0; i<jsonArray.length(); i++)
            {
                JSONObject obj = jsonArray.getJSONObject(i);
                adapter.addItem(new Book(obj.getString("title"), obj.getString("writer"), obj.getString("id")));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnBookItemClickListener() {
            @Override
            public void onItemClick(BookAdapter.ViewHolder holder, View view, int position) {
                Book item = adapter.getItem(position);
                currBookId = item.bookid;

                Intent intent = new Intent(getApplicationContext(), playActivity.class);
                intent.putExtra("title", item.title);
                intent.putExtra("bookid",item.bookid);

                startActivity(intent);
            }
        });

        bluetoothService = new BluetoothService(getApplicationContext());

        mBtnConnect = findViewById(R.id.btnConnect);

        mBtnConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
//                bluetoothOn();
                if(bluetoothService.mBluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(!bluetoothService.mBluetoothAdapter.isEnabled()){
                    Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    listPairedDevices();
                }

            }
        });

        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == bluetoothService.BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }
//    void bluetoothOn() {
//        if(bluetoothService.mBluetoothAdapter == null) {
//            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
//        }
//        else {
//            if (!bluetoothService.mBluetoothAdapter.isEnabled()) {
//                Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    void listPairedDevices() {
        if (bluetoothService.mBluetoothAdapter.isEnabled()) {
            bluetoothService.mPairedDevices = bluetoothService.mBluetoothAdapter.getBondedDevices();

            if (bluetoothService.mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                bluetoothService.mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : bluetoothService.mPairedDevices) {
                    bluetoothService.mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = bluetoothService.mListPairedDevices.toArray(new CharSequence[bluetoothService.mListPairedDevices.size()]);
                bluetoothService.mListPairedDevices.toArray(new CharSequence[bluetoothService.mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        bluetoothService.connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
//            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}