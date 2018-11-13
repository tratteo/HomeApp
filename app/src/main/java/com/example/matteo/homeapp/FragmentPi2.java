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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.PrintWriter;

public class FragmentPi2 extends Fragment
{
    FloatingActionButton deleteCommandLineButton;
    CheckBox toggleTimerCheckBox;
    TimePicker timerSetter;
    EditText commandLine;
    PrintWriter outToCabinet;
    Button sendButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragmentpi2_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(MainActivity.rackSocket != null)
            try{ outToCabinet = new PrintWriter(MainActivity.rackSocket.getOutputStream());} catch (Exception e) {}

        deleteCommandLineButton = getView().findViewById(R.id.deleteCommandLineButton);
        toggleTimerCheckBox = getView().findViewById(R.id.toggleTImerCheckBox);
        timerSetter = getView().findViewById(R.id.timePicker);
        commandLine = getView().findViewById(R.id.commandLine);
        sendButton = getView().findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(MainActivity.connectedToRack && !commandLine.getText().equals(""))
                    if(toggleTimerCheckBox.isChecked())
                        new MainActivity.SendDataToServerAsync().execute("p2-t" + Integer.toString(UtilitiesClass.GetTimerSeconds(timerSetter.getHour(), timerSetter.getMinute())) + "-" + commandLine.getText());
                    else
                        new MainActivity.SendDataToServerAsync().execute("p2-" + commandLine.getText());
            }
        });

       deleteCommandLineButton.setOnClickListener(new View.OnClickListener()
       {
           @Override
           public void onClick(View v)
           {
               commandLine.setText("");
           }
       });
    }
}
