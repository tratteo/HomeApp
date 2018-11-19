package com.example.matteo.homeapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SettingsFragment extends Fragment
{
    EditText rackIp, rackPort, p1Ip, p1Port, p2Ip, p2Port;
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
        rackIp = getView().findViewById(R.id.rack_ip);
        rackPort = getView().findViewById(R.id.rack_port);
        p1Ip = getView().findViewById(R.id.pi1_ip);
        p1Port = getView().findViewById(R.id.pi1_port);
        p2Ip = getView().findViewById(R.id.pi2_ip);
        p2Port = getView().findViewById(R.id.pi2_port);
    }
}
