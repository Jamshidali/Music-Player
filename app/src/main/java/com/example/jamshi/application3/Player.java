package com.example.jamshi.application3;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Player extends ActionBarActivity implements View.OnClickListener {
   static MediaPlayer mp;
    ArrayList<File> mySongs;
    Uri u;
    int position=0;
    Thread updateSeekBar;

    SeekBar sb;
    Button btnPlay,btnFF,btnFB,btnNext,btnPrev;

    Intent i;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlay = (Button)findViewById(R.id.btnPlay);
        btnFF   = (Button)findViewById(R.id.btnFF);
        btnFB   = (Button)findViewById(R.id.btnFB);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnPrev = (Button)findViewById(R.id.btnPrev);

        btnPlay.setOnClickListener(this);
        btnFF.setOnClickListener(this);
        btnFB.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);

        sb = (SeekBar) findViewById(R.id.seekBar);
        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration){

                    try {
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //super.run();
            }
        };

        if (mp!=null){
            mp.stop();
            mp.release();
        }

         i = getIntent();
         b = i.getExtras();
        ArrayList<File> mySongs =(ArrayList) b.getParcelableArrayList("songList");

        position = b.getInt("pos",0);
        u = Uri.parse(mySongs.get(position).toString());
        //mp = MediaPlayer.create(getApplicationContext(), u);
        mp = new MediaPlayer();
        try {
            mp.setDataSource(getApplicationContext(),u);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
        sb.setMax(mp.getDuration());
        updateSeekBar.start();

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              mp.seekTo(seekBar.getProgress());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        ArrayList<File> mySongs =(ArrayList) b.getParcelableArrayList("songList");
        position = b.getInt("pos");
        switch (id){
            case R.id.btnPlay:
                //Toast.makeText(getApplicationContext(),"position is:"+position,Toast.LENGTH_LONG).show();
                if (mp!=null && mp.isPlaying()){
                    btnPlay.setText(">");
                    mp.pause();
                }else {
                    btnPlay.setText("||");
                    mp.start();
                }
               break;
            case R.id.btnFF:
                mp.seekTo(mp.getCurrentPosition()+5000);
                break;
            case R.id.btnFB:
                mp.seekTo(mp.getCurrentPosition()-5000);
                break;
            case R.id.btnNext:
                mp.stop();
                //mp.release();

               // ArrayList<File> mySongs =(ArrayList) b.getParcelableArrayList("songList");
                //position = b.getInt("pos",0);

                mp.reset();
                if(position < (mySongs.size() - 1)){
                    position = position+1;
                }else{
                    position = 0;
                }
                  u = Uri.parse(mySongs.get(position).toString());
                  mp = MediaPlayer.create(getApplicationContext(),u);
                mp.start();
                sb.setMax(mp.getDuration());
                break;
            case R.id.btnPrev:
                mp.stop();
                //mp.release();
                if(position > 0){
                    position = position-1;
                }
                else{
                    position = mySongs.size()-1;
                }
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(),u);

                mp.start();
                sb.setMax(mp.getDuration());
                break;
        }

    }
}
