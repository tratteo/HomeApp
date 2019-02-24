package com.example.matteo.homeapp.Interfaces;

public interface KillableRunnable extends Runnable
{
    void kill();
    boolean isRunning();
}
