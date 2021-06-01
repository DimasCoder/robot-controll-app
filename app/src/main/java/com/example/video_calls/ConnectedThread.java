package com.example.video_calls;

import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.OutputStream;

public class ConnectedThread extends Thread  {
    private BluetoothSocket socket;
    private OutputStream outputStream;

    public ConnectedThread(BluetoothSocket socket) {
        this.socket = socket;
        try {
            outputStream =  socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String input) {
        byte[] bytes = input.getBytes();
        try {
            outputStream.write(bytes);
        } catch (IOException e) { }
    }

}

