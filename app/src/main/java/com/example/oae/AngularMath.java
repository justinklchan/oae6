package com.example.oae;

public class AngularMath {

    public static double Normalize(double ang)
    {
        double angle = ang;
        while (angle < 0)
        {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI)
        {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    //beaware averaging angles can be tricky than you thought
    public static double AverageAngle(double[] ang)
    {
        double initialAngle = ang[0];
        //Console.Error.WriteLine("initialangle = " + ang[0]);
        double avgAng = ang[0];
        for (int i = 1; i < ang.length; i++)
        {
            double angle = ang[i];
            //Console.Error.WriteLine("angle = " + ang[i]);
            if (angle - initialAngle > Math.PI)
            {
                angle = ang[i] - 2*Math.PI;
            }
            else if (angle - initialAngle <= -Math.PI)
            {
                angle = angle + 2*Math.PI;
            }
            //Console.Error.WriteLine("angle = " + angle);
            avgAng += angle;
        }
        avgAng = avgAng / (double)ang.length;
        //Console.Error.WriteLine("avgangle = " + avgAng);
        return avgAng;
    }

    public static double QuantizePhase(double Quantum, double phase)
    {
        double ansFloor = Math.floor(phase / Quantum) * Quantum;
        double ansCeil = Math.ceil(phase / Quantum) * Quantum;

        if(Math.abs(ansCeil - phase) < Math.abs(ansFloor - phase))
        {
            phase = ansCeil;
        }
        else
        {
            phase = ansFloor;
        }
        phase = Normalize(phase);
        return phase;
    }
}
