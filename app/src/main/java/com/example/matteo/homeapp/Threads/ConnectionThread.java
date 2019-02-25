package com.example.matteo.homeapp.Threads;

import android.util.Log;

import com.example.matteo.homeapp.MainActivity;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionThread extends Thread
{
    private boolean stop = false;
    private MainActivity mainActivity;
    public ConnectionThread(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run()
    {
        while (!stop)
        {
            try
            {
                InetSocketAddress socketAddress = new InetSocketAddress(mainActivity.rackIP, Integer.parseInt(mainActivity.rackPort));
                mainActivity.rackSocket = new Socket();
                mainActivity.rackSocket.connect(socketAddress, 500);
                mainActivity.outToRack = new PrintWriter(mainActivity.rackSocket.getOutputStream());
                stop = true;
            }
            catch (Exception e)
            {
                stop = false;
            }
            if (stop)
            {
                mainActivity.setConnectedToRack(true);
                final String ipString = mainActivity.rackSocket.getInetAddress().toString().substring(1);
                mainActivity.toolbarConnectionText.post(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.toolbarConnectionText.setText("Connected to: " + ipString);
                    }
                });
                if (mainActivity.listenerThread != null)
                    mainActivity.listenerThread.interrupt();
                mainActivity.listenerThread = new ListenerThread(mainActivity);
                mainActivity.listenerThread.start();
            }
            try{Thread.sleep(1000);} catch (InterruptedException e){return;}
        }
    }
}
