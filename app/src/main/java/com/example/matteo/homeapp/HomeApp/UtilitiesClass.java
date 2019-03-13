package com.example.matteo.homeapp.HomeApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.example.matteo.homeapp.Fragments.LEDFragment;

import java.util.Calendar;

public class UtilitiesClass
{
    public static final String ARDUINO_PREFIX = "ar-";
    public static final String PI1_PREFIX = "p1-";
    public static final String PI2_PREFIX = "p2-";

    private static UtilitiesClass instance;
    public static UtilitiesClass getInstance()
    {
        if(instance == null)
            instance = new UtilitiesClass();
        return instance;
    }

    private MainActivity mainActivity;
    public void setMainActivity(MainActivity mainActivity) {this.mainActivity = mainActivity;}


    public Thread ExecuteRunnable(Runnable runnable)
    {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    public int GetSecondsFromHoursAndMinutes(int hour, int minute)
    {
        Calendar taskCalendar = Calendar.getInstance();
        taskCalendar.set(Calendar.HOUR_OF_DAY, hour);
        taskCalendar.set(Calendar.MINUTE, minute);

        if((minute < Calendar.getInstance().get(Calendar.MINUTE) && hour == Calendar.getInstance().get(Calendar.HOUR_OF_DAY))|| hour < Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            taskCalendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+1);
        else
            taskCalendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        long difference = taskCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        return (int) (difference/ (1000));
    }

    public void HideSoftInputKeyboard(View v)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)mainActivity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void SaveSharedPreferencesKey(String sharedPreference, String key, String value)
    {
        SharedPreferences settings = mainActivity.getApplicationContext().getSharedPreferences(sharedPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(key, value);
        edit.apply();
        edit.commit();
    }

    public String GetSharedPreferencesKey(String sharedPreference, String key)
    {
        SharedPreferences settings = mainActivity.getApplicationContext().getSharedPreferences(sharedPreference, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public void LoadAppPreferences()
    {
        mainActivity.rackIP = GetSharedPreferencesKey("settings", "RACK_IP");
        mainActivity.rackPort = GetSharedPreferencesKey("settings", "RACK_PORT");
        LEDFragment.defaultRainbowRate = GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE");

        if(mainActivity.rackIP.equals(""))
            SaveSharedPreferencesKey("settings", "RACK_IP", "192.168.1.40");
        if(mainActivity.rackPort.equals(""))
            SaveSharedPreferencesKey("settings", "RACK_PORT", "7777");
        if(LEDFragment.defaultRainbowRate.equals(""))
            SaveSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE", "800");
    }

    public boolean IsStringFloatConvertible(String sentence)
    {
        if(sentence.equals(null))
            return false;
        boolean succeded;
        try
        {
            Float.parseFloat(sentence);
            succeded = true;
        }
        catch (Exception e) {succeded  = false;}
        return succeded;
    }

    public int GetColorFromTemperature(float temperature)
    {
        float red = 0, green = 0, blue = 0;

        if(temperature < 10f)
        {
            red = 0;
            green = 0;
            blue = 255f;
        }
        else if( temperature >= 10 && temperature < 16f)
        {
            red = 0;
            green = 42.5f * (temperature - 10f);
            blue = 255f;
        }
        else if(temperature >= 16f && temperature < 20f)
        {
            red = 0;
            green = 255f;
            blue = -63.75f * (temperature - 20f);
        }
        else if(temperature >= 20f && temperature < 25f)
        {
            red = 51f * (temperature - 20f);
            green = 255f;
            blue = 0;
        }
        else if(temperature >= 25f && temperature <= 36f)
        {
            red = 255f;
            green = -23.15f * (temperature - 36f);
            blue = 0;
        }
        else if(temperature > 36f)
        {
            red = 255f;
            green = 0;
            blue = 0;
        }

        int intRed = (int)red;
        int intGreen = (int)green;
        int intBlue = (int)blue;
        int color = (0xFF & 0xFF) << 24 | (intRed & 0xFF) << 16 | (intGreen & 0xFF) << 8 | (intBlue & 0xFF);
        return color;
    }
}
