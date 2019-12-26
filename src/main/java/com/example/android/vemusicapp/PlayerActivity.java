package com.example.android.vemusicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {
    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper ="";
    private ImageView pausePlayBtn, nextBtn ,previousBtn;
    private TextView songNameTxt;
    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;
    private  String mode ="ON";

    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mySongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        checkVoiceCommandPermissions();


        pausePlayBtn=findViewById(R.id.play_pause_btn);
        nextBtn=findViewById(R.id.next_btn);
        previousBtn=findViewById(R.id.previous_btn);
        imageView=findViewById(R.id.logo);

        lowerRelativeLayout=findViewById(R.id.lower);
        voiceEnabledBtn=findViewById(R.id.voice_enabled_btn);
        songNameTxt=findViewById(R.id.songsName);



        parentRelativeLayout =findViewById(R.id.parentRelativeLayout);
        speechRecognizer= SpeechRecognizer.createSpeechRecognizer(PlayerActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        validateRecieveValuesAndStartPlaying();
        imageView.setBackgroundResource(R.drawable.logo);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String>matchesFound=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matchesFound != null){

                    if(mode.equals("ON")){

                        keeper =matchesFound.get(0);
                        if(keeper.equals("pause the song")|| keeper.equals("pause")){

                            pauseSong();
                            Toast.makeText(PlayerActivity.this, "Command = "+ keeper, Toast.LENGTH_LONG).show();
                        }
                        else if(keeper.equals("play the song")|| keeper.equals("play")){

                            playSong();
                            Toast.makeText(PlayerActivity.this, "Command = "+ keeper, Toast.LENGTH_LONG).show();
                        }

                        else if(keeper.equals("play next song")|| keeper.equals("play next")){

                            playNextSong();
                            Toast.makeText(PlayerActivity.this, "Command = "+ keeper, Toast.LENGTH_LONG).show();
                        }

                        else if(keeper.equals("play previous song")|| keeper.equals("play previous")){

                            playPreviousSong();
                            Toast.makeText(PlayerActivity.this, "Command = "+ keeper, Toast.LENGTH_LONG).show();
                        }
                    }

                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper="";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;

                }
                return false;
            }
        });

        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mode.equals("ON")){
                    mode = "OFF";
                    voiceEnabledBtn.setText("Voice Enabled Mode - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else{
                    mode = "ON";
                    voiceEnabledBtn.setText("Voice Enabled Mode - ON");
                    lowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });

        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playPauseSong();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myMediaPlayer.getCurrentPosition()>0){

                    playPreviousSong();

                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myMediaPlayer.getCurrentPosition()==0 || myMediaPlayer.getCurrentPosition()>0){
                    playNextSong();

                }
            }
        });


    }




    private void validateRecieveValuesAndStartPlaying(){

        if(myMediaPlayer != null){

            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mySongName = mySongs.get(position).getName();
        String songName =intent.getStringExtra("name");
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position= bundle.getInt("position",0);
        Uri uri =Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(PlayerActivity.this,uri);
        myMediaPlayer.start();
    }




    private void checkVoiceCommandPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){

                Intent intent =new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }


    private void playPauseSong(){

        imageView.setBackgroundResource(R.drawable.four);
        if(myMediaPlayer.isPlaying()){

            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();

        }
        else{

            pausePlayBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.five);
        }

    }

    private void playSong(){

        myMediaPlayer.start();

    }

    private void pauseSong(){

        myMediaPlayer.pause();
    }


    private void playNextSong(){

        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position =((position+1)%mySongs.size());

        Uri uri =Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(PlayerActivity.this,uri);

        mySongName= mySongs.get(position).toString();
        songNameTxt.setText(mySongName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.three);



        if(myMediaPlayer.isPlaying()){

            pausePlayBtn.setImageResource(R.drawable.pause);

        }
        else{

            pausePlayBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.five);
        }
    }


    private void playPreviousSong(){

        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position =((position-1)<0 ?(mySongs.size()-1) : (position-1));

        Uri uri =Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(PlayerActivity.this,uri);


        mySongName= mySongs.get(position).toString();
        songNameTxt.setText(mySongName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.two);



        if(myMediaPlayer.isPlaying()){

            pausePlayBtn.setImageResource(R.drawable.pause);

        }
        else{

            pausePlayBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.five);
        }


    }
}
