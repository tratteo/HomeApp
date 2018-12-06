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
    EditText rackIPText, rackPortText, sshCommandLine;
    FloatingActionButton saveSettingsButton;
    Button reconnectRackButton, reconnectP1Button, reconnectP2Button, launchServerButton, closeServerButton, sendSSHButton;

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

        sendSSHButton = getView().findViewById(R.id.sendSshButton);
        launchServerButton = getView().findViewById(R.id.launchServerButton);
        closeServerButton = getView().findViewById(R.id.closeServerButton);
        reconnectRackButton = getView().findViewById(R.id.reconnectRackButton);
        reconnectP1Button = getView().findViewById(R.id.reconnectP1Button);
        reconnectP2Button = getView().findViewById(R.id.reconnectP2Button);

        sshCommandLine = getView().findViewById(R.id.sshCommandLine);
        saveSettingsButton = getView().findViewById(R.id.saveSettingsButton);
        rackIPText = getView().findViewById(R.id.rack_ip);
        rackPortText = getView().findViewById(R.id.rack_port);

        rackIPText.setHint(UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_IP", "192.168.1.40"));
        rackPortText.setHint(UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_PORT", "7777"));

        sendSSHButton.setOnClickListener(clickListener);
        closeServerButton.setOnClickListener(clickListener);
        launchServerButton.setOnClickListener(clickListener);
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

                case R.id.launchServerButton:
                    if(!MainActivity.connectedToRack)
                        UtilitiesClass.RunSSHCommand("192.168.1.40", "rack", "rackpcpassword", "export DISPLAY=:0 && java -jar /home/rack/Programmazione/RackServer/RackServer.jar");
                    break;
                case R.id.closeServerButton:
                    if(MainActivity.connectedToRack)
                        new MainActivity.SendDataToServerAsync().execute("rack-close server");
                    break;
                case R.id.sendSshButton:
                    if(!sshCommandLine.getText().toString().equals("") && !MainActivity.connectedToRack)
                    UtilitiesClass.RunSSHCommand("192.168.1.40", "rack", "rackpcpassword", sshCommandLine.getText().toString());
                    break;
            }
        }
    };
}
