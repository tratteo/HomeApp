package com.example.matteo.homeapp.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.SendDataRunnable;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;

import xdroid.toaster.Toaster;

public class SettingsFragment extends Fragment
{
    private MainActivity mainActivity;
    EditText rackIPText, rackPortText, defaultRainbowRate;
    FloatingActionButton saveSettingsButton;
    Button reconnectRackButton, reconnectP1Button, reconnectP2Button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_settings, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        UtilitiesClass.getInstance().HideSoftInputKeyboard(getView());

        reconnectRackButton = getView().findViewById(R.id.reconnectRackButton);
        reconnectP1Button = getView().findViewById(R.id.reconnectP1Button);
        reconnectP2Button = getView().findViewById(R.id.reconnectP2Button);

        defaultRainbowRate = getView().findViewById(R.id.defaultRainbowRateText);
        saveSettingsButton = getView().findViewById(R.id.saveSettingsButton);

        rackIPText = getView().findViewById(R.id.rack_ip);
        rackPortText = getView().findViewById(R.id.rack_port);

        saveSettingsButton.setOnClickListener(clickListener);
        reconnectRackButton.setOnClickListener(clickListener);
        reconnectP1Button.setOnClickListener(clickListener);
        reconnectP2Button.setOnClickListener(clickListener);

        UtilitiesClass.getInstance().LoadAppPreferences();
        rackIPText.setHint(mainActivity.rackIP);
        rackPortText.setHint(mainActivity.rackPort);
        defaultRainbowRate.setHint(LEDFragment.defaultRainbowRate);
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.saveSettingsButton:
                    String newRackPort = rackPortText.getText().toString().trim();
                    String newRackIP = rackIPText.getText().toString().trim();
                    String newDefaultRainbowRate = defaultRainbowRate.getText().toString().trim();
                    if (!newRackIP.equals(""))
                    {
                        UtilitiesClass.getInstance().SaveSharedPreferencesKey("settings", "RACK_IP", newRackIP);
                        rackIPText.setHint(newRackIP);
                        mainActivity.rackIP = newRackIP;
                        mainActivity.setConnectedToRack(false);
                        mainActivity.StartNewConnectionThread();
                    }
                    if(!newRackPort.equals(""))
                    {
                        UtilitiesClass.getInstance().SaveSharedPreferencesKey("settings", "RACK_PORT", newRackPort);
                        rackPortText.setHint(newRackPort);
                        mainActivity.rackPort = newRackPort;
                        mainActivity.setConnectedToRack(false);
                        mainActivity.StartNewConnectionThread();
                    }
                    if(!newDefaultRainbowRate.equals(""))
                    {
                        UtilitiesClass.getInstance().SaveSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE", newDefaultRainbowRate);
                        defaultRainbowRate.setHint(newDefaultRainbowRate);
                    }
                    Toaster.toast("Settings saved");
                    break;

                case R.id.reconnectRackButton:
                    mainActivity.StartNewConnectionThread();
                    break;

                case R.id.reconnectP1Button:
                    UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("p1-connect", mainActivity));
                    break;

                case R.id.reconnectP2Button:
                    UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("p2-connect", mainActivity));
                    break;
            }
        }
    };
}
