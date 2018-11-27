package com.example.matteo.homeapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    static Thread listenerThread;
    public static TextView toolbarConnectionText;
    public static String rackIP = "192.168.1.40";
    public static String rackPort = "7777";
    static ConnectToServerAsync connectToServerAsync;
    static ScheduledExecutorService connectionThreadService;
    static boolean connectedToRack = false;
    public static Socket rackSocket;
    static PrintWriter outToRack;

    NavigationView navigationDrawer;
    DrawerLayout drawerLayout;

    public static Context context;

    @Override
    protected void onStart()
    {
        super.onStart();
        rackIP = UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_IP", "192.168.1.40");
        rackPort = UtilitiesClass.GetSharedPreferencesKey("settings", "RACK_PORT", "7777");
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);
        try
        {
            if(!connectedToRack)
                connectionThreadService.shutdown();
            new SendDataToServerAsync().execute("disconnecting");
        }
        catch (Exception e) { }
    }
    @Override
    protected void onRestart()
    {
        super.onRestart();
        if(!connectedToRack)
            try { StartConnectionThread(); } catch (Exception e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

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

        if(!connectedToRack)
            StartConnectionThread();
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

    public void InflateFragment(Fragment _fragment)
    {
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, _fragment);
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
            case R.id.pi1:
                fragment = new Pi1Fragment();
                break;
            case R.id.pi2:
                fragment = new Pi2Fragment();
                break;
            case R.id.settings:
                fragment = new SettingsFragment();
                break;
        }
        if (fragment != null)
        {
            InflateFragment(fragment);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void StartConnectionThread()
    {
        toolbarConnectionText.setText("Trying to connect...");
        connectionThreadService = Executors.newScheduledThreadPool(1);
        connectionThreadService.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                if(IsConnectedToWiFi())
                {
                    connectToServerAsync = new ConnectToServerAsync();
                    connectToServerAsync.execute();
                }
            }
        }, 300, 2000, TimeUnit.MILLISECONDS);
    }

    public static class ConnectToServerAsync extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids)
        {
            if(!connectedToRack)
            {
                try
                {
                    rackSocket = new Socket(rackIP, Integer.parseInt(rackPort));
                    outToRack = new PrintWriter(rackSocket.getOutputStream());
                    return "connected";
                }
                catch (Exception e)
                {
                    return "failed";
                }
            }
            return "null";
        }

        @Override
        protected void onPostExecute(String v)
        {
            if(v.equals("connected"))
            {
                connectedToRack = true;
                String ipString = rackSocket.getInetAddress().toString().substring(1, rackSocket.getInetAddress().toString().length());
                toolbarConnectionText.setText("Connected to: " + ipString);
                connectionThreadService.shutdown();
                listenerThread = new Thread(new ListenerThread());
                listenerThread.start();
            }
        }
    }

    public static class SendDataToServerAsync extends AsyncTask<String, Void, Void>
    {
        String messageToSend;
        @Override
        protected Void doInBackground(String... voids)
        {
            messageToSend = voids[0];
            if(connectedToRack)
            {
                try
                {
                    outToRack.println(voids[0]);
                    outToRack.flush();
                }
                catch (Exception e) {}
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            if (messageToSend.equals("disconnecting"))
                connectedToRack = false;

            else if (messageToSend.substring(0, 2).equals("p2"))
                if(messageToSend.substring(3, 5).equals("on"))
                    Pi2Fragment.ChangeProgressBarsValue(255);
                else if(messageToSend.substring(3, 6).equals("off"))
                    Pi2Fragment.ChangeProgressBarsValue(0);
        }
    }

    public static boolean IsConnectedToWiFi()
    {
        WifiManager wifiManager = (WifiManager)MainActivity.context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getNetworkId() != -1;
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch(wifiStateExtra)
            {
                case WifiManager.WIFI_STATE_DISABLED:
                    if(!connectionThreadService.isShutdown())
                    connectionThreadService.shutdown();
                    toolbarConnectionText.setText("Activate Wi-Fi and retry");
                    break;
            }
        }
    };
}