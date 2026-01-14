package edisyn.nn;

public class Normalization implements Layer
    {
    private final double[] mean;
    private final double[] std;

    public Normalization(double[] mean, double[] std)
        {
        this.mean = mean;
        this.std = std;
        }

    public double[] feed(double[] vec)
        {
        if (mean == null || std == null)
            {
            return vec;
            }
        if (vec.length != mean.length || vec.length != std.length)
            {
            System.err.println("Bad input to Normalization.feed: vec length " + vec.length + " does not match mean/std length " + mean.length);
            return null;
            }
        double[] out = new double[vec.length];
        for(int i = 0; i < vec.length; i++)
            {
            double s = std[i];
            if (s == 0.0)
                {
                out[i] = vec[i] - mean[i];
                }
            else
                {
                out[i] = (vec[i] - mean[i]) / s;
                }
            }
        return out;
        }

    public static Layer readFromString(String str)
        {
        String[] strs = str.trim().split("\\s+");
        if (strs.length <= 1)
            {
            return new Normalization(null, null);
            }

        int index = 1;
        int n = -1;

        // Try: Normalization {n} [means... n] [std... n]
        try
            {
            n = Integer.parseInt(strs[index]);
            index++;
            }
        catch (NumberFormatException e)
            {
            n = -1;
            }

        if (n < 0)
            {
            // Try: Normalization [means... n] [std... n]  (infer n)
            int remaining = strs.length - 1;
            if ((remaining % 2) != 0)
                {
                // Unrecognized format, default to identity
                return new Normalization(null, null);
                }
            n = remaining / 2;
            index = 1;
            }
        else
            {
            // Require enough tokens: n means + n stds
            if (strs.length < 1 + 1 + 2 * n)
                {
                return new Normalization(null, null);
                }
            }

        double[] mean = new double[n];
        double[] std = new double[n];
        for(int i = 0; i < n; i++)
            {
            mean[i] = Double.parseDouble(strs[index + i]);
            }
        index += n;
        for(int i = 0; i < n; i++)
            {
            std[i] = Double.parseDouble(strs[index + i]);
            }

        return new Normalization(mean, std);
        }
    }
