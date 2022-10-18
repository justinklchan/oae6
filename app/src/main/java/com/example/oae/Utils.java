package com.example.oae;

public class Utils {
    public static short[] concat(short[] d1, short[] d2) {
        short[] out = new short[d1.length+d2.length];
        int counter=0;
        for (Short s : d1) {
            out[counter++]=s;
        }
        for (Short s : d2) {
            out[counter++]=s;
        }
        return out;
    }
}
