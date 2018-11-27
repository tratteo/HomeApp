package com.example.matteo.homeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RackFragment extends Fragment
{
    String[] commands = new String[]
    {
        "Rack Commands",
        "Dolomites Flythrough"
    };
    FloatingActionButton deleteCommandLineButton;
    Button reconnectButton, connectToP1, connectToP2, sendDataToRackButton;
    EditText rackCommandLine;
    Spinner rackCommandsSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rack_fragment_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        UtilitiesClass.HideSoftInputKeyboard(getView());
        deleteCommandLineButton = getView().findViewById(R.id.deleteCommandLineButton);
        rackCommandsSpinner = getView().findViewById(R.id.rackCommands);
        reconnectButton = getView().findViewById(R.id.reconnectButton);
        connectToP1 = getView().findViewById(R.id.connectToP1);
        connectToP2 = getView().findViewById(R.id.connectToP2);
        sendDataToRackButton = getView().findViewById(R.id.sendDataToRack);
        rackCommandLine = getView().findViewById(R.id.rackCommandLine);

        SetArrayAdapterForSpinner();

        rackCommandsSpinner.setOnItemSelectedListener(itemChangeListener);
        sendDataToRackButton.setOnClickListener(clickListener);
        deleteCommandLineButton.setOnClickListener(clickListener);
        reconnectButton.setOnClickListener(clickListener);
        connectToP1.setOnClickListener(clickListener);
        connectToP2.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.sendDataToRack:
                    if(MainActivity.connectedToRack && !rackCommandLine.getText().equals(""))
                        new MainActivity.SendDataToServerAsync().execute("rack-" + rackCommandLine.getText());
                    break;

                case R.id.connectToP1:
                    if(MainActivity.connectedToRack)
                        new MainActivity.SendDataToServerAsync().execute("p1-connect");
                    break;

                case R.id.connectToP2:
                    if(MainActivity.connectedToRack)
                        new MainActivity.SendDataToServerAsync().execute("p2-connect");
                    break;

                case R.id.reconnectButton:
                    if(!MainActivity.connectedToRack && MainActivity.IsConnectedToWiFi())
                        MainActivity.StartConnectionThread();
                    break;
                case R.id.deleteCommandLineButton:
                    rackCommandLine.setText("");
                    break;
            }

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
                    rackCommandLine.setText("firefox https://www.youtube.com/tv#/watch?v=MGe_Jw6as6U");
                    break;
                case 2:
                    rackCommandLine.setText("Morning Routine");
                    break;
            }
            rackCommandsSpinner.setSelection(0);
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
        rackCommandsSpinner.setAdapter(rackCommandsSpinnerAdapter);
    }
}
