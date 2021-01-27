package com.zhilai.opengllearn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.videoview.ZVideoHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class NewTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_test);
//        int start = ZVideoHelper.start();
//        System.out.println("test:" + start);
//        startTestUdpSend();
    }

    Thread thread;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thread != null) {
            thread.interrupt();
        }
    }

    private void startTestUdpSend() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket sendSocket = null;
                try {
                    sendSocket = new DatagramSocket(null);
                    sendSocket.setReuseAddress(true);
                    int count = 0;
                    String serverName = "0want.top";
                    InetAddress byName = InetAddress.getByName(serverName);
                    System.out.println(serverName + "->" + byName.getHostAddress());
                    while (!Thread.interrupted()) {
                        count++;
                        byte[] data = new byte[]{
                                (byte) ((count & 0xff0000) >> 16),
                                (byte) ((count & 0xff00) >> 8),
                                (byte) (count & 0xff)};
                        DatagramPacket packet = new DatagramPacket(data, data.length, byName, 25000);
                        sendSocket.send(packet);
                        System.out.println("send packet:" + Arrays.toString(data));
                        Thread.sleep(500);
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (sendSocket != null) {
                        sendSocket.close();
                    }
                }
            }
        });
        thread.start();

    }
}