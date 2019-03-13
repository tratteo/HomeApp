package com.example.matteo.homeapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.SendDataRunnable;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;
import yuku.ambilwarna.AmbilWarnaDialog;

public class LEDFragment extends Fragment
{
    private MainActivity mainActivity;
    public static String defaultRainbowRate = "800";
    private Button ledOnButton, ledOffButton, rainbowstartButton, rainbowstopButton, colorpickerButton;
    private EditText rainbowrateText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity)getActivity();
        return inflater.inflate(R.layout.fragment_led, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        UtilitiesClass.getInstance().HideSoftInputKeyboard(getView());

        rainbowrateText = getView().findViewById(R.id.rainbowRateText);
        rainbowrateText.setHint(UtilitiesClass.getInstance().GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE"));

        colorpickerButton = getView().findViewById(R.id.colorpickerButton);
        rainbowstopButton = getView().findViewById(R.id.rainbowstopButton);
        rainbowstartButton = getView().findViewById(R.id.rainbowstartButton);
        ledOnButton = getView().findViewById(R.id.ledOnButton);
        ledOffButton = getView().findViewById(R.id.ledOffButton);

        colorpickerButton.setOnClickListener(clickListener);
        rainbowstopButton.setOnClickListener(clickListener);
        rainbowstartButton.setOnClickListener(clickListener);
        ledOffButton.setOnClickListener(clickListener);
        ledOnButton.setOnClickListener(clickListener);
    }

    //Color picker listener
    private AmbilWarnaDialog.OnAmbilWarnaListener colorPickerListener = new AmbilWarnaDialog.OnAmbilWarnaListener()
    {
        @Override
        public void onCancel(AmbilWarnaDialog dialog) { }

        @Override
        public void onOk(AmbilWarnaDialog dialog, int color)
        {
            String r = Integer.toString( (color >> 16) & 0xFF );
            String g = Integer.toString( (color >> 8) & 0xFF );
            String b = Integer.toString( (color) & 0xFF );
            if(mainActivity.isConnectedToRack())
            {
                UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("ar-r" + r, mainActivity));
                UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("ar-g" + g, mainActivity));
                UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("ar-b" + b, mainActivity));
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId()) {
                case R.id.ledOnButton:
                    if (mainActivity.isConnectedToRack()) {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.ARDUINO_PREFIX + "on", mainActivity));
                    }
                    break;

                case R.id.ledOffButton:
                    if (mainActivity.isConnectedToRack()) {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.ARDUINO_PREFIX + "off", mainActivity));
                    }
                    break;

                case R.id.rainbowstartButton:
                    if (mainActivity.isConnectedToRack()) {
                        String rate;
                        if (rainbowrateText.getText().toString().equals(""))
                            rate = UtilitiesClass.getInstance().GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE");
                        else
                            rate = rainbowrateText.getText().toString();
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.ARDUINO_PREFIX + "rainbowstart" + rate, mainActivity));
                    }
                    break;
                case R.id.rainbowstopButton:
                    if (mainActivity.isConnectedToRack()) {
                        //TODO change communication protocol
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.ARDUINO_PREFIX + "rainbowstop", mainActivity));
                    }
                    break;
                case R.id.colorpickerButton:
                    AmbilWarnaDialog dialog = new AmbilWarnaDialog(mainActivity, 0xFFFFFF, colorPickerListener);
                    dialog.show();
                    break;
            }
        }
    };
}
