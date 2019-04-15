package com.example.matteo.homeapp.Runnables;

import android.util.Log;

import com.example.matteo.homeapp.Interfaces.KillableRunnable;
import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionRunnable implements KillableRunnable
{
    private boolean stop = false, succeded = false;
    private MainActivity mainActivity;
    public ConnectionRunnable(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run()
    {
        while (!stop)
        {
            try{Thread.sleep(200);} catch (InterruptedException ignored){}
            try
            {
                InetSocketAddress socketAddress = new InetSocketAddress(mainActivity.rackIP, Integer.parseInt(mainActivity.rackPort));
                mainActivity.rackSocket = new Socket();
                mainActivity.rackSocket.connect(socketAddress, 500);
                mainActivity.outToRack = new PrintWriter(mainActivity.rackSocket.getOutputStream());
                succeded = true;
            }
            catch (Exception e) {succeded = false;}

            if (succeded)
            {
                mainActivity.setConnectedToRack(true);
                final String ipString = mainActivity.rackSocket.getInetAddress().toString().substring(1);
                mainActivity.toolbarConnectionText.post(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.toolbarConnectionText.setText("Connected to: " + ipString);
                    }
                });
                if (mainActivity.listenerRunnable != null)
                    mainActivity.listenerRunnable.kill();
                mainActivity.listenerRunnable = new ListenerRunnable(mainActivity);
                UtilitiesClass.getInstance().ExecuteRunnable(mainActivity.listenerRunnable);
                kill();
            }
            else
                try{Thread.sleep(800);} catch (InterruptedException ignored){}
        }
    }

    @Override
    public boolean isRunning() {return !stop;}

    @Override
    public void kill() {stop = true;}
}
