package com.example.matteo.homeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.matteo.homeapp.Fragments.Pi2Fragment;

import java.util.Calendar;

public class UtilitiesClass
{
    private MainActivity mainActivity;
    private static UtilitiesClass instance;
    public static UtilitiesClass getInstance()
    {
        if(instance == null)
            instance = new UtilitiesClass();
        return instance;
    }

    public void setMainActivity(MainActivity mainActivity) {this.mainActivity = mainActivity;}

    public int GetSecondsFromHoursAndMinutes(int pickerHour, int pickerMinute)
    {
        Calendar taskCalendar = Calendar.getInstance();
        taskCalendar.set(Calendar.HOUR_OF_DAY, pickerHour);
        taskCalendar.set(Calendar.MINUTE, pickerMinute);

        if((pickerMinute < Calendar.getInstance().get(Calendar.MINUTE) && pickerHour == Calendar.getInstance().get(Calendar.HOUR_OF_DAY))|| pickerHour < Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            taskCalendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+1);
        else
            taskCalendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        long difference = taskCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        return (int) (difference/ (1000));
    }

    public void HideSoftInputKeyboard(View v)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)mainActivity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        Pi2Fragment.defaultRainbowRate = GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE");

        if(mainActivity.rackIP.equals(""))
            SaveSharedPreferencesKey("settings", "RACK_IP", "192.168.1.40");
        if(mainActivity.rackPort.equals(""))
            SaveSharedPreferencesKey("settings", "RACK_PORT", "7777");
        if(Pi2Fragment.defaultRainbowRate.equals(""))
            SaveSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE", "800");
    }
}
