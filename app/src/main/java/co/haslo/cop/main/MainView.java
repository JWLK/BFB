package co.haslo.cop.main;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import co.haslo.cop.R;

public class MainView extends AppCompatActivity {

    /*Layout*/
    TextView mTvBluetoothStatus;

    TextView mTvReceiveData;
    TextView mTvReceiveData0;
    TextView mTvReceiveData1;
    TextView mTvReceiveData2;
    ImageView tvReceiveDataGraph;
    Button mBtnBluetoothConnect;
    Button mBtnBluetoothDisconnect;
    Button mBtnSendData;
    Button mBtnStopData;

    String mTvSendData="start";
    String mTvStopData="end";
    int numInt0=0;
    int numInt1=0;
    int i = 0;
    int msgLength = 0;


    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;
    List<String> mListPairedDevicesUUID;
    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        // Default Bluetooth Module Setting
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //main_view.xml Component Link
        mTvBluetoothStatus = (TextView)findViewById(R.id.tvBluetoothStatus);
        mTvReceiveData = (TextView)findViewById(R.id.tvReceiveData);
        mBtnBluetoothConnect = (Button)findViewById(R.id.btnBluetoothConnect);
        mBtnBluetoothDisconnect = (Button)findViewById(R.id.btnBluetoothDisconnect);
        mBtnSendData = (Button)findViewById(R.id.btnStartCOP);
        mBtnStopData = (Button)findViewById(R.id.btnStopCOP);


        final Bitmap bitmap = Bitmap.createBitmap(1000,1000, Bitmap.Config.ARGB_8888);
        //tvReceiveDataGraph.setImageBitmap(bitmap);


        /*onCLickListener*/
        mBtnBluetoothConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOn();
            }
        });

        mBtnBluetoothDisconnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOff();
            }
        });

        mBtnSendData.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mThreadConnectedBluetooth != null) {
                    mThreadConnectedBluetooth.write(mTvSendData);
                }
                else {
                    Toast.makeText(getApplicationContext(), "블루투스 장치와 연결이 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mBtnStopData.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mThreadConnectedBluetooth != null) {
                    mThreadConnectedBluetooth.write(mTvStopData);
                }
                else {
                    Toast.makeText(getApplicationContext(), "블루투스 장치와 연결이 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });


        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){

                StringBuilder mMessage = new StringBuilder();

                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.TRANSPARENT);

                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStrokeWidth(5f);
                paint.setStyle(Paint.Style.STROKE);

                Path path = new Path();
                path.moveTo(500,500);

                if(msg.what == BT_MESSAGE_READ){

                    byte[] readBuffer = (byte[]) msg.obj;
                    String tempMessage = new String(readBuffer, 0, msg.arg1);
                    mMessage.append(tempMessage);
                    //Log.d("originMsg", tempMessage);


                    int endOfLineIndex = mMessage.indexOf("\n");
                        if(endOfLineIndex > 0) {
                            String sbprint = mMessage.substring(0, endOfLineIndex);
                            Log.d("EoiMsg", sbprint);
                            mTvReceiveData.setText(sbprint);


//                            if(sbprint.equals("normal 100")){
//                                mTvReceiveData0.setVisibility(View.VISIBLE);
//                                mTvReceiveData1.setVisibility(View.GONE);
//                            }
//                            else if(sbprint.equals("warning 200")){
//                                mTvReceiveData0.setVisibility(View.GONE);
//                                mTvReceiveData1.setVisibility(View.VISIBLE);
//                            }
                            /*/else if(sbprint == "300"){
                                mTvReceiveData0.setVisibility(View.GONE);
                                mTvReceiveData1.setVisibility(View.GONE);
                                mTvReceiveData2.setVisibility(View.VISIBLE);
                                //*/

                        }

                    }

                    /*/
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        msgLength = readMessage.length();
                        Log.d("Msg", readMessage);
                        //canvas.drawPath(path,paint);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //*/

                    /*/
                    byte[] readBuffer = (byte[]) msg.obj;
                    String tempMessage = new String(readBuffer, 0, msg.arg1);
                    mMessage.append(tempMessage);

                    int endOfLineIndex = mMessage.indexOf("\n");
                    if(endOfLineIndex > 0) {
                        String sbprint = mMessage.substring(0, endOfLineIndex);
                        Log.d("originMsg", sbprint);
                        if(sbprint.contains(",")){
                            sbprint = sbprint.replaceAll(" ", "");
                            Log.d("msgLine", sbprint);
                            String[] RealData = sbprint.split(",");

                            if(!RealData[0].equals("") && !RealData[1].equals("")){
                                numInt0 = Integer.parseInt(RealData[0]);
                                //numInt1 = Integer.parseInt(RealData[1]);
                                Log.d("msgData00", ""+numInt0);
                                Log.d("msgData01", ""+numInt1);
                            }
                            path.lineTo(500+numInt0,500+numInt1);

                        }
                        mTvReceiveData.setText(sbprint);
                    }
                    mTvReceiveData.setText(i+readMessage+"\r\n");
                    Log.d("rsMsg",""+i);
                    canvas.drawPath(path, paint);
                    tvReceiveDataGraph.postInvalidate();

                    i++;

                    //*/
                }
        };
    }

    void bluetoothOn() {
        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        }
        else {
            if(mBluetoothAdapter.isEnabled()) {
                // Toast.makeText(getApplicationContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
                mTvBluetoothStatus.setText("활성화");
                listPairedDevices();
            }
            else {
                mTvBluetoothStatus.setText("비활성화");
                Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
            }
        }
    }

    void bluetoothOff() {
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
            mTvBluetoothStatus.setText("비활성화");
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스가 활성화 되었습니다.", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("활성화");
                    listPairedDevices();
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("비활성화");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>();
                mListPairedDevicesUUID = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevicesUUID.add((device.getUuids());
                    //mListPairedDevices.add(device.getName() + "\n" + Arrays.toString(device.getUuids()));

                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
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
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedBluetoothThread extends Thread {                     //블루투스 연결 쓰레드 클래스
        private final BluetoothSocket mmSocket;                                 //블루투스 소켓
        private final InputStream mmInStream;                                   //읽기
        private final OutputStream mmOutStream;                                 //쓰기

        public ConnectedBluetoothThread(BluetoothSocket socket) {               //클래스 메인
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();                //블루투스 읽기 스트림 연결
                tmpOut = socket.getOutputStream();              //블루투스 쓰기 스트림 연결
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
            mTvBluetoothStatus.setText("장치 연결됨");

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {                                    //쓰레드 실행 메인문, Thresd.start() 할 경우 run() 부분이 실행됨.
            byte[] buffer = new byte[1024];                    //송수신 1024 바이트 버퍼 생성(한번에 최대 1kb까지 송수신 가능)
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();           //현재 읽어야할 바이트수를 반환함, 0바이트면 읽을게 없고, 0바이트 이상이면 읽을게 있다는 소리
                    if (bytes != 0) {
                        SystemClock.sleep(100);          //0.1초 간격으로 읽기 실행
                        bytes = mmInStream.available();         //0.1초 사이에 통신이 들어오면 바이트 크기가 갱신될 수 있기에 재수행
                        bytes = mmInStream.read(buffer, 0, bytes);      //위에 생성한 바이트 버퍼에 읽기를 수행
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();  //메세지 풀로부터 메세지 객체를 리턴
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {         //블루투스 통한 데이터 전송, str을 보냄
            byte[] bytes = str.getBytes();      //데이터를 보내려면 byte array화 해서 변환 해야함
            try {
                mmOutStream.write(bytes);       //전송
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {   //cancel이라기 보단 close인데, 자바에서의 close는 스트림에서 스트림을 종료해주기 위한 메소드로 사용하기에는 일반적으로 잘 사용하지 않음.

            try {
                mmSocket.close();   //이거처럼, 소켓을 닫거나, 통신 스트림을 종료하거나 등의 용도로 close() 메소드를 범용적으로 사용함
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
