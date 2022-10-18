package com.example.oae;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class Recorder extends Thread {
    AudioRecord rec;
    int samplingfrequency = 48000;
    int minbuffersize;
    short[] temp;
    CyclicBuffer buffer;

    public Recorder(CyclicBuffer buffer, int samplingfreq) {
        samplingfrequency = samplingfreq;
        minbuffersize = AudioRecord.getMinBufferSize(samplingfrequency, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        rec = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER, samplingfrequency, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minbuffersize);
        temp = new short[minbuffersize];
        this.buffer = buffer;
        //filter

    }

    public void run() {
        //int count = 0;
        boolean recording = true;
        rec.startRecording();

        while (recording) {
            int shortsrec = rec.read(temp, 0, temp.length);
            double[] samples = new double[shortsrec];
            for (int i = 0; i < shortsrec; i++) {
                samples[i] = temp[i];
            }

            buffer.Add(samples);
        }

    }
}