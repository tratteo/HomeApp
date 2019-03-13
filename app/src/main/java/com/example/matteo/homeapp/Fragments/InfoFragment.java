package com.example.matteo.homeapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.R;

public class InfoFragment extends Fragment
{
    private MainActivity mainActivity;
    public TextView temperatureTextView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity)getActivity();
        return inflater.inflate(R.layout.fragment_info, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        temperatureTextView = getView().findViewById(R.id.temperatureTextView);
    }
}
