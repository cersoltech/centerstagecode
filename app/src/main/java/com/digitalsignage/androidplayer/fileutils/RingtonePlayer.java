package com.digitalsignage.androidplayer.fileutils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

/**
 * QuickBlox team
 */
public class RingtonePlayer {

    private static final String TAG = RingtonePlayer.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private Context context;
    private Integer noOfTimePlay = 0;

    public RingtonePlayer(Context context, int resource) {
        this.context = context;
        Uri beepUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resource);
        mMediaPlayer = new MediaPlayer();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING);

            mMediaPlayer.setAudioAttributes(audioAttributesBuilder.build());
        } else {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }

        try {
            mMediaPlayer.setDataSource(context, beepUri);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(boolean looping,Integer repeat) {
        Log.i(TAG, "play");
        if (mMediaPlayer == null) {
            Log.i(TAG, "mediaPlayer isn't created ");
            return;
        }
        mMediaPlayer.setLooping(looping);
        int ringtoneDuration = mMediaPlayer.getDuration();
        Log.d("pppp", "onCompletion: called "+ Integer.toString(ringtoneDuration));
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("pppp", "onCompletion:");

               /*noOfTimePlay++;

               if (noOfTimePlay.equals(repeat))
               {
                   //mediaPlayer.release();
                   stop();
               }else {
                   //mMediaPlayer.stop();
                   mMediaPlayer.start();
                   //mediaPlayer.seekTo(0);
                   *//*new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           mMediaPlayer.start();
                       }
                   },1000);*//*
               }*/
            }
        });
       /* mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
               mediaPlayer.start();
            }
        });*/

        mMediaPlayer.start();
    }

    public MediaPlayer getMediaPlayerObj(){
        return mMediaPlayer;
    }

    public synchronized void stop() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}