package com.example.matteo.homeapp.Runnables;

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
        //serverResponse is temperature
        if(UtilitiesClass.getInstance().IsStringFloatConvertible(serverResponse))
        {
            if(mainActivity.infoFragment != null && !serverResponse.equals(null))
            {
                mainActivity.infoFragment.temperatureTextView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mainActivity.infoFragment.temperatureTextView.setText(serverResponse + "°");
                        mainActivity.infoFragment.temperatureTextView.setTextColor(UtilitiesClass.getInstance().GetColorFromTemperature(Float.parseFloat(serverResponse)));
                    }
                });
            }
        }
        else
        {
            switch (serverResponse)
            {
            case "serverdown":

                mainActivity.toolbarConnectionText.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mainActivity.toolbarConnectionText.setText("Connection interrupted from server");
                    }
                });

                try { mainActivity.rackSocket.close(); } catch (final Exception ignored) { }

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

    @Override
    public boolean isRunning() {return !stop;}
    @Override
    public void kill() {stop = true;}
}