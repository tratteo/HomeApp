package com.example.matteo.homeapp.Threads;

import com.example.matteo.homeapp.MainActivity;

public class SendDataThread extends Thread
{
    private boolean succeded;
    private String message;
    private MainActivity mainActivity;
    public SendDataThread(String message, MainActivity mainActivity)
    {
        this.message = message;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run()
    {
        if(Thread.interrupted())
            return;
        if(mainActivity.isConnectedToRack())
        {
            try
            {
                mainActivity.outToRack.println(message);
                mainActivity.outToRack.flush();
                succeded = true;
            }
            catch (Exception e) {}
            if(succeded)
            {
                if (message.equals("disconnecting"))
                    mainActivity.setConnectedToRack(false);
            }
        }
    }
}
