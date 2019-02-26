package com.example.matteo.homeapp.Runnables;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

import xdroid.toaster.Toaster;

public class SSHCommandRunnable implements Runnable
{
    private String IP, username, password, command;
    public SSHCommandRunnable(String IP, String username, String password, String command)
    {
        this.IP = IP;
        this.username = username;
        this.password = password;
        this.command = command;
    }

    @Override
    public void run()
    {
        try
        {
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, IP, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.setTimeout(5000);
            session.connect();
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();
            try{Thread.sleep(500 );}catch(Exception ignored){}
            channel.disconnect();
        }
        catch(JSchException e)
        {
            Toaster.toast(e.toString());
        }
    }
}
