package com.example.matteo.homeapp.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;
import com.example.matteo.homeapp.R;

public class InfoFragment extends Fragment
{
    private MainActivity mainActivity;
    public TextView temperatureTextView = null;
    public TextView tratPiConnectText = null;
    public TextView guizPiConnectText = null;
    public TextView arduinoConnectText = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity)getActivity();
        return inflater.inflate(R.layout.fragment_info, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        temperatureTextView = getView().findViewById(R.id.temperatureTextView);
        tratPiConnectText = getView().findViewById(R.id.tratPiConnectedText);
        guizPiConnectText = getView().findViewById(R.id.guizPiConnectedText);
        arduinoConnectText = getView().findViewById(R.id.arduinoConnectedText);
        SetDeviceStatus("p1", mainActivity.tratPiConnected);
        SetDeviceStatus("p2", mainActivity.guizPiConnected);
        SetDeviceStatus("arduino", mainActivity.arduinoConnected);
    }

    public void SetTemperatureLabel(String temperature)
    {
        temperatureTextView.setText(temperature + "°");
        temperatureTextView.setTextColor(UtilitiesClass.getInstance().GetColorFromTemperature(Float.parseFloat(temperature)));
    }

    public void SetDeviceStatus(String device, boolean status)
    {
        switch (device)
        {
            case "p1":
                if(status)
                {
                    tratPiConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.enabled));
                }
                else
                {
                    tratPiConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.disabled));
                }
                break;
            case "p2":
                if(status)
                {
                    guizPiConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.enabled));
                }
                else
                {
                    guizPiConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.disabled));
                }
                break;
            case "arduino":
                if(status)
                {
                    arduinoConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.enabled));
                }
                else
                {
                    arduinoConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.disabled));
                }
                break;
        }
    }
}
