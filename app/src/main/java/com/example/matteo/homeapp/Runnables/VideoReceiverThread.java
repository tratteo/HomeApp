package com.example.matteo.homeapp.Runnables;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import com.example.matteo.homeapp.Fragments.InfoFragment;
import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;
import com.example.matteo.homeapp.Interfaces.KillableRunnable;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class VideoReceiverThread implements KillableRunnable
{
    InfoFragment infoFragment = null;
    private boolean stop = false;
    DatagramSocket UDPSocket;
    DatagramPacket recvPacket;
    byte[] recvBuf = new byte[65536];
    private MainActivity mainActivity;
    public VideoReceiverThread(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }
    @Override
    public void run()
    {
        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("stream-on", mainActivity));
        try
        {
            UDPSocket = new DatagramSocket(7777);
        }
        catch(Exception e)
        {
            kill();
        }
        if(mainActivity.getCurrentFragment().getClass().equals(InfoFragment.class))
        {
            infoFragment = (InfoFragment) mainActivity.getCurrentFragment();
        }
        else
        {
            kill();
        }
        while(!stop)
        {
            try {
                recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
                UDPSocket.receive(recvPacket);
                byte[] received = recvPacket.getData();
                UtilitiesClass.getInstance().ExecuteOnMainThread(() ->  infoFragment.SetVideoCurrentFrame(received));

            }
            catch (Exception e)
            {
            }
        }
    }

    @Override
    public boolean isRunning() {return !stop;}
    @Override
    public void kill()
    {
        Log.d("FRAGMENT", "On kill");
        stop = true;
        UDPSocket.close();
        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("stream-off", mainActivity));
    }
}
