package com.example.oae;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class CustomSoundPool {

    SoundPool soundPool;

    public CustomSoundPool() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();

            soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(attributes)
                .build();
//        } else {
//            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
//        }

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1.0f, 0, 0, 0, 1.0f); //left channel
                soundPool.play(sampleId, 0, 1.0f, 0, 0, 1.0f); //right channel
            }
        });
    }

    public void playSound(String path) {
        if (soundPool != null) {
            soundPool.load(path, 1);
        }
    }

    public void release(){
        if (soundPool != null) {
            soundPool.release();
        }
    }
}