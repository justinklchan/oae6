package com.example.oae;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBuffer {
    private double[] buffer;
    private int StartPos;
    private int EndPos;
    private int NoValidSamples;

    public CyclicBuffer(int len)
    {
        buffer = new double[len];
        StartPos = 0;
        EndPos = 0;
        NoValidSamples = 0;
    }

    private void Add(double sample)
    {
        buffer[EndPos] = sample;
        EndPos = (EndPos + 1) % (buffer.length);
        NoValidSamples++;
        if (EndPos == StartPos)
        {

        }
    }

    public void AddOne(double sample)
    {
        Lock l = new ReentrantLock();
        l.lock();
        try
        {
            Add(sample);
        }
        finally
        {
            l.unlock();
        }
    }

    public void Add(double[] samples)
    {
        Lock l = new ReentrantLock();
        l.lock();
        try
        {
            for (int i = 0; i < samples.length; i++)
            {
                Add(samples[i]);
            }

        }
        finally
        {
            l.unlock();
        }
    }

    //done working with N samples
    //advance the start position
    public void ConsumedNSamples(int N)
    {
        Lock l = new ReentrantLock();
        l.lock();
        try
        {
            StartPos = (StartPos + N) % buffer.length;
            NoValidSamples -= N;

        }
        finally
        {
            l.unlock();
        }
    }

    //reads the next N Samples into provided buffer
    public void ReadNextSamples(int N, double[] buf)
    {
        Lock l = new ReentrantLock();
        l.lock();
        try
        {
            while (NoValidSamples < N)
            {
                //Monitor.Wait(this);
            }
            int index = StartPos;
            for (int i = 0; i < N; i++)
            {
                buf[i] = buffer[index];
                index = (index + 1) % buffer.length;
            }
        }
        finally
        {
            l.unlock();
        }
    }

    public String ToString()
    {
        Lock l = new ReentrantLock();
        l.lock();
        String s = new String();
        try
        {
            int index = StartPos;
            for (int i = 0; i < NoValidSamples; i++)
            {
                s = s+(buffer[index] + " ");
                index = (index + 1) % buffer.length;
            }

        }
        finally
        {
            l.unlock();
        }
        return s;
    }
}