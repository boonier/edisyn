package edisyn.nn;

public class Sigmoid implements Layer
    {
    public Sigmoid()
        {
        }

    private static double sigmoid(double x)
        {
        if (x >= 0)
            {
            double z = Math.exp(-x);
            return 1.0 / (1.0 + z);
            }
        else
            {
            double z = Math.exp(x);
            return z / (1.0 + z);
            }
        }

    public double[] feed(double[] vec)
        {
        double[] out = new double[vec.length];
        for(int i = 0; i < vec.length; i++)
            {
            out[i] = sigmoid(vec[i]);
            }
        return out;
        }

    public static Layer readFromString(String str)
        {
        return new Sigmoid();
        }
    }
