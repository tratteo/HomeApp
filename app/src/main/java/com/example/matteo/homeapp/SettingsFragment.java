package com.example.matteo.homeapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import xdroid.toaster.Toaster;

public class SettingsFragment extends Fragment
{
    EditText rackIPText, rackPortText;
    FloatingActionButton saveSettingsButton;
    Button reconnectRackButton, reconnectP1Button, reconnectP2Button, rackSSHButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_settings, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        UtilitiesClass.HideSoftInputKeyboard(getView());

        rackSSHButton = getView().findViewById(R.id.rackSshButton);
        reconnectRackButton = getView().findViewById(R.id.reconnectRackButton);
        reconnectP1Button = getView().findViewById(R.id.reconnectP1Button);
        reconnectP2Button = getView().findViewById(R.id.reconnectP2Button);

        saveSettingsButton = getView().findViewById(R.id.saveSettingsButton);
        rackIPText = getView().findViewById(R.id.rack_ip);
        rackPortText = getView().findViewById(R.id.rack_port);

        rackIPText.setHint(UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_IP", "192.168.1.40"));
        rackPortText.setHint(UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_PORT", "7777"));

        rackSSHButton.setOnClickListener(clickListener);
        saveSettingsButton.setOnClickListener(clickListener);
        reconnectRackButton.setOnClickListener(clickListener);
        reconnectP1Button.setOnClickListener(clickListener);
        reconnectP2Button.setOnClickListener(clickListener);
    }


    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.saveSettingsButton:
                    String newRackPort = rackPortText.getText().toString().trim();
                    String newRackIP = rackIPText.getText().toString().trim();

                    if (!newRackIP.equals(""))
                    {
                        UtilitiesClass.SaveSharedPreferencesKey("settings", "RACK_IP", newRackIP);
                        rackIPText.setHint(newRackIP);
                        MainActivity.rackIP = newRackIP;
                        MainActivity.connectedToRack = false;
                        MainActivity.StartConnectionThread();
                    }
                    if(!newRackPort.equals(""))
                    {
                        UtilitiesClass.SaveSharedPreferencesKey("settings", "RACK_PORT", newRackPort);
                        rackPortText.setHint(newRackPort);
                        MainActivity.rackPort = newRackPort;
                        MainActivity.connectedToRack = false;
                        MainActivity.StartConnectionThread();
                    }
                    Toaster.toast("Settings saved");
                    break;

                case R.id.reconnectRackButton:
                    if(!MainActivity.connectedToRack && MainActivity.IsConnectedToWiFi())
                        MainActivity.StartConnectionThread();
                    break;

                case R.id.reconnectP1Button:
                    if(MainActivity.connectedToRack && MainActivity.IsConnectedToWiFi())
                        new MainActivity.SendDataToServerAsync().execute("p1-connect");
                    break;

                case R.id.reconnectP2Button:
                    if(MainActivity.connectedToRack && MainActivity.IsConnectedToWiFi())
                        new MainActivity.SendDataToServerAsync().execute("p2-connect");
                    break;

                case R.id.rackSshButton:
                    if(!MainActivity.connectedToRack)
                        UtilitiesClass.RunSSHCommand("192.168.1.40", "rack", "rackpcpassword", "export DISPLAY=:0 && java -jar /home/rack/Programmazione/RackServer/RackServer.jar &");
                    else
                        Toaster.toast("RackServer already running");
                    break;
            }
        }
    };
}
