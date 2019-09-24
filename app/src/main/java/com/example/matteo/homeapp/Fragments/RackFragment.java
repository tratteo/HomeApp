package com.example.matteo.homeapp.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;

import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.SSHCommandRunnable;
import com.example.matteo.homeapp.Runnables.SendDataRunnable;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xdroid.toaster.Toaster;


public class RackFragment extends Fragment
{
    private MainActivity mainActivity;
    String[] commands = new String[]
    {
        "Rack Commands",
        "Dolomites Flythrough"
    };

    SeekBar volumeSeekBar;
    FloatingActionButton deleteCommandLineButton;
    ImageButton spotifyButton, firefoxButton, spotifyToggleButton, spotifyNextButton, spotifyPreviousButton;
    Button sendDataToRackButton, launchServerButton, closeServerButton, suspendRackButton;
    EditText rackCommandLine;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_rack, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        UtilitiesClass.getInstance().HideSoftInputKeyboard(getView());

        volumeSeekBar = getView().findViewById(R.id.volumeSeekBar);
        spotifyNextButton = getView().findViewById(R.id.spotifyNextBtn);
        spotifyToggleButton = getView().findViewById(R.id.spotifyToggleBtn);
        spotifyPreviousButton = getView().findViewById(R.id.spotifyPrevBtn);
        spotifyButton = getView().findViewById(R.id.spotifyIconBtn);
        firefoxButton = getView().findViewById(R.id.firefoxIconBtn);
        suspendRackButton = getView().findViewById(R.id.suspendRackButton);
        launchServerButton = getView().findViewById(R.id.launchServerButton);
        closeServerButton = getView().findViewById(R.id.closeServerButton);
        deleteCommandLineButton = getView().findViewById(R.id.deleteCommandLineButton);
        sendDataToRackButton = getView().findViewById(R.id.sendDataToRack);
        rackCommandLine = getView().findViewById(R.id.rackCommandLine);


        suspendRackButton.setOnClickListener(clickListener);
        launchServerButton.setOnClickListener(clickListener);
        closeServerButton.setOnClickListener(clickListener);
        sendDataToRackButton.setOnClickListener(clickListener);
        deleteCommandLineButton.setOnClickListener(clickListener);

        firefoxButton.setOnClickListener(clickListener);
        firefoxButton.setOnLongClickListener(longClickListener);
        spotifyButton.setOnClickListener(clickListener);
        spotifyButton.setOnLongClickListener(longClickListener);

        spotifyToggleButton.setOnClickListener(clickListener);
        spotifyNextButton.setOnClickListener(clickListener);
        spotifyPreviousButton.setOnClickListener(clickListener);

        volumeSeekBar.setOnSeekBarChangeListener(seekBarListener);

    }

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(mainActivity.VIBRATOR_SERVICE);
            switch(v.getId())
            {
                case R.id.spotifyIconBtn:
                    if(mainActivity.isConnectedToRack())
                    {
                        vibrator.vibrate(35);
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("rack-close spotify", mainActivity));
                    }
                    else
                        Toaster.toast("Not connected to rack");
                    break;
                case R.id.firefoxIconBtn:
                    if(mainActivity.isConnectedToRack())
                    {
                        vibrator.vibrate(35);
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("rack-close firefox", mainActivity));
                    }
                    else
                        Toaster.toast("Not connected to rack");
                    break;
            }

            return true;
        }

    };

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean state)
        {
            UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("rack-volume" + Integer.toString(progress*10), mainActivity));
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.sendDataToRack:
                    if(mainActivity.isConnectedToRack() && !rackCommandLine.getText().equals(""))
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(rackCommandLine.getText().toString(), mainActivity));
                    break;

                case R.id.deleteCommandLineButton:
                    rackCommandLine.setText("");
                    break;

                case R.id.launchServerButton:
                    SSHCommandRunnable launchServerSsh = new SSHCommandRunnable(mainActivity.rackIP, "rack", "rackpcpassword", "export DISPLAY=:0 && /run_batches/run_rack.sh");
                    UtilitiesClass.getInstance().ExecuteRunnable(launchServerSsh);
                    break;

                case R.id.closeServerButton:
                    if(mainActivity.isConnectedToRack())
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("rack-close server", mainActivity));
                    break;

                case R.id.suspendRackButton:
                    SSHCommandRunnable sshRunnable = new SSHCommandRunnable(mainActivity.rackIP, "rack", "rackpcpassword", "sudo -S systemctl suspend");
                    UtilitiesClass.getInstance().ExecuteRunnable(sshRunnable);
                    break;

                case R.id.spotifyIconBtn:
                    if(mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("rack-spotify", mainActivity));
                    }
                    else
                        Toaster.toast("Not connected to rack");
                    break;

                case R.id.firefoxIconBtn:
                    if(mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("rack-firefox", mainActivity));
                    }
                    else
                        Toaster.toast("Not connected to rack");
                    break;
                case R.id.spotifyToggleBtn:
                    if(mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("rack-spotifytoggle", mainActivity));
                    }
                    break;
                case R.id.spotifyNextBtn:
                    if(mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("rack-spotifynext", mainActivity));
                    }
                    break;
                case R.id.spotifyPrevBtn:
                    if(mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("rack-spotifyprevious", mainActivity));
                    }
                    break;
            }
        }
    };


/*    private AdapterView.OnItemSelectedListener itemChangeListener = new AdapterView.OnItemSelectedListener()
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
    }*/

    public void SetVolumeSeekBarValue(int value)
    {
        volumeSeekBar.setProgress(value);
    }
}
