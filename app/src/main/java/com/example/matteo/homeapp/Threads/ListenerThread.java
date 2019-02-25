package com.example.matteo.homeapp.Threads;

import android.util.Log;

import com.example.matteo.homeapp.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import xdroid.toaster.Toaster;

public class ListenerThread extends Thread
{
    private boolean stop = false;
    private MainActivity mainActivity;
    private BufferedReader inFromCabinet;
    private String serverResponse;

    public ListenerThread(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        if(this.mainActivity.rackSocket != null)
            try{ inFromCabinet = new BufferedReader(new InputStreamReader(mainActivity.rackSocket.getInputStream())); } catch (Exception e) {}
    }

    @Override
    public void run()
    {
        while (!stop)
        {
            if (Thread.interrupted())
                return;
            try
            {
                serverResponse = inFromCabinet.readLine();
                if (serverResponse != null)
                {
                    serverResponse = serverResponse.toLowerCase();
                    CheckCommandToExecute();
                }
            } catch (IOException ex) {}
        }
    }

    private void CheckCommandToExecute()
    {
        switch (serverResponse)
        {
            case "serverdown":

                mainActivity.toolbarConnectionText.post(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.toolbarConnectionText.setText("Connection interrupted from server");
                    }
                });
                try { mainActivity.rackSocket.close(); } catch (final Exception e) { }
                mainActivity.setConnectedToRack(false);
                stop = true;
                break;

            case "p1-unable":
                Toaster.toast("Unable to connect to P1");
                break;
            case "p2-unable":
                Toaster.toast("Unable to connect to P2");
                break;
            case "p1-connected":
                Toaster.toast("P1 Connected");
                break;
            case "p2-connected":
                Toaster.toast("P2 Connected");
                break;
            case "p1-interrupt":
                Toaster.toast("P1 has interrupted connection");
                break;
            case "p2-interrupt":
                Toaster.toast("P2 has interrupted connection");
                break;
            case "p2-rainbowrunning":
                Toaster.toast("Rainbow thread is running on P2");
                break;
        }
    }
}