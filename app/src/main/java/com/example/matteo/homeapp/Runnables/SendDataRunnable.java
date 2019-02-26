package com.example.matteo.homeapp.Runnables;

import com.example.matteo.homeapp.MainActivity;

public class SendDataRunnable implements Runnable
{
    private boolean succeded = false;
    private String message;
    private MainActivity mainActivity;
    public SendDataRunnable(String message, MainActivity mainActivity)
    {
        this.message = message;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run()
    {
        if(mainActivity.isConnectedToRack())
        {
            try
            {
                mainActivity.outToRack.println(message);
                mainActivity.outToRack.flush();
                succeded = true;
            }
            catch (Exception e)
            {
                succeded = false;
            }
            if(succeded)
            {
                if (message.equals("disconnecting"))
                    mainActivity.setConnectedToRack(false);
            }
        }
    }
}
