package com.example.matteo.homeapp;


import android.content.Context;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;

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
}
