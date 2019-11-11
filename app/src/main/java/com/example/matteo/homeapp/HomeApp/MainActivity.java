package com.example.matteo.homeapp.HomeApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.core.app.*;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.matteo.homeapp.Fragments.InfoFragment;
import com.example.matteo.homeapp.Fragments.PlugsFragment;
import com.example.matteo.homeapp.Fragments.LEDFragment;
import com.example.matteo.homeapp.Fragments.RackFragment;
import com.example.matteo.homeapp.Fragments.SettingsFragment;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.ConnectionRunnable;
import com.example.matteo.homeapp.Runnables.ListenerRunnable;
import com.example.matteo.homeapp.Runnables.SendDataRunnable;

import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private ConnectionRunnable connectionRunnable = null;
    public ListenerRunnable listenerRunnable;
    public TextView toolbarConnectionText;
    public String rackIP = "192.168.1.40";
    public String rackPort = "7777";
    public Socket rackSocket;
    public PrintWriter outToRack;

    public boolean tratPiConnected = false, guizPiConnected = false, arduinoConnected = false;
    public synchronized void SetP1Connected(boolean status){tratPiConnected = status;}

    //Inflated fragments
    private Fragment currentFragment = null;
    public Fragment getCurrentFragment() {return currentFragment;}

    private NavigationView navigationDrawer;
    private DrawerLayout drawerLayout;

    private boolean connectedToRack = false;
    public synchronized boolean isConnectedToRack() {return connectedToRack;}
    public synchronized void setConnectedToRack(boolean connectedToRack) {this.connectedToRack = connectedToRack;}

    @Override
    protected void onResume()
    {
        super.onResume();
        UtilitiesClass.getInstance().LoadAppPreferences();
        StartConnectionThread();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if(connectionRunnable != null && connectionRunnable.isRunning())
            connectionRunnable.kill();

        if(listenerRunnable != null)
            if(listenerRunnable.isRunning())
                listenerRunnable.kill();

        if(connectedToRack)
            UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("disconnecting", this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UtilitiesClass.getInstance().setMainActivity(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarConnectionText = findViewById(R.id.toolbarConnectionText);
        navigationDrawer = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationDrawer.getMenu().getItem(0).setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationDrawer.setNavigationItemSelectedListener(this);

        currentFragment = new RackFragment();
        InflateFragment(currentFragment);
        navigationDrawer.setCheckedItem(R.id.rack);
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void InflateFragment(Fragment fragment)
    {
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        Fragment fragment = null;
        switch (item.getItemId())
        {
            case R.id.rack:
                fragment = new RackFragment();
                break;
            case R.id.plugs:
                fragment = new PlugsFragment();
                break;
            case R.id.led:
                fragment = new LEDFragment();
                break;
            case R.id.settings:
                fragment = new SettingsFragment();
                break;
            case R.id.info:
                fragment = new InfoFragment();
                break;
        }
        if (fragment != null)
        {
            currentFragment = fragment;
            InflateFragment(fragment);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void StartConnectionThread()
    {
        toolbarConnectionText.setText("Trying to connect...");
        connectionRunnable = new ConnectionRunnable(this);
        UtilitiesClass.getInstance().ExecuteRunnable(connectionRunnable);
    }
}