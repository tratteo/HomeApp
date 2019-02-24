package com.example.matteo.homeapp.Runnables;

import android.util.Log;

import com.example.matteo.homeapp.Interfaces.KillableRunnable;
import com.example.matteo.homeapp.MainActivity;

import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionThread implements KillableRunnable
{
    private MainActivity mainActivity;
    public ConnectionThread(MainActivity mainActivity) {this.mainActivity = mainActivity;}
    private boolean stop = false;
    @Override
    public void run()
    {
        while(!stop)
        {
            try{Thread.sleep(350);} catch (Exception e) {}
            try
            {
                mainActivity.rackSocket = new Socket(mainActivity.rackIP, Integer.parseInt(mainActivity.rackPort));
                mainActivity.outToRack = new PrintWriter(mainActivity.rackSocket.getOutputStream());
                stop = true;
            }
            catch (Exception e)
            {
                stop = false;
            }
            if(stop)
            {
                mainActivity.connectedToRack = true;
                final String ipString = mainActivity.rackSocket.getInetAddress().toString().substring(1);

                mainActivity.toolbarConnectionText.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mainActivity.toolbarConnectionText.setText("Connected to: " + ipString);
                    }
                });

                if(mainActivity.listenerThread != null)
                    mainActivity.listenerThread.kill();
                mainActivity.listenerThread = new ListenerThread(mainActivity);
                new Thread(mainActivity.listenerThread).start();
            }
            else
            {
                try{Thread.sleep(1150);} catch (Exception e) {}
            }
        }
    }

    @Override
    public void kill() {stop = true;}
    @Override
    public boolean isRunning() {return !stop;}
}
