package com.example.matteo.homeapp.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.SendDataRunnable;
import com.example.matteo.homeapp.Runnables.VideoReceiverThread;

public class InfoFragment extends Fragment
{
    private InfoFragment context;
    public int udpMappedPort = 0;
    private MainActivity mainActivity;
    private Switch videoStreamSwitch;
    private VideoReceiverThread videoReceiverThread = null;
    public TextView temperatureTextView = null;
    public ImageView videoFrame = null;
    public TextView tratPiConnectText = null;
    public TextView guizPiConnectText = null;
    public TextView arduinoConnectText = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        context = this;
        mainActivity = (MainActivity)getActivity();
        return inflater.inflate(R.layout.fragment_info, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        temperatureTextView = getView().findViewById(R.id.temperatureTextView);
        tratPiConnectText = getView().findViewById(R.id.tratPiConnectedText);
        guizPiConnectText = getView().findViewById(R.id.guizPiConnectedText);
        arduinoConnectText = getView().findViewById(R.id.arduinoConnectedText);
        videoFrame = getView().findViewById(R.id.videoStreamFrame);
        videoStreamSwitch = getView().findViewById(R.id.videoStreamSwitch);
        SetDeviceStatus("p1", mainActivity.tratPiConnected);
        SetDeviceStatus("p2", mainActivity.guizPiConnected);
        SetDeviceStatus("arduino", mainActivity.arduinoConnected);

        videoStreamSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!mainActivity.isConnectedToRack()) return;

            if(isChecked)
            {
                videoReceiverThread = new VideoReceiverThread(mainActivity, context);
                UtilitiesClass.getInstance().ExecuteRunnable(videoReceiverThread);
            }
            else
            {
                if(videoReceiverThread != null)
                    videoReceiverThread.kill();
                try{Thread.sleep(100);}catch(Exception e){}
                videoFrame.setImageBitmap(null);
            }
        });
}

    public void SetTemperatureLabel(String temperature)
    {
        temperatureTextView.setText(temperature + "°");
        temperatureTextView.setTextColor(UtilitiesClass.getInstance().GetColorFromTemperature(Float.parseFloat(temperature)));
    }

    public void SetDeviceStatus(String device, boolean status)
    {
        switch (device)
        {
            case "p1":
                if(status)
                {
                    tratPiConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.enabled));
                }
                else
                {
                    tratPiConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.disabled));
                }
                break;
            case "p2":
                if(status)
                {
                    guizPiConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.enabled));
                }
                else
                {
                    guizPiConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.disabled));
                }
                break;
            case "arduino":
                if(status)
                {
                    arduinoConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.enabled));
                }
                else
                {
                    arduinoConnectText.setTextColor(ContextCompat.getColor(this.getContext(), R.color.disabled));
                }
                break;
        }
    }

    public void SetVideoCurrentFrame(byte[] frameBuf)
    {
        Bitmap bmp = BitmapFactory.decodeByteArray(frameBuf, 0, frameBuf.length);
        videoFrame.setImageBitmap(Bitmap.createScaledBitmap(bmp, videoFrame.getWidth(), videoFrame.getHeight(), false));
    }

    public void KillVideoReceiverThread()
    {
        if(videoReceiverThread != null && videoReceiverThread.isRunning())
        {
            videoReceiverThread.kill();
        }
    }

    public synchronized void SetUDPMappedPort(int port)
    {
        udpMappedPort = port;
    }
    public synchronized int GetUDPMappedPort(){return udpMappedPort;}
}
