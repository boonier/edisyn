package edisyn.synth.rolandjv2080;
import edisyn.*;

public class RolandJV2080Rec extends Recognize
    {
    public static int getNextSysexPatchGroup(byte[][] sysex, int start)
        {
        if (start + 5 > sysex.length)
            {
            System.err.println("RolandJV2080Rec.getNextSysexPatchGroup(): not enough sysex messages.");
            return start;
            }

        if (!recognizeCommon(sysex[start]))
            {
            System.err.println("RolandJV2080Rec.getNextSysexPatchGroup(): could not find common.");
            return start;
            }

        for(int i = 0; i < 4; i++)
            {
            if (!recognizeTone(sysex[start + i + 1], i + 1))
                {
                System.err.println("RolandJV2080Rec.getNextSysexPatchGroup(): could not find tone " + i + ".");
                return start;
                }
            }

        return start + 5;
        }

    public static boolean recognizeCommon(byte[] data)
        {
        return recognizeTone(data, 0);
        }

    static final int[] toneOffsets = { 0x00, 0x10, 0x12, 0x14, 0x16 };

    public static boolean recognizeTone(byte[] data, int tone)
        {
        if (data == null || data.length < 11) return false;

        int a3 = toneOffsets[tone];

        int b3 = (a3 + 1) & 0x7F;

        return
            (data[0] == (byte)0xF0) &&
            (data[1] == (byte)0x41) &&
            (data[3] == (byte)0x6A) &&
            (data[4] == (byte)0x12) &&
            (data[6] == (byte)0x00) &&
            (data[7] == (byte)a3 || (tone != 0 && data[7] == (byte)b3)) &&
            (tone == 0 ? (data[5] == (byte)0x01 || data[5] == (byte)0x03) : true);
        }

    public static boolean recognize(byte[] data)
        {
        if (data == null || data.length < 11) return false;

        // Broad recognition: accept any JV-2080 DT1 packet so we can log USER dumps, etc.
        // We still insist on the core Roland header + JV-2080 model ID.
        if ((data[0] == (byte)0xF0) &&
            (data[1] == (byte)0x41) &&
            (data[3] == (byte)0x6A) &&
            (data[4] == (byte)0x12))
            return true;

        return recognizeCommon(data) ||
            recognizeTone(data, 1) ||
            recognizeTone(data, 2) ||
            recognizeTone(data, 3) ||
            recognizeTone(data, 4);
        }
    }
