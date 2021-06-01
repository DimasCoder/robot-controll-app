//package com.example.video_calls;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCallback;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattService;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.BluetoothProfile;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Handler;
//import androidx.appcompat.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.example.video_calls.LeDeviceListAdapter;
//import com.example.video_calls.R;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static android.widget.Toast.makeText;
//
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int REQUEST_ENABLE_BT = 1;
//    private LeDeviceListAdapter mLeDeviceListAdapter;
//    private BluetoothAdapter mBluetoothAdapter;
//    private static final long SCAN_PERIOD = 10000;
//    private boolean mScanning;
//    private Handler mHandler;
//    private BluetoothGatt mGatt = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        final ArrayList<BluetoothDevice> availableBT = new ArrayList<>();
//        mLeDeviceListAdapter = new LeDeviceListAdapter(this, availableBT);
//        final ListView listView_available = (ListView) findViewById(R.id.listview2);
//
//
//        mHandler = new Handler();
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            makeText(this, "BLE Not Supported", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        listView_available.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mGatt = null;
//                connectToDevice(mLeDeviceListAdapter.getItem(position));
//            }
//        });
//
//        Button scan = (Button) findViewById(R.id.scan);
//        scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast toast = makeText(getApplicationContext(), "Scanning...", Toast.LENGTH_SHORT);
//                toast.show();
//
//                mLeDeviceListAdapter.clear();
//                scanLeDevice(true);
//                listView_available.setAdapter(mLeDeviceListAdapter);
//
//            }
//        });
//
//        final Button dc = (Button) findViewById(R.id.dc);
//        dc.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (mGatt!=null){
//                    mGatt.disconnect();
//                    mGatt = null;
//                    Toast tdc = makeText(getApplicationContext(),"Disconnected From Device", Toast.LENGTH_SHORT);
//                    tdc.show();
//                }else {
//                    Toast tndc = makeText(getApplicationContext(),"No Device Is Connected", Toast.LENGTH_SHORT);
//                    tndc.show();
//                }
//                mGatt = null;
//            }
//        });
//
//        Button forward = (Button) findViewById(R.id.forward);
//        forward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mGatt != null) {
//
//                    BluetoothGattService service = mGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
//                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
//
//                    characteristic.setValue("f".getBytes());
//                    mGatt.writeCharacteristic(characteristic);
//                }
//            }
//        });
//
//        Button back = (Button) findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mGatt != null) {
//
//                    BluetoothGattService service = mGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
//                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
//
//                    characteristic.setValue("b".getBytes());
//                    mGatt.writeCharacteristic(characteristic);
//                }
//            }
//        });
//
//        Button left = (Button) findViewById(R.id.left);
//        left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mGatt != null) {
//
//                    BluetoothGattService service = mGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
//                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
//
//                    characteristic.setValue("l".getBytes());
//                    mGatt.writeCharacteristic(characteristic);
//                }
//            }
//        });
//
//        Button right = (Button) findViewById(R.id.right);
//        right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mGatt != null) {
//
//                    BluetoothGattService service = mGatt.getService(UUID.fromString("0000ffe0-1000-8000-00805f9b34fb"));
//                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
//
//                    characteristic.setValue("r".getBytes());
//                    mGatt.writeCharacteristic(characteristic);
//                }
//            }
//        });
//
//        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//    }
//
//
//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning=false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                }
//            },SCAN_PERIOD);
//
//            mScanning = true;
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
//
//        }else {
//            mScanning = false;
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//
//        }
//    }
//
//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mLeDeviceListAdapter.addDevice(device);
//                    mLeDeviceListAdapter.notifyDataSetChanged();
//
//                }
//            });
//        }
//    };
//
//    public void connectToDevice(BluetoothDevice device) {
//        if (mGatt == null) {
//            scanLeDevice(false);
//            mGatt = device.connectGatt(this, false, gattCallback);
//        }
//    }
//
//    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            Log.i("onConnectionStateChange", "Status: " + status);
//            switch (newState) {
//                case BluetoothProfile.STATE_CONNECTED:
//                    Log.i("gattCallback", "STATE_CONNECTED");
//                    gatt.discoverServices();
//                    break;
//                case BluetoothProfile.STATE_DISCONNECTED:
//                    Log.e("gattCallback", "STATE_DISCONNECTED");
//                    break;
//                default:
//                    Log.e("gattCallback", "STATE_OTHER");
//            }
//        }
//
//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//
//            List<BluetoothGattService> services = mGatt.getServices();
//            Log.i("onServicesDiscovered", services.toString());
//            gatt.readCharacteristic(services.get(0).getCharacteristics().get(0));
//        }
//
//        @Override
//        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
//            Log.i("onCharacteristicRead", characteristic.toString());
//        }
//    };
//
//
//}
//
//
package com.example.video_calls;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity1 extends AppCompatActivity {
    //widgets
    Button btnPaired, btnOn;
    ListView devicelist;

    public final String TAG = getClass().getSimpleName();

    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    private BluetoothSocket mBluetoothSocket;
    private OutputStream mOutputSteam;
    private static MainActivity1 instance;
    private boolean isEnabledW = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        //Calling widgets
        btnPaired = (Button) findViewById(R.id.button);
        btnOn = (Button) findViewById(R.id.buttonOn);
        devicelist = (ListView) findViewById(R.id.listView);
        btnOn.setOnClickListener(clickListener);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {
            //Show a message. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        } else if (!myBluetooth.isEnabled()) {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String command = "";

            if(v.equals(btnOn)){
                isEnabledW = !isEnabledW;
                command = isEnabledW ? "1" : "2";
                Log.d(TAG, "onClick: isEnabledW: " + isEnabledW);
           }
            setMessage(command);
            Log.d(TAG, "End of send: " + command);

        }
    };

    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found" + "\n" + "or Bluetooth is turned off", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    public void setMessage(String command) {
        byte[] buffer = command.getBytes();

            try {
                mOutputSteam.write(buffer);
                showToastMessage("Succesfull sended");
            } catch (IOException e) {
                showToastMessage("Can't to send message");
                e.printStackTrace();
            }
    }

    private void startConnection(BluetoothDevice device) {
        if (device != null) {
            try {
                Method method = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                mBluetoothSocket = (BluetoothSocket) method.invoke(device, 1);
                mBluetoothSocket.connect();

                mOutputSteam = mBluetoothSocket.getOutputStream();

                showToastMessage("Connected successfully");
            } catch (Exception e) {
                showToastMessage("Can't connect");
                e.printStackTrace();
            }
        }
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
            List<BluetoothDevice> blList = new ArrayList<>(pairedDevices);
            BluetoothDevice device = blList.get(position);

            startConnection(device);
//            Intent i = new Intent(MainActivity.this, VideoActivity.class);
//
//            startActivity(i);
        }
    };


    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static MainActivity1 getInstance() {
        return instance;
    }
}