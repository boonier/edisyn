package edisyn.test;

import edisyn.synth.rolandjv2080.RolandJV2080Rec;

public class RolandJV2080RecTest
    {
    static byte[] buildMessage(int tone)
        {
        byte[] data = new byte[11];
        data[0] = (byte)0xF0;
        data[1] = (byte)0x41;
        data[3] = (byte)0x6A;
        data[4] = (byte)0x12;

        if (tone == 0)
            {
            data[5] = (byte)0x01;
            data[6] = (byte)0x00;
            data[7] = (byte)0x00;
            }
        else if (tone == 1)
            {
            data[6] = (byte)0x00;
            data[7] = (byte)0x10;
            }
        else if (tone == 2)
            {
            data[6] = (byte)0x00;
            data[7] = (byte)0x12;
            }
        else if (tone == 3)
            {
            data[6] = (byte)0x00;
            data[7] = (byte)0x14;
            }
        else if (tone == 4)
            {
            data[6] = (byte)0x00;
            data[7] = (byte)0x16;
            }

        return data;
        }

    static void printRecognize(byte[] msg, int tone)
        {
        System.err.println("recognizeTone(tone=" + tone + ") = " + RolandJV2080Rec.recognizeTone(msg, tone));
        }

    public static void main(String[] args)
        {
        byte[] common = buildMessage(0);
        byte[] tone1 = buildMessage(1);
        byte[] tone2 = buildMessage(2);
        byte[] tone3 = buildMessage(3);
        byte[] tone4 = buildMessage(4);

        byte[][] group = new byte[][] { common, tone1, tone2, tone3, tone4 };

        System.err.println("--- Per-message recognition ---");
        printRecognize(common, 0);
        printRecognize(tone1, 1);
        printRecognize(tone2, 2);
        printRecognize(tone3, 3);
        printRecognize(tone4, 4);

        System.err.println();
        System.err.println("--- Group recognition (expected advance to 5) ---");
        int next = RolandJV2080Rec.getNextSysexPatchGroup(group, 0);
        System.err.println("getNextSysexPatchGroup(..., 0) = " + next);

        System.err.println();
        System.err.println("--- Group recognition failure demo (tone2 offset wrong, expected stay at 0) ---");
        byte[] badTone2 = buildMessage(2);
        badTone2[7] = (byte)0x10;
        byte[][] badGroup = new byte[][] { common, tone1, badTone2, tone3, tone4 };
        int badNext = RolandJV2080Rec.getNextSysexPatchGroup(badGroup, 0);
        System.err.println("getNextSysexPatchGroup(..., 0) = " + badNext);
        }
    }
