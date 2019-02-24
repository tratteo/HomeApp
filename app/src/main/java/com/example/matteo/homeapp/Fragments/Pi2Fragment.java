package com.example.matteo.homeapp.Fragments;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.matteo.homeapp.MainActivity;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.SendDataThread;
import com.example.matteo.homeapp.UtilitiesClass;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pi2Fragment extends Fragment
{
    private MainActivity mainActivity;
    public static String defaultRainbowRate = "800";
    String[] commands = new String[]
    {
        "Pi2 Commands",
        "Morning routine"
    };

    static SeekBar rSeekBar, gSeekBar, bSeekBar;
    static TextView rText, gText, bText;
    Spinner p2CommandsSpinner;
    FloatingActionButton deleteCommandLineButton;
    CheckBox toggleTimerCheckBox;
    TimePicker timerSetter;
    EditText commandLine;
    PrintWriter outToRack;
    Button sendButton, ledOnButton, ledOffButton, rainbowButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragmentpi2_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        UtilitiesClass.getInstance().HideSoftInputKeyboard(getView());
        rText = getView().findViewById(R.id.rText);
        gText= getView().findViewById(R.id.gText);
        bText= getView().findViewById(R.id.bText);
        rSeekBar = getView().findViewById(R.id.rSeekBar);
        gSeekBar = getView().findViewById(R.id.gSeekBar);
        bSeekBar = getView().findViewById(R.id.bSeekBar);
        rainbowButton = getView().findViewById(R.id.rainbowButton);
        ledOnButton = getView().findViewById(R.id.ledOnButton);
        ledOffButton = getView().findViewById(R.id.ledOffButton);
        p2CommandsSpinner = getView().findViewById(R.id.pi2Commands);
        deleteCommandLineButton = getView().findViewById(R.id.deleteCommandLineButton);
        toggleTimerCheckBox = getView().findViewById(R.id.toggleTImerCheckBox);
        timerSetter = getView().findViewById(R.id.timePicker);
        commandLine = getView().findViewById(R.id.commandLine);
        sendButton = getView().findViewById(R.id.sendButton);

        SetArrayAdapterForSpinner();

        rSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        gSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        bSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        rainbowButton.setOnClickListener(clickListener);
        ledOffButton.setOnClickListener(clickListener);
        ledOnButton.setOnClickListener(clickListener);
        sendButton.setOnClickListener(clickListener);
        deleteCommandLineButton.setOnClickListener(clickListener);

        p2CommandsSpinner.setOnItemSelectedListener(itemChangeListener);
        toggleTimerCheckBox.setOnCheckedChangeListener(toggleTimerCheckBoxListener);

        if(mainActivity.rackSocket != null)
            try{ outToRack = new PrintWriter(mainActivity.rackSocket.getOutputStream());} catch (Exception e) {}
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            if(fromUser)
            {
                switch (seekBar.getId())
                {
                    case R.id.rSeekBar:
                        if (mainActivity.connectedToRack)
                            new Thread(new SendDataThread("p2-r" + Integer.toString(progress), mainActivity)).start();
                        rText.setText("R: " + Integer.toString(progress));
                        break;
                    case R.id.gSeekBar:
                        if (mainActivity.connectedToRack)
                            new Thread(new SendDataThread("p2-g" + Integer.toString(progress), mainActivity)).start();
                        gText.setText("G: " + Integer.toString(progress));
                        break;
                    case R.id.bSeekBar:
                        if (mainActivity.connectedToRack)
                            new Thread(new SendDataThread("p2-b" + Integer.toString(progress), mainActivity)).start();
                        bText.setText("B: " + Integer.toString(progress));
                        break;
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    private CheckBox.OnCheckedChangeListener toggleTimerCheckBoxListener = new CheckBox.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if(isChecked)
                timerSetter.setVisibility(View.VISIBLE);
            else
                timerSetter.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //Click cases
            switch(v.getId())
            {
                case R.id.sendButton:
                    if(mainActivity.connectedToRack && !commandLine.getText().equals(""))
                    {
                        if(commandLine.getText().toString().equals("Morning routine"))
                        {
                            String command1 = "p2-t" + Integer.toString(UtilitiesClass.getInstance().GetSecondsFromHoursAndMinutes(6, 25)) + "-rainbowstart500";
                            String command2 = "p2-t" + Integer.toString(UtilitiesClass.getInstance().GetSecondsFromHoursAndMinutes(7, 10)) + "-rainbowstop";
                            new Thread(new SendDataThread(command1, mainActivity)).start();
                            new Thread(new SendDataThread(command2, mainActivity)).start();
                        }
                        else
                        {
                            if (toggleTimerCheckBox.isChecked())
                            {
                                String commandString = "p2-t" + Integer.toString(UtilitiesClass.getInstance().GetSecondsFromHoursAndMinutes(timerSetter.getHour(), timerSetter.getMinute())) + "-" + commandLine.getText();
                                new Thread(new SendDataThread(commandString, mainActivity)).start();
                            }
                            else
                                new Thread(new SendDataThread("p2-" + commandLine.getText(), mainActivity)).start();
                        }
                    }
                    break;

                case R.id.ledOnButton:
                    if(mainActivity.connectedToRack)
                    {
                        new Thread(new SendDataThread("p2-on", mainActivity)).start();
                    }
                    break;

                case R.id.ledOffButton:
                    if(mainActivity.connectedToRack)
                    {
                        new Thread(new SendDataThread("p2-off", mainActivity)).start();
                    }
                    break;

                case R.id.rainbowButton:
                    if (mainActivity.connectedToRack)
                    {
                        String rate = UtilitiesClass.getInstance().GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE");
                        new Thread(new SendDataThread("p2-rainbowstart" + rate, mainActivity)).start();
                    }
                    break;
                case R.id.deleteCommandLineButton:
                    commandLine.setText("");
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
                    commandLine.setText("Morning routine");
                    break;
            }
            p2CommandsSpinner.setSelection(0);
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
        p2CommandsSpinner.setAdapter(rackCommandsSpinnerAdapter);
    }

    public static void ChangeProgressBarsValue(int value)
    {
        rSeekBar.setProgress(value, true);
        rText.setText("R: " + Integer.toString(value));
        gSeekBar.setProgress(value, true);
        gText.setText("G: " + Integer.toString(value));
        bSeekBar.setProgress(value, true);
        bText.setText("B: " + Integer.toString(value));
    }
}
