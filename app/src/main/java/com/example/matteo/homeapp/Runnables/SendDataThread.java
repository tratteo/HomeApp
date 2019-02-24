package com.example.matteo.homeapp.Runnables;

import com.example.matteo.homeapp.MainActivity;

public class SendDataThread implements Runnable
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
        if(mainActivity.connectedToRack)
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
                    mainActivity.connectedToRack = false;
            }
        }
    }
}
