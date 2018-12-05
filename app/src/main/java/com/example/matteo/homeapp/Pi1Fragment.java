package com.example.matteo.homeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pi1Fragment extends Fragment
{

    Button windowPlugOnButton, windowPlugOffButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragmentpi1_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        UtilitiesClass.HideSoftInputKeyboard(getView());

        windowPlugOnButton = getView().findViewById(R.id.windowPlugOnButton);
        windowPlugOffButton = getView().findViewById(R.id.windowPlugOffButton);
        windowPlugOnButton.setOnClickListener(clickListener);
        windowPlugOffButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.windowPlugOnButton:
                    if(MainActivity.connectedToRack)
                        new MainActivity.SendDataToServerAsync().execute("p1-windowplug_on");
                    break;
                case R.id.windowPlugOffButton:
                    if(MainActivity.connectedToRack)
                        new MainActivity.SendDataToServerAsync().execute("p1-windowplug_off");
                    break;
            }
        }
    };
}
