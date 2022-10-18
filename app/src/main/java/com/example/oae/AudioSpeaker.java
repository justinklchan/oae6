package com.example.oae;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioSpeaker extends Thread {

    AudioTrack track1;
    int SamplingFreq;
    Context mycontext;
    short[] samples;
    int speakerType;
    AudioManager man;
    boolean stereo;

    int[] streams = new int[]{AudioManager.STREAM_MUSIC,
            AudioManager.STREAM_ACCESSIBILITY, AudioManager.STREAM_ALARM,
            AudioManager.STREAM_DTMF, AudioManager.STREAM_NOTIFICATION,
            AudioManager.STREAM_RING, AudioManager.STREAM_SYSTEM,
            AudioManager.STREAM_VOICE_CALL};

    public AudioSpeaker(Context mycontext,short[] samples,int samplingFreq, int speakerType, boolean stereo, double vol) {
        this.mycontext = mycontext;
        this.stereo = stereo;
        man = (AudioManager)mycontext.getSystemService(Context.AUDIO_SERVICE);
        for (Integer i : streams) {
            man.setStreamMute(i,true);
        }
        man.setStreamMute(speakerType,false);
        man.setStreamVolume(speakerType,(int)(man.getStreamMaxVolume(speakerType)*vol),0);

        SamplingFreq = samplingFreq;
        this.samples = samples;
        this.speakerType = speakerType;

//        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
//                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
//                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);

        if (stereo) {
            //1 short is 2 bytes
            track1 = new AudioTrack(speakerType, SamplingFreq, AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT, samples.length * 2, AudioTrack.MODE_STATIC);
        }
        else {
            track1 = new AudioTrack(speakerType, SamplingFreq, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, samples.length * 2, AudioTrack.MODE_STATIC);
        }
        Log.e("asdf","vol "+track1.getMaxVolume());
        int ret = track1.write(samples,0,samples.length);
//        Log.e("asdf","write "+ret);
    }

    public void play(int loops, float vol) {
        try {
//            setVolume(1);
            Log.e("earapp","maxvol "+man.getStreamMaxVolume(speakerType)+"");

            if (stereo) {
                int ret = track1.setLoopPoints(0, samples.length / 2, loops);
//                Log.e("asdf","set loop points "+ret);
//                track1.setStereoVolume((float) vol, (float) vol);
//                track1.setStereoVolume((float) 0, (float) 1);
                track1.setVolume((float) vol);
            } else {
                int ret = track1.setLoopPoints(0,samples.length,loops);
//                Log.e("asdf","set loop points "+ret);
                ret = track1.setVolume((float) vol);
                Log.e("asdf","set vol "+ret);
            }
            track1.play();
        }catch(Exception e) {
            Log.e("err",e.getMessage());
        }
    }

    public void run() {
        track1.setLoopPoints(0,samples.length,-1);
        track1.play();
    }

    public void pause() {
        track1.pause();
    }

    public void stopit() {
        track1.stop();
    }
}
