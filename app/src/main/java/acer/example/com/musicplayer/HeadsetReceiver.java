package acer.example.com.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class HeadsetReceiver extends BroadcastReceiver
{


    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        int state = intent.getIntExtra("state",-1);
        switch(state)
        {
            case 0:
                Toast.makeText(context, "Headset is unplugged!", Toast.LENGTH_SHORT).show();
                Intent iService = new Intent(context,MusicService.class);
                context.stopService(iService);
                break;

            case 1:
                Toast.makeText(context, "Headset Plugged!", Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(context, "Unknown State", Toast.LENGTH_SHORT).show();
        }
    }
}
