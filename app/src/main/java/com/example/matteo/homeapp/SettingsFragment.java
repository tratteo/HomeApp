package com.example.matteo.homeapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import xdroid.toaster.Toaster;

public class SettingsFragment extends Fragment
{
    EditText rackIPText, rackPortText;
    FloatingActionButton saveSettingsButton;

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

        saveSettingsButton = getView().findViewById(R.id.saveSettingsButton);
        rackIPText = getView().findViewById(R.id.rack_ip);
        rackPortText = getView().findViewById(R.id.rack_port);

        rackIPText.setHint(UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_IP", "192.168.1.40"));
        rackPortText.setHint(UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_PORT", "7777"));
        saveSettingsButton.setOnClickListener(saveSettingsButtonListener);
    }

    private View.OnClickListener saveSettingsButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
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
        }
    };
}
