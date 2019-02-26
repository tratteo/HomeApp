package com.example.matteo.homeapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.helper.ItemTouchUIUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.matteo.homeapp.MainActivity;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.SendDataRunnable;
import com.example.matteo.homeapp.UtilitiesClass;

public class Pi1Fragment extends Fragment
{
    MainActivity mainActivity;
    Button windowPlugOnButton, windowPlugOffButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragmentpi1_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        UtilitiesClass.getInstance().HideSoftInputKeyboard(getView());

        windowPlugOnButton = getView().findViewById(R.id.windowPlugOnButton);
        windowPlugOffButton = getView().findViewById(R.id.windowPlugOffButton);
        windowPlugOnButton.setOnClickListener(clickListener);
        windowPlugOffButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.windowPlugOnButton:
                    if(mainActivity.isConnectedToRack())
                        UtilitiesClass.getInstance().executeRunnable(new SendDataRunnable("p1-windowplug_on", mainActivity));
                    break;
                case R.id.windowPlugOffButton:
                    if(mainActivity.isConnectedToRack())
                        UtilitiesClass.getInstance().executeRunnable(new SendDataRunnable("p1-windowplug_off", mainActivity));
                    break;
            }
        }
    };
}
