package com.example.oae;

import android.util.Log;

public class Chirp {
    public static short[] generatetriangularChirpSpeaker(double startFreq, double endFreq, int N, double SamplingRate, double initialPhase)
    {
        short[] ans = new short[N];
        double f = startFreq;
        double time = (double)N/(2*SamplingRate);
        double k = (endFreq - startFreq) / (double)time;
        double phase=0;
        double t = 0;
        for (int i = 0; i < N/2; i++)
        {
            t = (double)i/SamplingRate;
            phase = initialPhase + 2*Math.PI*(startFreq*t + 0.5*k*t*t);
            phase = AngularMath.Normalize(phase);
            ans[i] = (short)(Math.sin(phase)*20000);
        }
        t = (double)N /(2* SamplingRate);
        //initialPhase = phase;
        //initialPhase = initialPhase + 2 * Math.PI * (startFreq * t + 0.5 * k * t * t);
        initialPhase = phase + 2 * Math.PI * (startFreq * t + 0.5 * k * t * t);
        for (int i = N/2; i < N ; i++)
        {
            t = (double)(t + 1 / SamplingRate);
            phase = initialPhase - 2 * Math.PI * (endFreq * t - 0.5 * k * t * t);
            phase = AngularMath.Normalize(phase);
            ans[i] = (short)(Math.sin(phase)*20000);
        }

        return ans;
    }


    public static short[] continuouspulse(double startFreq, double endFreq, int len, int gaplen, double SamplingRate, double initialPhase) {
        short[] signal = new short[(int) ((len + gaplen))];
        try {
            short[] signal1;
            int index = 0;

            signal1 = Chirp.generateChirpSpeaker(startFreq, endFreq, len, SamplingRate, 0);
            for (int j = 0; j < signal1.length; j++)
                signal[index++] = signal1[j];
            for (int j = 0; j < gaplen; j++)
                signal[index++] = 0;
        }
        catch (Exception e) {
            Log.e("asdf",e.getMessage());
        }

        return signal;
    }

    //static double t = 0;
    public static short[] generateChirpSpeaker(double startFreq, double endFreq, double time, double fs, double initialPhase) {

        int N = (int) (time * fs);
        short[] ans = new short[N];
        double f = startFreq;
        double k = (endFreq - startFreq) / time;
        for (int i = 0; i < N; i++) {
            double t = (double) i / fs;
            double phase = initialPhase + 2*Math.PI*(startFreq * t + 0.5 * k * t * t);
            phase = AngularMath.Normalize(phase);
            ans[i] = (short) (Math.sin(phase));
        }

        return ans;
    }
}
