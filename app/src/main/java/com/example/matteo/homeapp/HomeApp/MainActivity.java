package com.example.matteo.homeapp.HomeApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    //Inflated fragments
    public RackFragment rackFragment;
    public LEDFragment ledFragment;
    public PlugsFragment plugsFragment;
    public SettingsFragment settingsFragment;
    public InfoFragment infoFragment;

    private NavigationView navigationDrawer;
    private DrawerLayout drawerLayout;

    private boolean connectedToRack = false;
    public synchronized boolean isConnectedToRack() {return connectedToRack;}
    public synchronized void setConnectedToRack(boolean connectedToRack) {this.connectedToRack = connectedToRack;}

    @Override
    protected void onStart()
    {
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
        super.onStart();
        UtilitiesClass.getInstance().LoadAppPreferences();
        if(IsConnectedToWiFi())
            StartConnectionThread();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);

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

        InflateFragment(new RackFragment());
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
                rackFragment = (RackFragment)fragment;
                break;
            case R.id.plugs:
                fragment = new PlugsFragment();
                plugsFragment = (PlugsFragment)fragment;
                break;
            case R.id.led:
                fragment = new LEDFragment();
                ledFragment = (LEDFragment)fragment;
                break;
            case R.id.settings:
                fragment = new SettingsFragment();
                settingsFragment = (SettingsFragment)fragment;
                break;
            case R.id.info:
                fragment = new InfoFragment();
                infoFragment = (InfoFragment)fragment;
                break;
        }
        if (fragment != null)
        {
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

    public boolean IsConnectedToWiFi()
    {
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getNetworkId() != -1;
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch(wifiStateExtra)
            {
                case WifiManager.WIFI_STATE_DISABLED:
                    if(connectionRunnable != null && connectionRunnable.isRunning())
                        connectionRunnable.kill();
                    toolbarConnectionText.setText("Activate Wi-Fi and retry");
                    connectedToRack = false;
                    break;
            }
        }
    };
}