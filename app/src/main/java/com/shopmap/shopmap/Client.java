package com.shopmap.shopmap;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Client {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String input, output;
    private boolean connected, update;
    private String ip;

    public Client() {
        ip = "192.168.168.174";
        connected = true;
        input = "";

        getConnection();
    }

    private void getConnection() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connected = true;

                try {
                    socket = new Socket(ip, 800);

                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());

                    output = "";

                    while (connected) {
                        String temp = "";
                        if (!(temp = dis.readUTF().toString()).equals("Test_Connection")) {
                            input = temp;
                        }

                        if (!input.isEmpty()) {
                            update = true;
                        }
                    }
                } catch (IOException e1) {
                    connected = false;

                    try {
                        socket.close();
                    } catch (IOException e2) {

                    } catch (NullPointerException e3) {

                    }
                }
            }
        });

        thread.start();
    }

    public boolean sendOutput(String output) {
        this.output = output;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!output.equals("")) {
                        Log.i("Debug", "Request Sent to Server");
                        Log.i("Debug", output);
                        dos.writeUTF(output);
                        Log.i("Debug", "Request Stored");
                        dos.flush();
                        Log.i("Debug", "Request Flushed");
                    }
                } catch (IOException e1) {
                    Log.i("Debug", "Something is Fucked");
                } catch (NullPointerException e2) {

                }
            }
        });

        thread.start();

        for (int i = 0; i < 100; i++) {
            Log.i("Debug", String.format("%s, %s", connected, update));

            if (connected && !update) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return true;
    }

    public boolean getConnected() {return connected;}

    public void setIp(String ip) {this.ip = ip;}

    public String[] getStoreInfo(String storeID) {
        sendOutput(String.format("getStoreInfo/cmdend/%s", storeID));

        while (!update) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e1) {

            }
        }

        String[] storeInfo = input.split("/SPLIT/");

        return storeInfo;
    }

    public String[] getPath(ArrayList<String> targets) {
        String temp = "";
        for (String s: targets) {
            temp += String.format("%s/ADD/", s);
        }
        sendOutput(String.format("calculatePath/cmdend/%s", temp));

        while (!update) {
            try {
                TimeUnit.MILLISECONDS.wait(100);
            } catch (InterruptedException e1) {

            }
        }

        String[] path = input.split("/->/");

        return path;
    }

    public ArrayList<ArrayList<String>> getMap(String storeID) {
        sendOutput(String.format("getMap/cmdend/%s", storeID));

        while (!update) {
            try {
                TimeUnit.MILLISECONDS.wait(100);
            } catch (InterruptedException e1) {

            }
        }

        String[] temp = input.split("/SPLIT/");
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        for (String li: temp) {
            ArrayList<String> resultArray = new ArrayList<String>();
            String[] sTemp = li.split("/ADD/");

            for (String s: sTemp) {
                resultArray.add(s);
            }

            result.add(resultArray);
        }

        return result;
    }
}
