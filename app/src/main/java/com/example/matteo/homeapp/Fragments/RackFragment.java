package com.example.matteo.homeapp.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;

import com.example.matteo.homeapp.MainActivity;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.SSHCommandRunnable;
import com.example.matteo.homeapp.Runnables.SendDataRunnable;
import com.example.matteo.homeapp.UtilitiesClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RackFragment extends Fragment
{
    private MainActivity mainActivity;
    String[] commands = new String[]
    {
        "Rack Commands",
        "Dolomites Flythrough"
    };
    FloatingActionButton deleteCommandLineButton;
    Button sendDataToRackButton, launchServerButton, closeServerButton, suspendRackButton;
    EditText rackCommandLine;
    Spinner rackCommandsSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.rack_fragment_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        UtilitiesClass.getInstance().HideSoftInputKeyboard(getView());

        suspendRackButton = getView().findViewById(R.id.suspendRackButton);
        launchServerButton = getView().findViewById(R.id.launchServerButton);
        closeServerButton = getView().findViewById(R.id.closeServerButton);
        deleteCommandLineButton = getView().findViewById(R.id.deleteCommandLineButton);
        rackCommandsSpinner = getView().findViewById(R.id.rackCommands);
        sendDataToRackButton = getView().findViewById(R.id.sendDataToRack);
        rackCommandLine = getView().findViewById(R.id.rackCommandLine);

        SetArrayAdapterForSpinner();

        rackCommandsSpinner.setOnItemSelectedListener(itemChangeListener);
        suspendRackButton.setOnClickListener(clickListener);
        launchServerButton.setOnClickListener(clickListener);
        closeServerButton.setOnClickListener(clickListener);
        sendDataToRackButton.setOnClickListener(clickListener);
        deleteCommandLineButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.sendDataToRack:
                    if(mainActivity.isConnectedToRack() && !rackCommandLine.getText().equals(""))
                        UtilitiesClass.getInstance().executeRunnable(new SendDataRunnable("rack-" + rackCommandLine.getText(), mainActivity));
                    break;
                case R.id.deleteCommandLineButton:
                    rackCommandLine.setText("");
                    break;

                case R.id.launchServerButton:
                    if(!mainActivity.isConnectedToRack())
                    {
                        SSHCommandRunnable sshRunnable = new SSHCommandRunnable("192.168.1.40", "rack", "rackpcpassword", "export DISPLAY=:0 && java -jar /home/rack/Programmazione/RackServer/RackServer.jar");
                        UtilitiesClass.getInstance().executeRunnable(sshRunnable);
                    }
                    break;

                case R.id.closeServerButton:
                    if(mainActivity.isConnectedToRack())
                        UtilitiesClass.getInstance().executeRunnable(new SendDataRunnable("rack-close server", mainActivity));
                    break;
                case R.id.suspendRackButton:
                    SSHCommandRunnable sshRunnable = new SSHCommandRunnable("192.168.1.40", "rack", "rackpcpassword", "echo rackpcpassword | sudo -S systemctl suspend");
                    UtilitiesClass.getInstance().executeRunnable(sshRunnable);
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
        final ArrayAdapter<String> rackCommandsSpinnerAdapter = new ArrayAdapter<String>(mainActivity.getApplicationContext(), R.layout.rack_commands_spinner, rackCommands)
        {
            @Override
            public boolean isEnabled(int position)
            {
                return position != 0;
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
