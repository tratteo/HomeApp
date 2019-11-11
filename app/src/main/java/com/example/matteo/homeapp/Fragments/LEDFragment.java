package com.example.matteo.homeapp.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
    //Window LED
    private Button windowLedOnButton, windowLedOffButton, windowRainbowstartButton, windowRainbowstopButton, windowColorPickerButton;
    private EditText windowRainbowRateText;
    //Shelf LED
    private Button shelfLedOnButton, shelfLedOffButton, shelfRainbowstartButton, shelfRainbowstopButton, shelfColorPickerButton;
    private EditText shelfRainbowRateText;

    private AmbilWarnaDialog dialog;
    private String colorPickerPrefix = UtilitiesClass.ARDUINO_PREFIX;

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
        InitializeVariables();
    }

    private void InitializeVariables()
    {
        //Window LED
        windowRainbowRateText = getView().findViewById(R.id.windowRainbowRateText);
        windowRainbowRateText.setHint(UtilitiesClass.getInstance().GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE"));

        windowColorPickerButton = getView().findViewById(R.id.windowColorpickerButton);
        windowRainbowstopButton = getView().findViewById(R.id.windowRainbowstopButton);
        windowRainbowstartButton = getView().findViewById(R.id.windowRainbowstartButton);
        windowLedOnButton = getView().findViewById(R.id.windowLedOnButton);
        windowLedOffButton = getView().findViewById(R.id.windowLedOffButton);

        windowColorPickerButton.setOnClickListener(clickListener);
        windowRainbowstopButton.setOnClickListener(clickListener);
        windowRainbowstartButton.setOnClickListener(clickListener);
        windowLedOffButton.setOnClickListener(clickListener);
        windowLedOnButton.setOnClickListener(clickListener);

        //Shelf LED
        shelfRainbowRateText = getView().findViewById(R.id.shelfRainbowRateText);
        shelfRainbowRateText.setHint(UtilitiesClass.getInstance().GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE"));

        shelfColorPickerButton = getView().findViewById(R.id.shelfColorpickerButton);
        shelfRainbowstopButton = getView().findViewById(R.id.shelfRainbowstopButton);
        shelfRainbowstartButton = getView().findViewById(R.id.shelfRainbowstartButton);
        shelfLedOnButton = getView().findViewById(R.id.shelfLedOnButton);
        shelfLedOffButton = getView().findViewById(R.id.shelfLedOffButton);

        shelfColorPickerButton.setOnClickListener(clickListener);
        shelfRainbowstopButton.setOnClickListener(clickListener);
        shelfRainbowstartButton.setOnClickListener(clickListener);
        shelfLedOnButton.setOnClickListener(clickListener);
        shelfLedOffButton.setOnClickListener(clickListener);
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
                UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(colorPickerPrefix + "r" + r, mainActivity));
                UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(colorPickerPrefix + "g" +  g, mainActivity));
                UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(colorPickerPrefix + "b" + b, mainActivity));
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.shelfLedOnButton:
                    if (mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.PI1_PREFIX+ "on", mainActivity));
                    }
                    break;
                case R.id.shelfLedOffButton:
                    if (mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.PI1_PREFIX + "off", mainActivity));
                    }
                    break;

                case R.id.shelfRainbowstartButton:
                    if (mainActivity.isConnectedToRack())
                    {
                        String rate;
                        if (shelfRainbowRateText.getText().toString().equals(""))
                            rate = UtilitiesClass.getInstance().GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE");
                        else
                            rate = shelfRainbowRateText.getText().toString();
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.PI1_PREFIX + "rainbowstart" + rate, mainActivity));
                    }
                    break;
                case R.id.shelfRainbowstopButton:
                    if (mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.PI1_PREFIX + "rainbowstop", mainActivity));
                    }
                    break;
                case R.id.shelfColorpickerButton:
                    colorPickerPrefix = UtilitiesClass.PI1_PREFIX;
                    dialog = new AmbilWarnaDialog(mainActivity, 0xFFFFFF, colorPickerListener);
                    dialog.show();
                    break;

                case R.id.windowLedOnButton:
                    if (mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.ARDUINO_PREFIX + "on", mainActivity));
                    }
                    break;

                case R.id.windowLedOffButton:
                    if (mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.ARDUINO_PREFIX + "off", mainActivity));
                    }
                    break;

                case R.id.windowRainbowstartButton:
                    if (mainActivity.isConnectedToRack())
                    {
                        String rate;
                        if (windowRainbowRateText.getText().toString().equals(""))
                            rate = UtilitiesClass.getInstance().GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE");
                        else
                            rate = windowRainbowRateText.getText().toString();
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.ARDUINO_PREFIX + "rainbowstart" + rate, mainActivity));
                    }
                    break;
                case R.id.windowRainbowstopButton:
                    if (mainActivity.isConnectedToRack())
                    {
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable(UtilitiesClass.ARDUINO_PREFIX + "rainbowstop", mainActivity));
                    }
                    break;
                case R.id.windowColorpickerButton:
                    colorPickerPrefix = UtilitiesClass.ARDUINO_PREFIX;
                    dialog = new AmbilWarnaDialog(mainActivity, 0xFFFFFF, colorPickerListener);
                    dialog.show();
                    break;
            }
        }
    };
}
