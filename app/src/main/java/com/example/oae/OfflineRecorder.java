package com.example.oae;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Handler;


public class OfflineRecorder extends Thread {
    public boolean recording;
    int samplingfrequency;
    public short[] samples;
    public short[] temp;
    public int[] sumbuffer;
    int count;
    AudioRecord rec;
    int minbuffersize;
//    int duration;
    String filename;
    String dirname;
    Activity av;
    int avwindowsize;
    TextView tv;
    TextView tv2;
    int trackTone;
    int trackTone2;
    LinkedList<Integer>arts=new LinkedList<>();

    public OfflineRecorder(int trackTone, int trackTone2, TextView tv, TextView tv2, Activity av, int samplingfrequency, double duration, int microphone, String filename, String dirname, boolean stereo) {
        this.av = av;
        this.tv = tv;
        this.tv2 = tv2;
        this.trackTone = trackTone;
        this.trackTone2 = trackTone2;
        count = 0;
        this.samplingfrequency = samplingfrequency;
        this.avwindowsize = this.samplingfrequency;
//        this.duration = duration;
        this.filename = filename;

        int channels=AudioFormat.CHANNEL_IN_MONO;
        if (stereo) {
            channels=AudioFormat.CHANNEL_IN_STEREO;
        }

        minbuffersize = AudioRecord.getMinBufferSize(samplingfrequency,channels,AudioFormat.ENCODING_PCM_16BIT);

        Log.e("asdf","init start "+minbuffersize);
        rec = new AudioRecord(MediaRecorder.AudioSource.UNPROCESSED,samplingfrequency,channels,AudioFormat.ENCODING_PCM_16BIT,minbuffersize);
        Log.e("asdf","init end");
        boolean aa = AutomaticGainControl.isAvailable();
        Log.e("agc",AutomaticGainControl.isAvailable()+"");
        if (Constants.AGC) {
            if (AutomaticGainControl.isAvailable()) {
                AutomaticGainControl agc = AutomaticGainControl.create(
                        rec.getAudioSessionId()
                );
                agc.setEnabled(true);
            }
        }
        else {
            if (AutomaticGainControl.isAvailable()) {
                AutomaticGainControl agc = AutomaticGainControl.create(
                        rec.getAudioSessionId()
                );
                agc.setEnabled(false);
            }
        }

        temp = new short[minbuffersize];
        Log.e("hello4","duration "+duration);
        samples = new short[(int)(duration*samplingfrequency)];

        sumbuffer = new int[avwindowsize];
        this.dirname = dirname;
    }

    public static boolean works(int rate, int channelConfig, int audioFormat) {
        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
            AudioRecord recorder = new AudioRecord(AudioManager.STREAM_VOICE_CALL, rate, channelConfig, audioFormat, bufferSize);
            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                Log.e("hello",">>> works true");
                return true;
            }
        }
        Log.e("hello",">>> works false");
        return false;
    }

    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_FLOAT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        Log.d("hello", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
                        Log.e("hello",bufferSize+","+AudioRecord.ERROR_BAD_VALUE);
                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(AudioManager.STREAM_VOICE_CALL, rate, channelConfig, audioFormat, bufferSize);
                            Log.e("hello",recorder.getState()+","+AudioRecord.STATE_INITIALIZED);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                Log.e("hello", "worked");
                                return recorder;
                            }
                        }
                    } catch (Exception e) {
//                        Log.e(C.TAG, rate + "Exception, keep trying.",e);
                        Log.e("hello",e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    public void slog(String str,short[] buffer) {
        int len = 1000;
        int start = 0;
        int end = start+len;
        for(int i = 0; i < str.length()/len; i++) {
            Log.v("asdf",str.substring(start, end));
            start+=len;
            end+=len;
        }
        if (end>=str.length()) {
            end = str.length();
        }
        Log.v("asdf",str.substring(start, end));
//        Log.v("asdf",end+","+str.length()+","+buffer[buffer.length-1]);
    }

    int numavs=0;
    int pointer=48000;
//    public void sigproc() {
////        Log.e("asdf","buffer "+pointer+","+(pointer+avwindowsize)+","+avwindowsize);
//        short[] buffer = Arrays.copyOfRange(samples,pointer,pointer+avwindowsize);
//        pointer+=avwindowsize;
//        numavs++;
//
//        double[] dbuffer = new double[buffer.length];
//        for (int i = 0; i < buffer.length; i++) {
//            dbuffer[i] = buffer[i];
//        }
//
//        for (int i = 0; i < dbuffer.length; i++) {
//            sumbuffer[i] += dbuffer[i];
//        }
//
//        double[] avbuffer = new double[avwindowsize];
//        for (int i = 0; i < sumbuffer.length; i++) {
//            avbuffer[i] = sumbuffer[i]/(double)numavs;
//        }
//
////        final double[] spec = fftnative(avbuffer,avwindowsize);
//        final double[] spec = fftnative(dbuffer,avwindowsize);
//
//        final int bin = trackTone/(samplingfrequency/avwindowsize);
//        final int bin2 = trackTone2/(samplingfrequency/avwindowsize);
//
//        final String snr = calcSnr(spec,bin);
//        final String snr2 = calcSnr(spec,bin2);
//        final int art = (int)artifact(spec);
//        int ART_THRESH=5;
//        // TOO NOISY
//        if (arts.size() > 1 && art - arts.getLast() > ART_THRESH) {
//            // remove the current segment
//            for (int i = 0; i < dbuffer.length; i++) {
//                sumbuffer[i] -= dbuffer[i];
//            }
//        }
//        else {
//            // only add to list if it is NOT that noisy
//            arts.add(art);
//        }
//        av.runOnUiThread(new Runnable() {
//            public void run() {
//                tv.setText(snr + "," + art);
//                tv2.setText(snr2 + "," + art);
//            }
//        });
//    }

    public double artifact(double[] spec) {
        double val=0;
        for (int i = 0; i < 6000; i++) {
            val=val+10*Math.log10(spec[i]);
        }
        return val/6000;
    }

    public static double max(double[] arr) {
        double mmax=0;
        for (Double d : arr) {
            if (d > mmax) {
                mmax=d;
            }
        }
        return mmax;
    }

    public String calcSnr(double[] specout, int bin) {
        for (int i = 0; i < specout.length; i++) {
            specout[i] *= specout[i];
        }

        int tol = 2;
        int fwindow = (int)Math.ceil(20/(this.samplingfrequency/this.avwindowsize));

        double signal = specout[bin];

        double[] arr1=Arrays.copyOfRange(specout,bin-fwindow-tol,bin-tol);
        double[] arr2=Arrays.copyOfRange(specout,bin+tol,bin+tol+fwindow);

//        double noisesum = 0;
//        for(Double i : arr1) {
//            noisesum+=i;
//        }
//        for(Double i : arr2) {
//            noisesum+=i;
//        }
//        double noise = (noisesum/(arr1.length+arr2.length));

        double max1=max(arr1);
        double max2=max(arr2);
        double noise = max1>max2?max1:max2;

        signal = 10*Math.log10(signal);
        noise = 10*Math.log10(noise);

        int snr = (int)(signal-noise);

        return (int)signal+","+(int)noise+","+snr;
    }

    public void run() {
        int bytesread;
        try {
            rec.startRecording();
        }
        catch(Exception e) {
            Log.e("ERROR","ERROR "+filename);
            return;
        }
        recording = true;
        Log.e("REC","RECORDING");
        while(recording) {
            bytesread = rec.read(temp,0,minbuffersize);
            Log.e("REC",bytesread+"");
            for(int i=0;i<bytesread;i++) {
                if(count >= samples.length) {
                    rec.stop();
                    rec.release();
                    recording = false;
//                    String aa = Arrays.toString(samples);
//                    int[] narray=new int[samples.length/3];
//                    int counter=0;
//                    for (int j = 0; j < samples.length; j+=3) {
//                        byte[] arr = { samples[j],samples[j+1],samples[j+2] };
//                        ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
//                        try {
//                            narray[counter++] = wrapped.getInt();
//                        }
//                        catch(Exception e) {
//                            Log.e("err",counter+","+narray.length+","+arr[0]+","+arr[1]+","+arr[2]);
//                        }
//                    }
                    FileOperations.writetofile_short(av, samples, filename);
                    break;
                }
                else {
                    samples[count]=temp[i];
                    count++;
//                    Log.e("len","len "+count+","+samples.length);
//                    if (count % avwindowsize == 0 && count != 0 && count > avwindowsize) {
//                        Thread t = new Thread() {
//                            @Override
//                            public void run() {
//                                super.run();
////                                Log.e("len","len "+count+","+samples.length);
////                                sigproc();
//                            }
//                        };
//                        t.run();
//                    }
                }
            }
        }
    }

    public void stopit() {
        rec.stop();
        String aa = Arrays.toString(samples);
        FileOperations.writetofile_short(av, samples, filename);
    }

    public static native double[] fftnative(double[] data, int N);
}
