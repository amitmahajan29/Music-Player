package acer.example.com.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

//3 lifecycles.
//1-OnCreate, 2-OnStart, 3-OnDestroy
public class MusicService extends Service
{

    public static MediaPlayer mediaPlayer;
    public static int resume = 0;
    public MusicService()
    {

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        return null; //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true); //Ek gana loop mai chalte rehega
        mediaPlayer.setVolume(1.0f,1.0f);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try
        {
            String path = intent.getExtras().getString("path");
            mediaPlayer.setDataSource(path); //To play the selected song from listView.
            mediaPlayer.prepare(); //
            mediaPlayer.seekTo(resume);
            mediaPlayer.start();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Could not play the selected song "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() //Gets fired when the StopService is called.
    {
        //resume = mediaPlayer.getCurrentPosition();
        super.onDestroy();
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
