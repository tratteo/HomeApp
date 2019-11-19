package com.example.matteo.homeapp.Runnables;

import android.util.Log;
import android.widget.Toast;

import com.example.matteo.homeapp.Fragments.InfoFragment;
import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;
import com.example.matteo.homeapp.Interfaces.KillableRunnable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class VideoReceiverThread implements KillableRunnable
{
    InfoFragment infoFragment = null;
    private MainActivity mainActivity;
    DatagramSocket socket;
    DatagramPacket packet;
    byte[] receiveData, frame;
    byte[] handshakeData = new byte[3];
    boolean stop = false;
    int packetCount = 0, lastCopyIndex = 0, length, frameSize, protocolPacketNumber, packetFraction;

    public VideoReceiverThread(MainActivity mainActivity, InfoFragment infoFragment)
    {
        this.infoFragment = infoFragment;
        this.mainActivity = mainActivity;
        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("stream-on", mainActivity));
    }

    @Override
    public void run()
    {
        Log.d("DEBUGGING", "Thread starting");
        try
        {
            Thread.sleep(1000);
            socket = new DatagramSocket();
            DatagramPacket handShake = new DatagramPacket(handshakeData, handshakeData.length, InetAddress.getByName(mainActivity.rackIP), 7676);
            socket.send(handShake);
            Log.d("DEBUGGING", "HandShake Sent");
            Log.d("DEBUGGING", "Waiting for server protocol info");

            packet = new DatagramPacket(handshakeData, handshakeData.length);
            socket.receive(packet);
            UtilitiesClass.getInstance().ExecuteOnMainThread(() -> Toast.makeText(mainActivity.getBaseContext(),"Protocol initialized, initiating streaming", Toast.LENGTH_LONG).show());
            byte[] protocolData = packet.getData();
            byte a = protocolData[0];
            byte b = protocolData[1];
            byte c = protocolData[2];
            protocolPacketNumber = (int)c;
            int packetSize = ((a << 8) | (b & 0xFF));
            receiveData = new byte[packetSize];
            Log.d("DEBUGGING", "Protocol initialized, packetSize: "+packetSize+", packetNumber: " +protocolPacketNumber);
            packet = new DatagramPacket(receiveData, receiveData.length);
        }
        catch (Exception e)
        {
            Log.d("DEBUGGING", e.toString());
        }
        while(!stop)
        {
            packetCount = 0;
            lastCopyIndex = 0;
            try {
                byte[] received;

                do
                {
                    socket.receive(packet);
                    received = packet.getData();

                } while(received[received.length - 1] != (byte) 'S');
                //First packet has been received
                packetCount++;
                byte a = received[received.length - 2];
                byte b = received[received.length - 3];
                length = ((a << 8) | (b & 0xFF));
                packetFraction = length;
                lastCopyIndex = 0;
                a = received[received.length - 4];
                b = received[received.length - 5];
                frameSize = ((a << 8) | (b & 0xFF));
                frame = new byte[frameSize];
                //Log.d("DEBUGGING", "Frame initialized with size: " +frameSize);
                System.arraycopy(received, 0, frame, lastCopyIndex, length);
                //Log.d("DEBUGGING", packetCount+"°  Copied from: "+lastCopyIndex+", to: "+(lastCopyIndex+length-1)+", actual size: "+length);
                lastCopyIndex += length;

                do
                {
                    socket.receive(packet);
                    received = packet.getData();
                    packetCount++;
                    a = received[received.length - 2];
                    b = received[received.length - 3];
                    length = ((a << 8) | (b & 0xFF));
                    if(packetFraction != length && received[received.length-1] != (byte)'E')
                        break;
                    System.arraycopy(received, 0, frame, lastCopyIndex, length);
                    //Log.d("DEBUGGING", packetCount+"°  Copied from: "+lastCopyIndex+", to: "+(lastCopyIndex+length-1)+", actual size: "+length);
                    lastCopyIndex += length;

                }while(received[received.length-1] != (byte)'E' || packetCount < protocolPacketNumber);
                //Log.d("DEBUGGING", "Last packet");

                if(packetCount == protocolPacketNumber)
                {
                    UtilitiesClass.getInstance().ExecuteOnMainThread(() -> infoFragment.SetVideoCurrentFrame(frame));
                }
                else
                {
                    Log.d("DEBUGGING", "Packets lost");
                    continue;
                }

            }
            catch (Exception e)
            {
                Log.d("DEBUGGING", e.toString());
            }
        }
    }

    @Override
    public boolean isRunning() {return !stop;}
    @Override
    public void kill()
    {
        Log.d("DEBUGLISTENER", "On kill");
        stop = true;
        if(socket != null)
            socket.close();
        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("stream-off", mainActivity));
    }
}
