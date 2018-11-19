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
    String[] commands = new String[]
    {
            "Pi1 Commands"
    };
    Spinner p1CommandsSpinner;
    EditText commandLine;
    PrintWriter outToCabinet;
    Button sendButton;
    CheckBox toggleTimerCheckBox;
    TimePicker timerSetter;
    FloatingActionButton deleteCommandLineButton;

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
        p1CommandsSpinner = getView().findViewById(R.id.pi1Commands);
        toggleTimerCheckBox = getView().findViewById(R.id.toggleTImerCheckBox);
        deleteCommandLineButton  = getView().findViewById(R.id.deleteCommandLineButton);
        timerSetter = getView().findViewById(R.id.timePicker);
        commandLine = getView().findViewById(R.id.commandLine);
        sendButton = getView().findViewById(R.id.sendButton);

        SetArrayAdapterForSpinner();

        p1CommandsSpinner.setOnItemSelectedListener(itemChangeListener);
        sendButton.setOnClickListener(sendDataButtonListener);
        deleteCommandLineButton.setOnClickListener(deleteCommandLineButtonListener);
        if(MainActivity.rackSocket != null)
            try{ outToCabinet = new PrintWriter(MainActivity.rackSocket.getOutputStream());} catch (Exception e) {}

    }

    private View.OnClickListener sendDataButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(MainActivity.connectedToRack && !commandLine.getText().equals(""))
                if(toggleTimerCheckBox.isChecked())
                    new MainActivity.SendDataToServerAsync().execute("p1-t" + Integer.toString(UtilitiesClass.GetSecondsFromHoursAndMinutes(timerSetter.getHour(), timerSetter.getMinute())) + "-" + commandLine.getText());
                else
                    new MainActivity.SendDataToServerAsync().execute("p1-" + commandLine.getText());
        }
    };

    private View.OnClickListener deleteCommandLineButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            commandLine.setText("");
        }
    };

    private AdapterView.OnItemSelectedListener itemChangeListener = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            switch (position)
            {
                case 1:
                    commandLine.setText("Morning Routine");
                    break;
            }
            p1CommandsSpinner.setSelection(0);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    };

    private void SetArrayAdapterForSpinner()
    {
        final List<String> rackCommands = new ArrayList<>(Arrays.asList(commands));
        final ArrayAdapter<String> rackCommandsSpinnerAdapter = new ArrayAdapter<String>(MainActivity.context, R.layout.rack_commands_spinner, rackCommands)
        {
            @Override
            public boolean isEnabled(int position)
            {
                if(position == 0)
                    return false;
                else
                    return true;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0)
                    tv.setTextColor(Color.GRAY);
                else
                    tv.setTextColor(Color.BLACK);

                return view;
            }
        };
        rackCommandsSpinnerAdapter.setDropDownViewResource(R.layout.rack_commands_spinner);
        p1CommandsSpinner.setAdapter(rackCommandsSpinnerAdapter);
    }

}
