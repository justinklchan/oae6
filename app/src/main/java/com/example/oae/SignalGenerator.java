package com.example.oae;

import android.app.Activity;
import android.util.Log;

public class SignalGenerator {

    public static short[] chirpwithfile(short[] song) {
        short[] chirp = Chirp.generateChirpSpeaker(5000,10000,32,44100,0);
        double[] newsong = new double[song.length];
        int rate = 20000;
        int index = 1;
        while(true) {
            for (int i = 0; i < chirp.length; i++) {
                newsong[index] = chirp[i]+song[index];
                index++;
            }
            //for(int i=0;i<100;i++){
            //	newsong[index] = 0;
            //	index++;
            //}
            for(int i=0;i<rate;i++) {
                newsong[index] = song[index];
                index++;
            }

            if(index+rate+chirp.length > song.length)
                break;
        }
        double max = newsong[0];
        for(int i=1;i<index;i++)
            if(newsong[i]> max)
                max = newsong[i];
        short[] vals = new short[index];
        for(int i=0;i<index;i++)
            vals[i] = (short)((newsong[i]/max)*32000);
        return vals;
    }

    public static short[] ofdmsymbolfromfile(int gap1,int gap2) {
        short[] ofdmsymbol = FileOperations.readfromfile("ofdmsymbol.txt");
        short[] newsymbol = new short[gap1+ofdmsymbol.length+gap2];
        int index=0;
        for(int i=0;i<gap1;i++) {
            newsymbol[index] = 0;
            index++;
        }

        for(int i=0;i<ofdmsymbol.length;i++) {
            newsymbol[index] = ofdmsymbol[i];
            index++;
        }

        for(int i=0;i<gap2;i++) {
            newsymbol[index] =0;
            index++;
        }
        return newsymbol;
    }


    public static short[] songtoofdmpulse(short[] song, short[] ofdmsymbol,int rate) {
        short[] newsong = new short[song.length];
        int index = 0;
        while(true) {
            for(int i=0;i<rate && index < newsong.length;i++) {
                newsong[index] = song[index];
                index++;
            }

            for(int i=0;i<ofdmsymbol.length && index<newsong.length;i++) {
                newsong[index] = ofdmsymbol[i];
                index++;
            }
            if(index == newsong.length)
                break;
        }
        return newsong;
    }

    public static short[] randpulse(short[] rand,int gap,int count) {
        short[] signal = new short[(rand.length+gap)*count];

        int index = 0;
        for(int i=0;i<count;i++) {
            for(int j=0;j<rand.length;j++)
                signal[index++] = rand[j];
            for(int j=0;j<gap;j++)
                signal[index++] = 0;
        }
        return signal;
    }

    public static short[] pulse(int length,double startfreq,double endfreq,int Samplingfreq) {
        short[] signal = Chirp.generateChirpSpeaker(startfreq, endfreq, length, Samplingfreq, 0);
        return signal;
    }

    public static short[] repeat(short[] data, int rep) {
        short[] newArray = new short[data.length*rep];
        int counter = 0;
        for(int i = 0; i < rep; i++) {
            for(int j = 0; j < data.length; j++) {
                newArray[counter++] = data[i];
            }
        }
        return newArray;
    }

    public static short[] trianglepulse(int length,double startfreq,double endfreq,int Samplingfreq) {
        short[] signal = Chirp.generatetriangularChirpSpeaker(startfreq, endfreq, length, Samplingfreq, 0);
        return signal;
    }

    public static short[] continuoustrianglepulse(int length,double startfreq,double endfreq,int Samplingfreq,int gap,int count) {
        short[] signal = new short[(length+gap)*count];
        short[] signal1;
        int index = 0;
        for(int i=0;i<count;i++) {
            signal1 = trianglepulse(length,startfreq,endfreq,Samplingfreq);
            for(int j=0;j<signal1.length;j++)
                signal[index++] = signal1[j];
            for(int j=0;j<gap;j++)
                signal[index++] = 0;
        }

        return signal;
    }


    public static double[] arange(double start, double stop, double step) {
        double[] out = new double[(int)((stop-start)/step)];
        for (int i = 0; i < out.length; i++) {
            out[i] = start+(i*step);
        }
        return out;
    }

    public static short[] gauspuls(double tt, double fc, double bw, double fs) {
        tt = tt / 2;
        double[] t = arange(-tt, tt, 1 / fs);
        double bwr = -6;
        double r = Math.pow(10, bwr / 20);
        double fv = -bw * bw * fc * fc / (8 * Math.log(r));
        double tv = 1 / (4 * Math.PI * Math.PI * fv);

        double[] ye = new double[t.length];
        for (int i = 0; i < t.length; i++) {
            ye[i] = Math.exp((-t[i]*t[i])/(2*tv));
        }

        short[] yc = new short[ye.length];
        for (int i = 0; i < ye.length; i++) {
            yc[i] = (short)((ye[i] * Math.cos(2*Math.PI*fc*t[i]))*24000);
        }

        return yc;
    }

    public static short[] continuouspulse(double length, double startfreq, double endfreq, int Sampling_Rate, double gap, int chirps_required) {
        Log.e("logme", length + "," + gap + "," + chirps_required + "," + Sampling_Rate);
        Log.e("logme", (int) ((length + gap) * chirps_required * Sampling_Rate) + "");

        if (length > 1) {
            length = length / 1000;
        }
        if (gap > 1) {
            gap = gap / 1000;
        }

        short[] signal = new short[(int) ((length + gap) * chirps_required * Sampling_Rate)];
        try {
            short[] signal1;
            if (length == 1)  {
                signal1 = new short[1];
                signal1[0] = 32000;
            }
            int index = 0;
            for (int i = 0; i < chirps_required; i++) {
                signal1 = Chirp.generateChirpSpeaker(startfreq, endfreq, length, Sampling_Rate, 0);
                for (int j = 0; j < signal1.length; j++)
                    signal[index++] = signal1[j];
                for (int j = 0; j < gap * Sampling_Rate; j++)
                    signal[index++] = 0;
            }
        }
        catch (Exception e) {
            Log.e("asdf",e.getMessage());
        }

        return signal;
    }

    public static short[] continuouspulse_old(int length,double startfreq,double endfreq,int Samplingfreq,int gap,int count) {
        short[] signal = new short[(length+gap)*count];
        try {
            short[] signal1;
            if (length == 1) {
                signal1 = new short[1];
                signal1[0] = 25000;
            }
            int index = 0;
            for (int i = 0; i < count; i++) {
//                signal1 = Chirp.generateChirpSpeaker(startfreq, endfreq, length, Samplingfreq, 0);
                signal1 = generatePulse(length);
                for (int j = 0; j < signal1.length; j++)
                    signal[index++] = signal1[j];
                for (int j = 0; j < gap; j++)
                    signal[index++] = 0;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return signal;
    }

    //square pulse
    public static short[] custompulse(int length,int gap) {
        short[] pulse = generatePulse(length);
        short[] signal = new short[(length+gap)*4];

        int counter = 0;

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < pulse.length; i++) {
                signal[counter++] = (short) (pulse[i] / 3);
            }
            counter += gap;
        }
        for (int i = 0; i < pulse.length; i++) {
            signal[counter++] = (short)-pulse[i];
        }
        return signal;
    }

    public static short[] custompulse2(Activity av) {
//        short[] pulse = new short[]{-3201,-2057,-681,194,0,-1388,-3549,-5650,-6791,-6404,-4563,-2029,0,370,-1499,-5258,-9603,-12647,-12558,-8276,0,10714,21341,29139,32000,29139,21341,10714,0,-8276,-12558,-12647,-9603,-5258,-1499,370,0,-2029,-4563,-6404,-6791,-5650,-3549,-1388,0,194,-681,-2057,};
        short[] pulse = FileOperations.readrawasset_short(av,R.raw.pulse_sequence);
//        short[] signal = new short[(pulse.length+gap)*4];
//
//        int counter = 0;
//
//        for (int j = 0; j < 3; j++) {
//            for (int i = 0; i < pulse.length; i++) {
//                signal[counter++] = (short) (pulse[i]/3);
//            }
//            counter += gap;
//        }
//        for (int i = 0; i < pulse.length; i++) {
//            signal[counter++] = (short)-pulse[i];
//        }
        return pulse;
    }

    public static short[] generatePulse(int length) {
        short[] out = new short[length];
        for(int i = 1; i < out.length-1; i++) {
            out[i] = 32767;
        }
        return out;
    }

    public static short[] continuousgpulse(double tt, double fc, double bw, int Samplingfreq, int gap, int count) {
        short[] gpulse = gauspuls(tt,fc,bw,Samplingfreq);

        short[] signal = new short[(gpulse.length+gap)*count];
        int index = 0;
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < gpulse.length; j++) {
                signal[index++] = gpulse[j];
            }
            for (int j = 0; j < gap; j++) {
                signal[index++] = 0;
            }
        }

        return signal;
    }

    public static short[] continuoussinepulse(int length,double freq,int Samplingfreq,int gap,int count) {
        short[] signal = new short[(length+gap)*count];
        short[] signal1;

        signal1 = SineWaveSpeaker(length,freq,0,Samplingfreq);

        int index = 0;
        for(int i=0;i<count;i++) {
            for(int j=0;j<signal1.length;j++)
                signal[index++] = signal1[j];
            for(int j=0;j<gap;j++)
                signal[index++] = 0;
        }

        return signal;
    }

    public static short[] combinedsine(double f1, double f2,int Samplingfreq) {
        short[] signal = new short[Samplingfreq];
        short[] signal1;
        short[] signal2;

        signal1 = SineWaveSpeaker(Samplingfreq,f1,0,Samplingfreq);
        signal2 = SineWaveSpeaker(Samplingfreq,f2,0,Samplingfreq);

        int index = 0;
        for(int j=0;j<signal1.length;j++) {
            signal[index++] = (short)(signal1[j]+signal2[j]);
        }

        return signal;
    }

    public static short[] sine2speaker(double f1, double f2,int Samplingfreq, double v1, double v2) {
        short[] signal = new short[Samplingfreq*2];
        short[] signal1;
        short[] signal2;

        signal1 = SineWaveSpeaker(Samplingfreq,f1,0,Samplingfreq,v1);
        signal2 = SineWaveSpeaker(Samplingfreq,f2,0,Samplingfreq,v2);

        int counter = 0;
        for (int i = 0; i < signal.length; i+=2) {
            signal[i] = signal1[counter];
            signal[i+1] = signal2[counter];
//            signal[i+1] = 0;
            counter += 1;
        }

        return signal;
    }

    public static double[] RealSineWave(int len, double freq, double initialPhase, double samplingFreq) {
        double[] sin = new double[len];
        initialPhase = AngularMath.Normalize(initialPhase);
        double dphase = 2 * Math.PI * (double)freq / samplingFreq;
        double phase = initialPhase;

        for (int i = 0; i < len; i++) {
            sin[i] = Math.sin(phase);
            phase += dphase;
            phase = AngularMath.Normalize(phase);
        }
        return sin;
    }

    public static short[] SineWaveSpeaker(int len, double freq, double initialPhase, double samplingFreq) {
        short[] sin = new short[len];
        initialPhase = AngularMath.Normalize(initialPhase);
        double dphase = 2 * Math.PI * (double)freq / samplingFreq;
        double phase = initialPhase;

        for (int i = 0; i < len; i++) {
            sin[i] = (short)(Math.sin(phase)*24000);
            phase += dphase;
            phase = AngularMath.Normalize(phase);
        }
        return sin;
    }

    public static short[] SineWaveSpeaker(int len, double freq, double initialPhase, double samplingFreq, double vol) {
        short[] sin = new short[len];
        initialPhase = AngularMath.Normalize(initialPhase);
        double dphase = 2 * Math.PI * (double)freq / samplingFreq;
        double phase = initialPhase;

        for (int i = 0; i < len; i++) {
            sin[i] = (short)(Math.sin(phase)*(32000*vol));
            phase += dphase;
            phase = AngularMath.Normalize(phase);
        }
        return sin;
    }
}
