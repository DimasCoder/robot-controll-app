//package com.example.video_calls;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.speech.RecognitionListener;
//import android.speech.RecognizerIntent;
//import android.speech.SpeechRecognizer;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.vidyo.VidyoClient.Connector.Connector;
//import com.vidyo.VidyoClient.Connector.ConnectorPkg;
//
//import java.util.ArrayList;
//import java.util.Locale;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class VideoActivity extends AppCompatActivity implements Connector.IConnect, View.OnClickListener {
//
//    public static final Integer RecordAudioRequestCode = 1;
//    private Connector connector;
//    private FrameLayout videoFrame;
//    private SpeechRecognizer speechRecognizer;
//    private EditText editText;
//    private ImageView micButton;
//    private Button buttonF, buttonS, buttonL, buttonR, buttonB, previewVideo, connect, disconnect;
//    String host, username, roomID, token = "";
//
//    String usernameValid = "^[a-zA-Z0-9]";
//
//    private boolean isEnabledW = false;
//
//    private Timer mTimer;
//    private TimerTask timerTask;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video);
//
//        ConnectorPkg.setApplicationUIContext(this);
//        ConnectorPkg.initialize();
//        videoFrame = (FrameLayout) findViewById(R.id.videoFrame);
//        editText = findViewById(R.id.text);
//        micButton = findViewById(R.id.button);
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        buttonF = findViewById(R.id.buttonF);
//        buttonS = findViewById(R.id.buttonS);
//        buttonR = findViewById(R.id.buttonR);
//        buttonL = findViewById(R.id.buttonL);
//        buttonB = findViewById(R.id.buttonB);
//
//        previewVideo = findViewById(R.id.previewVideo);
//        connect = findViewById(R.id.connect);
//        disconnect = findViewById(R.id.disconnect);
//
//        videoFrame.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                connector.cycleCamera();
//            }
//        });
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            checkPermission();
//        }
//
//        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//
//        buttonF.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mConnectedThread != null) { //First check to make sure thread created
//                    mConnectedThread.write("1");
//                }
//            }
//        });
//
//        buttonB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mConnectedThread != null) { //First check to make sure thread created
//                    mConnectedThread.write("2");
//                }
//            }
//        });
//
//        buttonR.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mConnectedThread != null) { //First check to make sure thread created
//                    mConnectedThread.write("3");
//                }
//            }
//        });
//
//        buttonL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mConnectedThread != null) { //First check to make sure thread created
//                    mConnectedThread.write("4");
//                }
//            }
//        });
//
//        buttonS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mConnectedThread != null) { //First check to make sure thread created
//                    mConnectedThread.write("5");
//                }
//            }
//        });
//
//        speechRecognizer.setRecognitionListener(new RecognitionListener() {
//            @Override
//            public void onReadyForSpeech(Bundle bundle) {
//
//            }
//
//            @Override
//            public void onBeginningOfSpeech() {
//                editText.setText("");
//                editText.setHint("Listening...");
//            }
//
//            @Override
//            public void onRmsChanged(float v) {
//
//            }
//
//            @Override
//            public void onBufferReceived(byte[] bytes) {
//
//            }
//
//            @Override
//            public void onEndOfSpeech() {
//
//            }
//
//            @Override
//            public void onError(int i) {
//
//            }
//
//            @Override
//            public void onResults(Bundle bundle) {
//                micButton.setImageResource(R.drawable.ic_btn_speak_now);
//                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                editText.setText(data.get(0));
//                mTimer = new Timer();
//
//                if (data.get(0).equals("вперёд")) {
//                    buttonF.setBackgroundColor(Color.RED);
//                    // change to original after 5 secs.
//                    new Handler().postDelayed(new Runnable() {
//
//                        public void run() {
//                            buttonF.setBackgroundColor(Color.BLUE);
//                            editText.setText("Tap to Speak");
//
//                        }
//                    }, 1000);
//                } else if (data.get(0).equals("назад")) {
//                    buttonS.setBackgroundColor(Color.RED);
//                    new Handler().postDelayed(new Runnable() {
//
//                        public void run() {
//                            buttonS.setBackgroundColor(Color.BLUE);
//                        }
//                    }, 1000);
//                } else if (data.get(0).equals("направо")) {
//                    buttonR.setBackgroundColor(Color.RED);
//                    new Handler().postDelayed(new Runnable() {
//
//                        public void run() {
//                            buttonR.setBackgroundColor(Color.BLUE);
//                        }
//                    }, 1000);
//
//                } else if (data.get(0).equals("налево")) {
//                    buttonL.setBackgroundColor(Color.RED);
//                    new Handler().postDelayed(new Runnable() {
//
//                        public void run() {
//                            buttonL.setBackgroundColor(Color.BLUE);
//                        }
//                    }, 1000);
//
//                }
//            }
//
//
//            @Override
//            public void onPartialResults(Bundle bundle) {
//
//            }
//
//            @Override
//            public void onEvent(int i, Bundle bundle) {
//
//            }
//        });
//
//        micButton.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    speechRecognizer.stopListening();
//                }
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    micButton.setImageResource(R.drawable.ic_btn_speak_now);
//                    speechRecognizer.startListening(speechRecognizerIntent);
//                }
//                return false;
//            }
//        });
//
//
//    }
//
////    private View.OnClickListener clickListener = new View.OnClickListener() {
////        @Override
////        public void onClick(View v) {
////            String command = "";
////
////            if(v.equals(buttonW)){
////                isEnabledW = !isEnabledW;
////                command = isEnabledW ? "on" : "off";
////            }
////
////            MainActivity.getInstance().setMessage(command);
////        }
////    };
//
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public void onClick(View v) {
//        if (connector != null){
//            switch (v.getId()){
//                case R.id.previewVideo:
//                    previewVideo();
//                    break;
//
//                case R.id.connect:
//                    connectToCall(host, token, username, roomID);
//                    break;
//
//                case R.id.disconnect:
//                    disconnect();
//                    break;
//                default:
//                    throw new IllegalStateException("Unexpected value: " + v.getId());
//            }
//        }
//    }
//
//    private void checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
//        }
//    }
//
//
//    public void previewVideo() {
//        connector = new Connector(videoFrame,
//                Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default,
//                15,
//                "",
//                "",
//                0);
//        connector.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
//    }
//
//    public void connectToCall(String host, String token, String username, String roomName) {
//        if (username.matches(usernameValid)) {
//            connector.connect(host, token, username, roomName, this);
//        } else {
//            Toast.makeText(getApplicationContext(), "Оберіть інше ім'я", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    public void disconnect() {
//        connector.selectLocalCamera(null);
//        connector.selectLocalMicrophone(null);
//        connector.selectLocalSpeaker(null);
//        connector.disconnect();
//    }
//
//    public void onSuccess() {
//    }
//
//    public void onFailure(Connector.ConnectorFailReason reason) {
//        System.out.println(reason);
//        ;
//    }
//
//    public void onDisconnected(Connector.ConnectorDisconnectReason reason) {
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
