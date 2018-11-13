package com.example.matteo.homeapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;


public class DefaultFragment extends Fragment
{
    Button reconnectButton, connectToP1, connectToP2, sendDataToRackButton;
    EditText rackCommandLine;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.default_fragment_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        reconnectButton = getView().findViewById(R.id.reconnectButton);
        connectToP1 = getView().findViewById(R.id.connectToP1);
        connectToP2 = getView().findViewById(R.id.connectToP2);
        sendDataToRackButton = getView().findViewById(R.id.sendDataToRack);
        rackCommandLine = getView().findViewById(R.id.rackCommandLine);

        sendDataToRackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(MainActivity.connectedToRack && !rackCommandLine.getText().equals(""))
                    new MainActivity.SendDataToServerAsync().execute("rack-" + rackCommandLine.getText());
            }
        });

        reconnectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!MainActivity.connectedToRack)
                {
                    MainActivity.StartConnectionThread();
                }
            }
        });
        connectToP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(MainActivity.connectedToRack)
                    new MainActivity.SendDataToServerAsync().execute("p1-connect");
            }
        });
        connectToP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(MainActivity.connectedToRack)
                    new MainActivity.SendDataToServerAsync().execute("p2-connect");
            }
        });

    }
}
