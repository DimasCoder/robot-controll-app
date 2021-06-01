package com.example.video_calls;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Connector.ConnectorPkg;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements Connector.IConnect {

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ListView devicesList;

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> blArrayAdapter;

    private ConnectedThread thread;
    private BluetoothSocket socket = null;

    public static final Integer RecordAudioRequestCode = 1;
    private Connector connector;
    private FrameLayout videoFrame;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private ImageView micButton;
    private Button buttonF, buttonS, buttonL, buttonR, buttonB;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetoothTurnOn:
                if (!bluetoothAdapter.isEnabled()) {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, 0);
                    showMessage("Включено Bluetooth");
                } else {
                    showMessage("Bluetooth вже включений");
                }
                break;
            case R.id.bluetoothTurnOff:
                bluetoothAdapter.disable();
                showMessage("Виключено Bluetooth");
                break;
            case R.id.showPairedDevices:
                showPairedDevices();
                break;
            case R.id.previewVideo:
                previewVideo(videoFrame);
                break;
            case R.id.connect:
                connectToCall();
                break;
            case R.id.disconnectCall:
                disconnect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        blArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        devicesList = (ListView) findViewById(R.id.devices_list_view);
        devicesList.setAdapter(blArrayAdapter);
        devicesList.setOnItemClickListener(onDeviceClick);

        ConnectorPkg.setApplicationUIContext(this);

        ConnectorPkg.initialize();
        videoFrame = (FrameLayout) findViewById(R.id.videoFrame);
        editText = findViewById(R.id.text);
        micButton = findViewById(R.id.button);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        buttonF = findViewById(R.id.buttonF);
        buttonS = findViewById(R.id.buttonS);
        buttonR = findViewById(R.id.buttonR);
        buttonL = findViewById(R.id.buttonL);
        buttonB = findViewById(R.id.buttonB);
        RelativeLayout rl = findViewById(R.id.rl);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        buttonF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.sendCommand("F");
            }
        });

        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.sendCommand("B");
            }
        });

        buttonR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.sendCommand("R");
            }
        });

        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thread != null) {
                    thread.sendCommand("L");
                }
            }
        });

        buttonS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thread != null) {
                    thread.sendCommand("S");
                }
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_btn_speak_now);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(data.get(0));

                if (data.get(0).equals("вперед")) {
                    if (thread != null) {
                        thread.sendCommand("1");
                    }
                } else if (data.get(0).equals("назад")) {

                    if (thread != null) {
                        thread.sendCommand("2");
                    }
                } else if (data.get(0).equals("направо")) {
                    if (thread != null) {
                        thread.sendCommand("3");
                    }

                } else if (data.get(0).equals("наліво")) {
                    if (thread != null) {
                        thread.sendCommand("4");
                    }

                } else if (data.get(0).equals("стоп")) {
                    if (thread != null) {
                        thread.sendCommand("5");
                    }
                }
            }


            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        micButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    micButton.setImageResource(R.drawable.ic_btn_speak_now);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });

    }


    private void showPairedDevices() {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (bluetoothAdapter.isEnabled()) {
            blArrayAdapter.clear();
            for (BluetoothDevice device : devices)
                blArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        } else
            showMessage("Потрібно включити Bluetooth");
    }

    private AdapterView.OnItemClickListener onDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!bluetoothAdapter.isEnabled()) {
                showMessage("Увімкніть Bluetooth");
                return;
            }
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);

            new Thread() {
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

                    try {
                        socket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                    }
                    try {
                        socket.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!fail) {
                        thread = new ConnectedThread(socket);
                        thread.start();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }



    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }


    public void previewVideo(View v) {
        connector = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 15, "warning info@VidyoClient info@VidyoConnector", "", 0);
        connector.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
        connector.disableAudioForAll(false, "1");
    }

    public void connectToCall(String host, String token, String username, String roomName) {
        connector.connect(host, token, username, roomName, this);
    }

    public void disconnect() {
        connector.selectLocalCamera(null);
        connector.selectLocalMicrophone(null);
        connector.selectLocalSpeaker(null);
        connector.disconnect();
    }

    public void onSuccess() {
    }

    public void onFailure(Connector.ConnectorFailReason reason) {
        System.out.println(reason);
    }

    public void onDisconnected(Connector.ConnectorDisconnectReason reason) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                showMessage("Permission Granted");
        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
