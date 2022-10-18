package com.example.oae;

public class Filewriter extends Thread {

    CyclicBuffer buffer;
    double[] temp;
    int tempsize;
    int count;
    String filename;
    int namecount;

    public Filewriter(CyclicBuffer buffer, int filesize, String foldername)
    {

        this.buffer = buffer;
        tempsize = filesize;
        temp = new double[filesize];
        count = 1;
        namecount = 1;
        filename = foldername+"/samples";
    }
    public void run()
    {
        while(true)
        {
            buffer.ReadNextSamples(tempsize,temp);
            buffer.ConsumedNSamples(tempsize);
            FileOperations.writetofile(temp,filename+namecount+"-"+count);
            count = count+1;
            if(count > 60000)
            {
                count = 1;
                namecount++;
            }
        }
    }

}