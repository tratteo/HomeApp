package com.example.matteo.homeapp;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    public static final String rackIP = "192.168.1.40";
    static ConnectToServerAsync connectToServerAsync;
    static ScheduledExecutorService connectionThreadService;
    static boolean connectedToRack = false;
    public static Socket rackSocket;
    static PrintWriter outToRack;

    NavigationView navigationDrawer;
    DrawerLayout drawerLayout;

    public static Context context;

    @Override
    protected void onStop()
    {
        super.onStop();
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

        InflateFragment(new DefaultFragment());
        context = getApplicationContext();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarConnectionText = findViewById(R.id.toolbarConnectionText);
        navigationDrawer = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);


        navigationDrawer.getMenu().getItem(0).setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationDrawer.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);

        if(!connectedToRack)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    StartConnectionThread();
                }
            }, 1600);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
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
        switch (item.getItemId()) {
            case R.id.defaultpage:
                fragment = new DefaultFragment();
                break;
            case R.id.pi1:
                fragment = new FragmentP1();
                break;
            case R.id.pi2:
                fragment = new FragmentPi2();
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
                else
                {
                    connectionThreadService.shutdown();
                    toolbarConnectionText.setText("Activate Wi-Fi and retry");
                }
            }
        }, 300, 1500, TimeUnit.MILLISECONDS);
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
                    rackSocket = new Socket(rackIP, 7777);
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
        String messageSent;
        @Override
        protected Void doInBackground(String... voids)
        {
            messageSent = voids[0];
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
            if(messageSent.equals("disconnecting"))
                connectedToRack = false;
        }
    }

    public static boolean IsConnectedToWiFi()
    {
        WifiManager wifiManager = (WifiManager)MainActivity.context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getNetworkId() != -1;
    }

}