package com.example.oae;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioSpeaker2 extends Thread {

    AudioTrack track1;
    AudioTrack track2;
    int SamplingFreq;
    Context mycontext;
//    short[] samples;
    short[] combined;
    int speakerType;
    AudioManager man;

    int[] streams = new int[]{AudioManager.STREAM_MUSIC,
            AudioManager.STREAM_ACCESSIBILITY, AudioManager.STREAM_ALARM,
            AudioManager.STREAM_DTMF, AudioManager.STREAM_NOTIFICATION,
            AudioManager.STREAM_RING, AudioManager.STREAM_SYSTEM,
            AudioManager.STREAM_VOICE_CALL};

    public AudioSpeaker2(Context mycontext,short[] samples,short[] samples2,int samplingFreq, int speakerType, boolean stereo) {
        this.mycontext = mycontext;
        man = (AudioManager)mycontext.getSystemService(Context.AUDIO_SERVICE);
        for (Integer i : streams) {
            man.setStreamMute(i,true);
        }
        man.setStreamMute(speakerType,false);
        man.setStreamVolume(speakerType,(int)(man.getStreamMaxVolume(speakerType)*.1),0);

        SamplingFreq = samplingFreq;
//        this.samples = samples;
        this.speakerType = speakerType;

        this.combined = interleave(samples,samples2);

//        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
//                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
//                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);

//        if (stereo) {
//            track1 = new AudioTrack(speakerType, SamplingFreq, AudioFormat.CHANNEL_OUT_STEREO,
//                    AudioFormat.ENCODING_PCM_16BIT, samples.length, AudioTrack.MODE_STATIC);
//        }
//        else {
//        track1 = new AudioTrack(speakerType, SamplingFreq, AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, samples.length * 2, AudioTrack.MODE_STATIC);
//        track2 = new AudioTrack(speakerType, SamplingFreq, AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, samples.length * 2, AudioTrack.MODE_STATIC);
//        }

        track1 = new AudioTrack(speakerType, SamplingFreq, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, combined.length, AudioTrack.MODE_STATIC);

        track1.write(combined,0,combined.length);
//        track2.write(samples2,0,samples.length);
    }

    public short[] interleave(short[] s1, short[] s2) {
        short[] sout=new short[s1.length+s2.length];
        int counter=0;
        for(int i = 0; i < s1.length; i++) {
            sout[counter++]=s1[i];
            sout[counter++]=s2[i];
        }
        return sout;
    }

//    for S6 volume is .01, 1 unit of volume
//    for S7 volume is .02, 1 unit of volume
//    for pixel use .01 of volume, 1 unit of volume
//    for s9 volume is .03, 1 unit of volume (ringtone)

    public void play(double vol, int loops) {
        try {
            track1.setLoopPoints(0,combined.length,loops);
//            track2.setLoopPoints(0,samples.length,loops);
//            setVolume(1);
            Log.e("earapp",man.getStreamMaxVolume(speakerType)+"");
            Log.e("earapp",loops+"");
//            track1.setVolume((float)vol);
//            track1.setStereoVolume((float) vol, (float) 0);
//            track2.setStereoVolume((float) 0, (float) vol);
            track1.setVolume((float)vol);
            track1.play();
//            track2.play();
        }catch(Exception e) {
        }
    }

    public void run() {
        track1.setLoopPoints(0,combined.length,-1);
        track1.play();
//        track2.setLoopPoints(0,samples.length,-1);
//        track2.play();
    }
}
