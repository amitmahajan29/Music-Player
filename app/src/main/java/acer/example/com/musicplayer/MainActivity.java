package acer.example.com.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{
    public static String folderPath; //the folderPath which we will give to access the songs in it
    public static String[] fileList; //storing the files of selected folder in an string array
    public static  boolean isPlaying=false;
    public static int currentPosition = 0; //for logic of prev, next in the listVew
    public FloatingActionButton fabPrev,fab,fabNext;
    private final int PERMISSIONS_REQUEST_CODE = 101; //To ask the user for the dialougeBox permission.
                                                      //We used to do that in manifest file and then going to settings and then allowing,
                                                        //to stop that redundancy we are doing this

    private void playSong(int position)
    {
       // MusicService.resume = 0;
        Intent iService = new Intent(MainActivity.this, MusicService.class);
        iService.putExtra("path",folderPath+fileList[position]);  //passing the exact address of the song in listView to the service
        startService(iService); //starting the service of playing song
        isPlaying = true;
        fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_stop)); //for changing the icon
        //after playing the song to stop icon.
    }

    private void stopSong()
    {
        //MusicService.resume = 0;
        Intent iService = new Intent(MainActivity.this,MusicService.class);
        stopService(iService);
        isPlaying = false;
        fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,android.R.drawable.ic_media_play));
    }

    private void listSongs()
    {
        try
        {
            folderPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/";
            File dir = new File(folderPath);
            //Below line is for selecting the files exclusively ending with .mp3 or .m4a
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String fileName)
                {
                    return fileName.endsWith(".mp3") || fileName.endsWith(".m4a");
                }
            };

            fileList = dir.list(filter); //This will filter accordingly
            Arrays.sort(fileList);
            ListView listView = (ListView) findViewById(R.id.lvSongs);

            //ArrayAdapter is used for customising the viewing of listItems in listview
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,fileList);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    MusicService.resume = 0;
                    currentPosition = position;
                    stopSong();
                    playSong(position);
                }
            });

        }
        catch(Exception e)
        {
            Toast.makeText(this, "Unexpected Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_CODE)
        {
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                listSongs();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Permission to access file required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void RegisterBR()
    {
        IntentFilter recieverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        HeadsetReceiver receiver = new HeadsetReceiver();
        registerReceiver(receiver,recieverFilter);
        //unregisterReceiver(receiver); Just extra things
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //This is for checking the permissions specified in settings of an app
        int permissionStorageState = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permissionStorageState == PackageManager.PERMISSION_GRANTED)
        {
            listSongs();
        }
        else
        {
            //Request the permission from user through a dialouge box.
            ActivityCompat.requestPermissions(MainActivity.this,
                                               new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                               PERMISSIONS_REQUEST_CODE);
        }

        RegisterBR();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabPrev = (FloatingActionButton) findViewById(R.id.fabPrev);
        fabNext = (FloatingActionButton) findViewById(R.id.fabNext);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(isPlaying)
                {
                    MusicService.resume = MusicService.mediaPlayer.getCurrentPosition();
                    stopSong();
                }
                else
                {
                    if(fileList.length > 0)
                    {
                        stopSong();
                        playSong(currentPosition);
                    }
                }
            }
        });

        fabPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(currentPosition > 0)
                {
                    currentPosition--;
                    MusicService.resume = 0;
                    stopSong();
                    playSong(currentPosition);
                }
            }
        });

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(currentPosition < fileList.length-1)
                {
                    currentPosition++;
                    MusicService.resume = 0;
                    stopSong();
                    playSong(currentPosition);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
