package com.example.matteo.homeapp.Runnables;

import android.os.Handler;
import android.util.Log;

import com.example.matteo.homeapp.Fragments.InfoFragment;
import com.example.matteo.homeapp.Interfaces.KillableRunnable;
import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import xdroid.toaster.Toaster;

public class ListenerRunnable implements KillableRunnable
{
    private boolean stop = false;
    private MainActivity mainActivity;
    private BufferedReader inFromCabinet;
    private String serverResponse;

    ListenerRunnable(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        if(this.mainActivity.rackSocket != null)
            try{ inFromCabinet = new BufferedReader(new InputStreamReader(mainActivity.rackSocket.getInputStream())); } catch (Exception ignored) {}
    }

    @Override
    public void run()
    {
        while (!stop)
        {
            try
            {
                serverResponse = inFromCabinet.readLine();
                if (serverResponse != null)
                {
                    serverResponse = serverResponse.toLowerCase();
                    CheckCommandToExecute();
                }
            } catch (IOException ignored) {}
        }
    }

    private void CheckCommandToExecute()
    {
        //Info fragment is active
        if(mainActivity.getCurrentFragment().getClass().equals(InfoFragment.class))
        {
            final InfoFragment infoFragment = (InfoFragment) mainActivity.getCurrentFragment();
            //Temperature
            if (UtilitiesClass.getInstance().IsStringFloatConvertible(serverResponse))
            {
                UtilitiesClass.getInstance().ExecuteOnMainThread(() ->  infoFragment.SetTemperatureLabel(serverResponse));
            }
        }
        switch (serverResponse)
        {
            case "serverdown":
                UtilitiesClass.getInstance().ExecuteOnMainThread(() ->  mainActivity.toolbarConnectionText.setText("Connection interrupted from server"));
                try { mainActivity.rackSocket.close(); } catch (final Exception ignored) { }

                mainActivity.setConnectedToRack(false);
                stop = true;
                break;
            case "ar-connected":
                mainActivity.arduinoConnected = Boolean.TRUE;
                break;
            case "p1-connected":
                mainActivity.tratPiConnected = Boolean.TRUE;
                break;
            case "p2-connected":
                mainActivity.guizPiConnected = Boolean.TRUE;
                break;
            case "ar-interrupt":
                mainActivity.arduinoConnected = Boolean.FALSE;
                break;
            case "p1-interrupt":
                mainActivity.tratPiConnected = Boolean.FALSE;
                break;
            case "p2-interrupt":
                mainActivity.guizPiConnected = Boolean.FALSE;
                break;
            case "p2-rainbowrunning":
                Toaster.toast("Rainbow thread is running on P2");
                break;
        }
        if(mainActivity.getCurrentFragment().getClass().equals(InfoFragment.class))
        {
            final InfoFragment infoFragment = (InfoFragment) mainActivity.getCurrentFragment();
            UtilitiesClass.getInstance().ExecuteOnMainThread(() ->
            {
                infoFragment.SetDeviceStatus("p1", mainActivity.tratPiConnected);
                infoFragment.SetDeviceStatus("p2", mainActivity.guizPiConnected);
                infoFragment.SetDeviceStatus("arduino", mainActivity.arduinoConnected);

            });
        }
    }

    @Override
    public boolean isRunning() {return !stop;}
    @Override
    public void kill() {stop = true;}
}