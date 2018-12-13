package com.example.matteo.homeapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jcraft.jsch.*;

import java.util.Calendar;
import java.util.Properties;

import xdroid.toaster.Toaster;

public class UtilitiesClass
{
    public static int GetSecondsFromHoursAndMinutes(int pickerHour, int pickerMinute)
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

    public static void HideSoftInputKeyboard(View v)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)MainActivity.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void SaveSharedPreferencesKey(String sharedPreference, String key, String value)
    {
        SharedPreferences settings = MainActivity.context.getSharedPreferences(sharedPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(key, value);
        edit.apply();
        edit.commit();
    }

    public static String GetSharedPreferencesKey(String sharedPreference, String key, String defaultValue)
    {
        SharedPreferences settings = MainActivity.context.getSharedPreferences(sharedPreference, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void RunSSHCommand(String IP, String username, String password, String command)
    {
        new RunSSHAsync().execute(IP, username, password, command);
    }

    private static class RunSSHAsync extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... parameters)
        {
            try{
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jsch = new JSch();
                Session session = jsch.getSession(parameters[1], parameters[0], 22);
                session.setPassword(parameters[2]);
                session.setConfig(config);
                session.setTimeout(5000);
                session.connect();
                ChannelExec channel = (ChannelExec)session.openChannel("exec");
                channel.setCommand(parameters[3]);
                channel.connect();
                try{Thread.sleep(500 );}catch(Exception e){}
                channel.disconnect();
                return true;
            }
            catch(JSchException e)
            {
                Toaster.toast(e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if(result)
            {
                Toaster.toast("SSH executed");

            }
            else
                Toaster.toast("Unable to execute SSH");
        }
    }

    public static void LoadAppPreferences()
    {
        MainActivity.rackIP = UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_IP", null);
        MainActivity.rackPort = UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_PORT", null);
        Pi2Fragment.defaultRainbowRate = UtilitiesClass.GetSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE", null);

        if(MainActivity.rackIP.equals(""))
            UtilitiesClass.SaveSharedPreferencesKey("settings", "RACK_IP", "192.168.1.40");
        if(MainActivity.rackPort.equals(""))
            UtilitiesClass.SaveSharedPreferencesKey("settings", "RACK_PORT", "7777");
        if(Pi2Fragment.defaultRainbowRate.equals(""))
            UtilitiesClass.SaveSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE", "800");
    }
}
