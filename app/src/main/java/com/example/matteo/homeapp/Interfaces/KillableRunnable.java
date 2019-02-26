package com.example.matteo.homeapp.Interfaces;

public interface KillableRunnable extends Runnable
{
    void run();
    boolean isRunning();
    void kill();
}
