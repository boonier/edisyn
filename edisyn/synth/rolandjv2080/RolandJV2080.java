package edisyn.synth.rolandjv2080;

import edisyn.*;
import edisyn.gui.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class RolandJV2080 extends Synth
    {
    public static final int MAXIMUM_NAME_LENGTH = 12;

    static final boolean DEBUG = true;

    public static final String[] KEY_ASSIGNS = new String[] { "Poly", "Solo" };
    public static final String[] PORTAMENTO_MODES = new String[] { "Legato", "Normal" };
    public static final String[] PORTAMENTO_TYPES = new String[] { "Time", "Rate" };
    public static final String[] PORTAMENTO_STARTS = new String[] { "Pitch", "CC", "Note" };
    public static final String[] HOLD_PEAK = new String[] { "Hold", "Peak" };
    public static final String[] VOICE_PRIORITIES = new String[] { "Last", "Loudest" };
    public static final String[] CHORUS_OUTPUTS = new String[] { "Mix", "EFX", "Reverb", "Mix+Reverb" };
    public static final String[] ON_OFF = new String[] { "Off", "On" };
    public static final String[] CLOCK_SOURCES = new String[] { "Internal", "MIDI" };
    public static final String[] PATCH_CONTROL_SOURCES = new String[]
        {
        "OFF",
        "SYS-CTRL1",
        "SYS-CTRL2",
        "MODULATION",
        "BREATH",
        "FOOT",
        "VOLUME",
        "PAN",
        "EXPRESSION",
        "PITCH BEND",
        "AFTERTOUCH",
        "LFO1",
        "LFO2",
        "VELOCITY",
        "KEYFOLLOW",
        "PLAYMATE"
        };
    public static final String[] EFX_CONTROL_SOURCES = new String[]
        {
        "OFF",
        "SYS-CTRL1",
        "SYS-CTRL2",
        "MODULATION",
        "BREATH",
        "FOOT",
        "VOLUME",
        "PAN",
        "EXPRESSION",
        "PITCH BEND",
        "AFTERTOUCH"
        };
    public static final String[] EFX_TYPES = new String[]
        {
        "STEREO-EQ",
        "OVERDRIVE",
        "DISTORTION",
        "PHASER",
        "SPECTRUM",
        "ENHANCER",
        "AUTO-WAH",
        "ROTARY",
        "COMPRESSOR",
        "LIMITER",
        "HEXA-CHORUS",
        "TREMOLO-CHORUS",
        "SPACE-D",
        "STEREO-CHORUS",
        "STEREO-FLANGER",
        "STEP-FLANGER",
        "STEREO-DELAY",
        "MODULATION-DELAY",
        "TRIPLE-TAP-DELAY",
        "QUADRUPLE-TAP-DELAY",
        "TIME-CONTROL-DELAY",
        "2VOICE-PITCH-SHIFTER",
        "FBK-PITCH-SHIFTER",
        "REVERB",
        "GATE-REVERB",
        "OVERDRIVE->CHORUS",
        "OVERDRIVE->FLANGER",
        "OVERDRIVE->DELAY",
        "DISTORTION->CHORUS",
        "DISTORTION->FLANGER",
        "DISTORTION->DELAY",
        "ENHANCER->CHORUS",
        "ENHANCER->FLANGER",
        "ENHANCER->DELAY",
        "CHORUS->DELAY",
        "FLANGER->DELAY",
        "CHORUS->FLANGER",
        "CHORUS/DELAY",
        "FLANGER/DELAY",
        "CHORUS/FLANGER"
        };

    int parseStatus = 0;

    static final int MODEL_ID = 0x6A;
    static final int COMMAND_RQ1 = 0x11;
    static final int COMMAND_DT1 = 0x12;

    static final int[] TEMP_PATCH_ADDR_PREFIX = { 0x03, 0x00 };

    static final int COMMON_SIZE = 0x4A;
    static final int TONE_SIZE = 0x81;

    static final int COMMON_OFFSET_EFX_TYPE = 0x0C;
    static final int COMMON_OFFSET_EFX_PARAMETER_1 = 0x0D;
    static final int COMMON_OFFSET_EFX_PARAMETER_12 = 0x18;
    static final int COMMON_OFFSET_EFX_MIX_OUT_SEND_LEVEL = 0x19;
    static final int COMMON_OFFSET_EFX_CHORUS_SEND_LEVEL = 0x1A;
    static final int COMMON_OFFSET_EFX_REVERB_SEND_LEVEL = 0x1B;
    static final int COMMON_OFFSET_EFX_CONTROL_SOURCE_1 = 0x1C;
    static final int COMMON_OFFSET_EFX_CONTROL_SENS_1 = 0x1D;
    static final int COMMON_OFFSET_EFX_CONTROL_DEPTH_1 = 0x1E;
    static final int COMMON_OFFSET_EFX_CONTROL_SOURCE_2 = 0x1F;
    static final int COMMON_OFFSET_EFX_CONTROL_SENS_2 = 0x20;
    static final int COMMON_OFFSET_EFX_CONTROL_DEPTH_2 = 0x21;
    static final int COMMON_OFFSET_CHORUS_LEVEL = 0x22;
    static final int COMMON_OFFSET_CHORUS_RATE = 0x23;
    static final int COMMON_OFFSET_CHORUS_DEPTH = 0x24;
    static final int COMMON_OFFSET_CHORUS_PRE_DELAY = 0x25;
    static final int COMMON_OFFSET_CHORUS_FEEDBACK = 0x26;
    static final int COMMON_OFFSET_CHORUS_OUTPUT = 0x27;
    static final int COMMON_OFFSET_REVERB_LEVEL = 0x28;
    static final int COMMON_OFFSET_REVERB_TIME = 0x29;
    static final int COMMON_OFFSET_REVERB_HF_DAMP = 0x2A;
    static final int COMMON_OFFSET_DELAY_FEEDBACK = 0x2B;
    static final int COMMON_OFFSET_PATCH_TEMPO = 0x2C;
    static final int COMMON_OFFSET_PATCH_LEVEL = 0x2D;
    static final int COMMON_OFFSET_PATCH_PANNING = 0x2E;
    static final int COMMON_OFFSET_ANALOG_FEEL = 0x2F;
    static final int COMMON_OFFSET_VOICE_RESERVE = 0x30;
    static final int COMMON_OFFSET_BEND_RANGE_UP = 0x31;
    static final int COMMON_OFFSET_BEND_RANGE_DOWN = 0x32;
    static final int COMMON_OFFSET_KEY_ASSIGN = 0x33;
    static final int COMMON_OFFSET_SOLO_LEGATO_SWITCH = 0x34;
    static final int COMMON_OFFSET_PORTAMENTO_SWITCH = 0x35;
    static final int COMMON_OFFSET_PORTAMENTO_MODE = 0x36;
    static final int COMMON_OFFSET_PORTAMENTO_TYPE = 0x37;
    static final int COMMON_OFFSET_PORTAMENTO_START = 0x38;
    static final int COMMON_OFFSET_PORTAMENTO_TIME = 0x39;
    static final int COMMON_OFFSET_PATCH_CONTROL_SOURCE_2 = 0x3A;
    static final int COMMON_OFFSET_PATCH_CONTROL_SOURCE_3 = 0x3B;
    static final int COMMON_OFFSET_EFX_CONTROL_HOLD_PEAK = 0x3C;
    static final int COMMON_OFFSET_CONTROL_1_HOLD_PEAK = 0x3D;
    static final int COMMON_OFFSET_CONTROL_2_HOLD_PEAK = 0x3E;
    static final int COMMON_OFFSET_CONTROL_3_HOLD_PEAK = 0x3F;
    static final int COMMON_OFFSET_CONTROL_4_HOLD_PEAK = 0x40;
    static final int COMMON_OFFSET_VELOCITY_RANGE_SWITCH = 0x41;
    static final int COMMON_OFFSET_OCTAVE_SHIFT = 0x42;
    static final int COMMON_OFFSET_STEREO_TONE_DEPTH = 0x43;
    static final int COMMON_OFFSET_VOICE_PRIORITY = 0x44;
    static final int COMMON_OFFSET_STRUCTURE_TYPE_1_2 = 0x45;
    static final int COMMON_OFFSET_STRUCTURE_TYPE_3_4 = 0x46;
    static final int COMMON_OFFSET_BOOSTER_3_4 = 0x47;
    static final int COMMON_OFFSET_CLOCK_SOURCE = 0x48;
    static final int COMMON_OFFSET_CATEGORY = 0x49;

    static final int DT1_WRITE_BIAS_CHORUS_REVERB_DELAY_START = 0x22;
    static final int DT1_WRITE_BIAS_CHORUS_REVERB_DELAY_END = 0x2B;
    static final int DT1_WRITE_BIAS_PATCH_TEMPO_AND_LATER_START = 0x2C;
    static final int DT1_WRITE_BIAS_MINUS_ONE = -1;
    static final int DT1_WRITE_BIAS_PLUS_ONE = 1;

    static final HashMap<String, Integer> COMMON_KEY_TO_OFFSET = new HashMap<String, Integer>();
    static
        {
        COMMON_KEY_TO_OFFSET.put("efxtype", COMMON_OFFSET_EFX_TYPE);
        for(int i = 1; i <= 12; i++)
            {
            COMMON_KEY_TO_OFFSET.put("efxparameter" + i, COMMON_OFFSET_EFX_PARAMETER_1 + (i - 1));
            }
        COMMON_KEY_TO_OFFSET.put("efxmixoutsendlevel", COMMON_OFFSET_EFX_MIX_OUT_SEND_LEVEL);
        COMMON_KEY_TO_OFFSET.put("efxchorussendlevel", COMMON_OFFSET_EFX_CHORUS_SEND_LEVEL);
        COMMON_KEY_TO_OFFSET.put("efxreverbsendlevel", COMMON_OFFSET_EFX_REVERB_SEND_LEVEL);
        COMMON_KEY_TO_OFFSET.put("efxcontrolsource1", COMMON_OFFSET_EFX_CONTROL_SOURCE_1);
        COMMON_KEY_TO_OFFSET.put("efxcontrolsens1", COMMON_OFFSET_EFX_CONTROL_SENS_1);
        COMMON_KEY_TO_OFFSET.put("efxcontroldepth1", COMMON_OFFSET_EFX_CONTROL_DEPTH_1);
        COMMON_KEY_TO_OFFSET.put("efxcontrolsource2", COMMON_OFFSET_EFX_CONTROL_SOURCE_2);
        COMMON_KEY_TO_OFFSET.put("efxcontrolsens2", COMMON_OFFSET_EFX_CONTROL_SENS_2);
        COMMON_KEY_TO_OFFSET.put("efxcontroldepth2", COMMON_OFFSET_EFX_CONTROL_DEPTH_2);

        COMMON_KEY_TO_OFFSET.put("choruslevel", COMMON_OFFSET_CHORUS_LEVEL);
        COMMON_KEY_TO_OFFSET.put("chorusrate", COMMON_OFFSET_CHORUS_RATE);
        COMMON_KEY_TO_OFFSET.put("chorusdepth", COMMON_OFFSET_CHORUS_DEPTH);
        COMMON_KEY_TO_OFFSET.put("choruspredelay", COMMON_OFFSET_CHORUS_PRE_DELAY);
        COMMON_KEY_TO_OFFSET.put("chorusfeedback", COMMON_OFFSET_CHORUS_FEEDBACK);
        COMMON_KEY_TO_OFFSET.put("chorusoutput", COMMON_OFFSET_CHORUS_OUTPUT);

        COMMON_KEY_TO_OFFSET.put("reverblevel", COMMON_OFFSET_REVERB_LEVEL);
        COMMON_KEY_TO_OFFSET.put("reverbtime", COMMON_OFFSET_REVERB_TIME);
        COMMON_KEY_TO_OFFSET.put("reverbhfdamp", COMMON_OFFSET_REVERB_HF_DAMP);
        COMMON_KEY_TO_OFFSET.put("delayfeedback", COMMON_OFFSET_DELAY_FEEDBACK);

        COMMON_KEY_TO_OFFSET.put("patchtempo", COMMON_OFFSET_PATCH_TEMPO);
        COMMON_KEY_TO_OFFSET.put("patchlevel", COMMON_OFFSET_PATCH_LEVEL);
        COMMON_KEY_TO_OFFSET.put("patchpanning", COMMON_OFFSET_PATCH_PANNING);
        COMMON_KEY_TO_OFFSET.put("analogfeel", COMMON_OFFSET_ANALOG_FEEL);
        COMMON_KEY_TO_OFFSET.put("voicereserve", COMMON_OFFSET_VOICE_RESERVE);
        COMMON_KEY_TO_OFFSET.put("bendrangeup", COMMON_OFFSET_BEND_RANGE_UP);
        COMMON_KEY_TO_OFFSET.put("bendrangedown", COMMON_OFFSET_BEND_RANGE_DOWN);
        COMMON_KEY_TO_OFFSET.put("keyassign", COMMON_OFFSET_KEY_ASSIGN);
        COMMON_KEY_TO_OFFSET.put("sololegatoswitch", COMMON_OFFSET_SOLO_LEGATO_SWITCH);
        COMMON_KEY_TO_OFFSET.put("portamentoswitch", COMMON_OFFSET_PORTAMENTO_SWITCH);
        COMMON_KEY_TO_OFFSET.put("portamentomode", COMMON_OFFSET_PORTAMENTO_MODE);
        COMMON_KEY_TO_OFFSET.put("portamentotype", COMMON_OFFSET_PORTAMENTO_TYPE);
        COMMON_KEY_TO_OFFSET.put("portamentostart", COMMON_OFFSET_PORTAMENTO_START);
        COMMON_KEY_TO_OFFSET.put("portamentotime", COMMON_OFFSET_PORTAMENTO_TIME);

        COMMON_KEY_TO_OFFSET.put("patchcontrolsource2", COMMON_OFFSET_PATCH_CONTROL_SOURCE_2);
        COMMON_KEY_TO_OFFSET.put("patchcontrolsource3", COMMON_OFFSET_PATCH_CONTROL_SOURCE_3);

        COMMON_KEY_TO_OFFSET.put("efxcontrolholdpeak", COMMON_OFFSET_EFX_CONTROL_HOLD_PEAK);
        COMMON_KEY_TO_OFFSET.put("control1holdpeak", COMMON_OFFSET_CONTROL_1_HOLD_PEAK);
        COMMON_KEY_TO_OFFSET.put("control2holdpeak", COMMON_OFFSET_CONTROL_2_HOLD_PEAK);
        COMMON_KEY_TO_OFFSET.put("control3holdpeak", COMMON_OFFSET_CONTROL_3_HOLD_PEAK);
        COMMON_KEY_TO_OFFSET.put("control4holdpeak", COMMON_OFFSET_CONTROL_4_HOLD_PEAK);

        COMMON_KEY_TO_OFFSET.put("velocityrangeswitch", COMMON_OFFSET_VELOCITY_RANGE_SWITCH);
        COMMON_KEY_TO_OFFSET.put("octaveshift", COMMON_OFFSET_OCTAVE_SHIFT);
        COMMON_KEY_TO_OFFSET.put("stereotondepth", COMMON_OFFSET_STEREO_TONE_DEPTH);
        COMMON_KEY_TO_OFFSET.put("voicepriority", COMMON_OFFSET_VOICE_PRIORITY);
        COMMON_KEY_TO_OFFSET.put("structuretype12", COMMON_OFFSET_STRUCTURE_TYPE_1_2);
        COMMON_KEY_TO_OFFSET.put("structuretype34", COMMON_OFFSET_STRUCTURE_TYPE_3_4);
        COMMON_KEY_TO_OFFSET.put("booster34", COMMON_OFFSET_BOOSTER_3_4);
        COMMON_KEY_TO_OFFSET.put("clocksource", COMMON_OFFSET_CLOCK_SOURCE);
        COMMON_KEY_TO_OFFSET.put("category", COMMON_OFFSET_CATEGORY);
        }

    byte[] commonData = new byte[COMMON_SIZE];
    boolean[] commonReceived = new boolean[COMMON_SIZE];
    byte[][] toneData = new byte[][] { null, new byte[TONE_SIZE], new byte[TONE_SIZE], new byte[TONE_SIZE], new byte[TONE_SIZE] };
    boolean[][] toneReceived = new boolean[][] { null, new boolean[TONE_SIZE], new boolean[TONE_SIZE], new boolean[TONE_SIZE], new boolean[TONE_SIZE] };
    int receivedCountCommon = 0;
    int[] receivedCountTone = new int[] { 0, 0, 0, 0, 0 };

    static final int TONE_OFFSET_TONE_SWITCH = 0x00;
    static final int TONE_OFFSET_WAVE_GROUP_TYPE = 0x01;
    static final int TONE_OFFSET_WAVE_GROUP_ID = 0x02;
    static final int TONE_OFFSET_WAVE_NUMBER_MSB = 0x03;
    static final int TONE_OFFSET_WAVE_NUMBER_LSB = 0x04;
    static final int TONE_OFFSET_TONE_DELAY_MODE = 0x09;
    static final int TONE_OFFSET_TONE_DELAY_TIME = 0x0A;
    static final int TONE_OFFSET_VELOCITY_RANGE_LOWER = 0x0C;
    static final int TONE_OFFSET_VELOCITY_RANGE_UPPER = 0x0D;
    static final int TONE_OFFSET_COARSE_TUNE = 0x3D;
    static final int TONE_OFFSET_FINE_TUNE = 0x3E;
    static final int TONE_OFFSET_LFO1_RATE = 0x2F;
    static final int TONE_OFFSET_LFO1_DELAY_TIME = 0x31;
    static final int TONE_OFFSET_LFO2_RATE = 0x37;
    static final int TONE_OFFSET_LFO2_DELAY_TIME = 0x39;
    static final int TONE_OFFSET_PITCH_ENV_DEPTH = 0x41;
    static final int TONE_OFFSET_PITCH_ENV_TIME_1 = 0x46;
    static final int TONE_OFFSET_PITCH_ENV_TIME_2 = 0x47;
    static final int TONE_OFFSET_PITCH_ENV_TIME_3 = 0x48;
    static final int TONE_OFFSET_PITCH_ENV_TIME_4 = 0x49;
    static final int TONE_OFFSET_PITCH_ENV_LEVEL_1 = 0x4A;
    static final int TONE_OFFSET_PITCH_ENV_LEVEL_2 = 0x4B;
    static final int TONE_OFFSET_PITCH_ENV_LEVEL_3 = 0x4C;
    static final int TONE_OFFSET_PITCH_ENV_LEVEL_4 = 0x4D;
    static final int TONE_OFFSET_FILTER_TYPE = 0x50;
    static final int TONE_OFFSET_CUTOFF_FREQUENCY = 0x51;
    static final int TONE_OFFSET_RESONANCE = 0x53;
    static final int TONE_OFFSET_FILTER_ENV_DEPTH = 0x55;
    static final int TONE_OFFSET_FILTER_ENV_TIME_1 = 0x5B;
    static final int TONE_OFFSET_FILTER_ENV_TIME_2 = 0x5C;
    static final int TONE_OFFSET_FILTER_ENV_TIME_3 = 0x5D;
    static final int TONE_OFFSET_FILTER_ENV_TIME_4 = 0x5E;
    static final int TONE_OFFSET_FILTER_ENV_LEVEL_1 = 0x5F;
    static final int TONE_OFFSET_FILTER_ENV_LEVEL_2 = 0x60;
    static final int TONE_OFFSET_FILTER_ENV_LEVEL_3 = 0x61;
    static final int TONE_OFFSET_FILTER_ENV_LEVEL_4 = 0x62;
    static final int TONE_OFFSET_TONE_LEVEL = 0x65;
    static final int TONE_OFFSET_TONE_PAN = 0x77;
    static final int TONE_OFFSET_AMP_ENV_TIME_1 = 0x6E;
    static final int TONE_OFFSET_AMP_ENV_TIME_2 = 0x6F;
    static final int TONE_OFFSET_AMP_ENV_TIME_3 = 0x70;
    static final int TONE_OFFSET_AMP_ENV_TIME_4 = 0x71;
    static final int TONE_OFFSET_AMP_ENV_LEVEL_1 = 0x72;
    static final int TONE_OFFSET_AMP_ENV_LEVEL_2 = 0x73;
    static final int TONE_OFFSET_AMP_ENV_LEVEL_3 = 0x74;
    static final int TONE_OFFSET_OUTPUT_ASSIGN = 0x7D;
    static final int TONE_OFFSET_MIX_EFX_SEND_LEVEL = 0x7E;
    static final int TONE_OFFSET_CHORUS_SEND_LEVEL = 0x7F;
    static final int TONE_OFFSET_REVERB_SEND_LEVEL = 0x80;

    public RolandJV2080()
        {
        model.set("name", "Init Patch   ");

        JComponent soundPanel = new SynthPanel(this);
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        hbox.add(addNameGlobal(Style.COLOR_GLOBAL()));
        hbox.addLast(addCommon(Style.COLOR_A()));
        vbox.add(hbox);
        soundPanel.add(vbox, BorderLayout.CENTER);
        addTab("Common", soundPanel);

        for(int i = 1; i < 5; i++)
            {
            JComponent tonePanel = new SynthPanel(this);
            VBox tvbox = new VBox();
            tvbox.add(addToneBasic(i, (i % 2 == 0 ? Style.COLOR_B() : Style.COLOR_A())));
            tonePanel.add(tvbox, BorderLayout.CENTER);
            addTab("Tone " + i, tonePanel);
            }

        }

    void loadCommonFromBuffer(byte[] buf)
        {
        if (buf == null || buf.length < COMMON_SIZE) return;

        if (DEBUG)
            {
            System.err.println("RolandJV2080 Common Raw tempo hi/lo + level/pan/analog bytes=" +
                String.format("%02X %02X %02X %02X %02X",
                    buf[COMMON_OFFSET_PATCH_TEMPO] & 0x7F,
                    buf[COMMON_OFFSET_PATCH_TEMPO + 1] & 0x7F,
                    buf[0x2E] & 0x7F,
                    buf[0x2F] & 0x7F,
                    buf[0x30] & 0x7F));
            System.err.println("RolandJV2080 Common Raw chorus/reverb bytes=" +
                String.format("%02X %02X %02X %02X %02X %02X %02X %02X %02X %02X",
                    buf[COMMON_OFFSET_CHORUS_LEVEL] & 0x7F,
                    buf[COMMON_OFFSET_CHORUS_RATE] & 0x7F,
                    buf[COMMON_OFFSET_CHORUS_DEPTH] & 0x7F,
                    buf[COMMON_OFFSET_CHORUS_PRE_DELAY] & 0x7F,
                    buf[COMMON_OFFSET_CHORUS_FEEDBACK] & 0x7F,
                    buf[COMMON_OFFSET_CHORUS_OUTPUT] & 0x7F,
                    buf[COMMON_OFFSET_REVERB_LEVEL] & 0x7F,
                    buf[COMMON_OFFSET_REVERB_TIME] & 0x7F,
                    buf[COMMON_OFFSET_REVERB_HF_DAMP] & 0x7F,
                    buf[COMMON_OFFSET_DELAY_FEEDBACK] & 0x7F));

            StringBuilder dump = new StringBuilder();
            for(int i = 0; i < COMMON_SIZE; i++)
                {
                if ((i % 16) == 0)
                    {
                    if (i > 0) dump.append('\n');
                    dump.append(String.format("%02X: ", i));
                    }
                dump.append(String.format("%02X ", buf[i] & 0x7F));
                }
            System.err.println("RolandJV2080 Common Dump\n" + dump.toString());

            int[] targets = new int[] { 0x78, 0x7F, 0x40 };
            for(int t = 0; t < targets.length; t++)
                {
                int target = targets[t];
                int first = -1;
                int count = 0;
                for(int i = 0; i < COMMON_SIZE; i++)
                    {
                    int v = buf[i] & 0x7F;
                    if (v == target)
                        {
                        if (first == -1) first = i;
                        count++;
                        }
                    }
                System.err.println("RolandJV2080 Common Scan value=" + String.format("%02X", target) + " firstOffset=" + (first == -1 ? "--" : String.format("%02X", first)) + " count=" + count);
                }
            }

        model.set("efxtype", (buf[COMMON_OFFSET_EFX_TYPE] & 0x7F));
        for(int i = 0; i < 12; i++)
            {
            model.set("efxparameter" + (i + 1), (buf[COMMON_OFFSET_EFX_PARAMETER_1 + i] & 0x7F));
            }
        model.set("efxmixoutsendlevel", (buf[COMMON_OFFSET_EFX_MIX_OUT_SEND_LEVEL] & 0x7F));
        model.set("efxchorussendlevel", (buf[COMMON_OFFSET_EFX_CHORUS_SEND_LEVEL] & 0x7F));
        model.set("efxreverbsendlevel", (buf[COMMON_OFFSET_EFX_REVERB_SEND_LEVEL] & 0x7F));
        int efxSrc1 = (buf[COMMON_OFFSET_EFX_CONTROL_SOURCE_1] & 0x7F);
        if (efxSrc1 >= EFX_CONTROL_SOURCES.length) efxSrc1 = EFX_CONTROL_SOURCES.length - 1;
        model.set("efxcontrolsource1", efxSrc1);
        model.set("efxcontrolsens1", (buf[COMMON_OFFSET_EFX_CONTROL_SENS_1] & 0x7F));
        model.set("efxcontroldepth1", (buf[COMMON_OFFSET_EFX_CONTROL_DEPTH_1] & 0x7F));
        int efxSrc2 = (buf[COMMON_OFFSET_EFX_CONTROL_SOURCE_2] & 0x7F);
        if (efxSrc2 >= EFX_CONTROL_SOURCES.length) efxSrc2 = EFX_CONTROL_SOURCES.length - 1;
        model.set("efxcontrolsource2", efxSrc2);
        model.set("efxcontrolsens2", (buf[COMMON_OFFSET_EFX_CONTROL_SENS_2] & 0x7F));
        model.set("efxcontroldepth2", (buf[COMMON_OFFSET_EFX_CONTROL_DEPTH_2] & 0x7F));

        model.set("choruslevel", (buf[COMMON_OFFSET_CHORUS_LEVEL] & 0x7F));
        model.set("chorusrate", (buf[COMMON_OFFSET_CHORUS_RATE] & 0x7F));
        model.set("chorusdepth", (buf[COMMON_OFFSET_CHORUS_DEPTH] & 0x7F));
        model.set("choruspredelay", (buf[COMMON_OFFSET_CHORUS_PRE_DELAY] & 0x7F));
        model.set("chorusfeedback", (buf[COMMON_OFFSET_CHORUS_FEEDBACK] & 0x7F));

        int chOut = (buf[COMMON_OFFSET_CHORUS_OUTPUT] & 0x7F);
        if (chOut > 3) chOut = 3;
        model.set("chorusoutput", chOut);

        model.set("reverblevel", (buf[COMMON_OFFSET_REVERB_LEVEL] & 0x7F));
        model.set("reverbtime", (buf[COMMON_OFFSET_REVERB_TIME] & 0x7F));
        int hf = (buf[COMMON_OFFSET_REVERB_HF_DAMP] & 0x7F);
        if (hf > 17) hf = 17;
        model.set("reverbhfdamp", hf);
        model.set("delayfeedback", (buf[COMMON_OFFSET_DELAY_FEEDBACK] & 0x7F));

        // Empirically, the JV-2080 Common dump encodes Patch Tempo as two nibbles (hi/lo)
        // at 0x2C/0x2D: tempo = hi * 16 + lo.
        int tempo = ((buf[COMMON_OFFSET_PATCH_TEMPO] & 0x7F) * 16) + (buf[COMMON_OFFSET_PATCH_TEMPO + 1] & 0x7F);
        if (tempo < 20) tempo = 20;
        if (tempo > 250) tempo = 250;
        model.set("patchtempo", tempo);

        model.set("patchlevel", (buf[0x2E] & 0x7F));
        model.set("patchpanning", (buf[0x2F] & 0x7F));
        model.set("analogfeel", (buf[0x30] & 0x7F));

        int vr = (buf[0x31] & 0x7F);
        if (vr > 64) vr = 64;
        model.set("voicereserve", vr);

        int bendUp = (buf[COMMON_OFFSET_BEND_RANGE_UP] & 0x7F);
        if (bendUp > 12) bendUp = 12;
        model.set("bendrangeup", bendUp);

        int bendDown = (buf[COMMON_OFFSET_BEND_RANGE_DOWN] & 0x7F);
        if (bendDown > 48) bendDown = 48;
        model.set("bendrangedown", bendDown);

        model.set("keyassign", ((buf[COMMON_OFFSET_KEY_ASSIGN] & 0x7F) == 0 ? 0 : 1));
        model.set("sololegatoswitch", ((buf[COMMON_OFFSET_SOLO_LEGATO_SWITCH] & 0x7F) == 0 ? 0 : 1));
        model.set("portamentoswitch", ((buf[COMMON_OFFSET_PORTAMENTO_SWITCH] & 0x7F) == 0 ? 0 : 1));
        model.set("portamentomode", ((buf[COMMON_OFFSET_PORTAMENTO_MODE] & 0x7F) == 0 ? 0 : 1));
        model.set("portamentotype", ((buf[COMMON_OFFSET_PORTAMENTO_TYPE] & 0x7F) == 0 ? 0 : 1));
        int portaStart = (buf[COMMON_OFFSET_PORTAMENTO_START] & 0x7F);
        if (portaStart > 2) portaStart = 2;
        model.set("portamentostart", portaStart);
        model.set("portamentotime", (buf[COMMON_OFFSET_PORTAMENTO_TIME] & 0x7F));

        int pcs2 = (buf[COMMON_OFFSET_PATCH_CONTROL_SOURCE_2] & 0x7F);
        if (pcs2 >= PATCH_CONTROL_SOURCES.length) pcs2 = PATCH_CONTROL_SOURCES.length - 1;
        model.set("patchcontrolsource2", pcs2);
        int pcs3 = (buf[COMMON_OFFSET_PATCH_CONTROL_SOURCE_3] & 0x7F);
        if (pcs3 >= PATCH_CONTROL_SOURCES.length) pcs3 = PATCH_CONTROL_SOURCES.length - 1;
        model.set("patchcontrolsource3", pcs3);

        model.set("efxcontrolholdpeak", ((buf[COMMON_OFFSET_EFX_CONTROL_HOLD_PEAK] & 0x7F) == 0 ? 0 : 1));
        model.set("control1holdpeak", ((buf[COMMON_OFFSET_CONTROL_1_HOLD_PEAK] & 0x7F) == 0 ? 0 : 1));
        model.set("control2holdpeak", ((buf[COMMON_OFFSET_CONTROL_2_HOLD_PEAK] & 0x7F) == 0 ? 0 : 1));
        model.set("control3holdpeak", ((buf[COMMON_OFFSET_CONTROL_3_HOLD_PEAK] & 0x7F) == 0 ? 0 : 1));
        model.set("control4holdpeak", ((buf[COMMON_OFFSET_CONTROL_4_HOLD_PEAK] & 0x7F) == 0 ? 0 : 1));

        model.set("velocityrangeswitch", ((buf[COMMON_OFFSET_VELOCITY_RANGE_SWITCH] & 0x7F) == 0 ? 0 : 1));

        int os = (buf[COMMON_OFFSET_OCTAVE_SHIFT] & 0x7F);
        if (os > 6) os = 6;
        model.set("octaveshift", os);

        int std = (buf[COMMON_OFFSET_STEREO_TONE_DEPTH] & 0x7F);
        if (std > 6) std = 6;
        model.set("stereotondepth", std);

        model.set("voicepriority", ((buf[COMMON_OFFSET_VOICE_PRIORITY] & 0x7F) == 0 ? 0 : 1));
        model.set("structuretype12", (buf[COMMON_OFFSET_STRUCTURE_TYPE_1_2] & 0x7F));
        model.set("structuretype34", (buf[COMMON_OFFSET_STRUCTURE_TYPE_3_4] & 0x7F));

        int booster = (buf[COMMON_OFFSET_BOOSTER_3_4] & 0x7F);
        if (booster > 1) booster = 1;
        model.set("booster34", booster);

        int clock = (buf[COMMON_OFFSET_CLOCK_SOURCE] & 0x7F);
        if (clock > 1) clock = 1;
        model.set("clocksource", clock);

        model.set("category", (buf[COMMON_OFFSET_CATEGORY] & 0x7F));
        }


    public byte getID()
        {
        try
            {
            byte b = (byte)(Byte.parseByte(tuple.id));
            if (b >= 17) return (byte)(b - 1);
            }
        catch (NullPointerException e) { }
        catch (NumberFormatException e) { Synth.handleException(e); }
        return (byte)16;
        }

    public String reviseID(String id)
        {
        try
            {
            int val = Integer.parseInt(id);
            if (val < 17) val = 17;
            if (val > 32) val = 32;
            return "" + val;
            }
        catch (Exception e)
            {
            return "" + (getID() + 1);
            }
        }

    byte produceChecksum(byte[] data, int start, int end)
        {
        int check = 0;
        for(int i = start; i < end; i++)
            {
            check += data[i];
            }
        check = check & 0x7F;
        check = 0x80 - check;
        if (check == 0x80) check = 0;
        return (byte) check;
        }

    int linearAddress(int a1, int a2, int a3, int a4)
        {
        return ((a1 & 0x7F) << 21) | ((a2 & 0x7F) << 14) | ((a3 & 0x7F) << 7) | (a4 & 0x7F);
        }

    int[] buildAddress(int cc, int dd)
        {
        return new int[] { TEMP_PATCH_ADDR_PREFIX[0], TEMP_PATCH_ADDR_PREFIX[1], cc & 0x7F, dd & 0x7F };
        }

    byte[] buildRQ1(int[] address, int[] size)
        {
        byte[] msg = new byte[1 + 1 + 1 + 1 + 1 + 4 + 4 + 1 + 1];
        int pos = 0;
        msg[pos++] = (byte)0xF0;
        msg[pos++] = (byte)0x41;
        msg[pos++] = (byte)getID();
        msg[pos++] = (byte)MODEL_ID;
        msg[pos++] = (byte)COMMAND_RQ1;

        msg[pos++] = (byte)address[0];
        msg[pos++] = (byte)address[1];
        msg[pos++] = (byte)address[2];
        msg[pos++] = (byte)address[3];

        msg[pos++] = (byte)size[0];
        msg[pos++] = (byte)size[1];
        msg[pos++] = (byte)size[2];
        msg[pos++] = (byte)size[3];

        msg[pos++] = produceChecksum(msg, 5, 5 + 8);
        msg[pos++] = (byte)0xF7;
        return msg;
        }

    byte[] buildDT1(int[] address, byte[] data)
        {
        if (data == null) data = new byte[0];
        byte[] msg = new byte[1 + 1 + 1 + 1 + 1 + 4 + data.length + 1 + 1];
        int pos = 0;
        msg[pos++] = (byte)0xF0;
        msg[pos++] = (byte)0x41;
        msg[pos++] = (byte)getID();
        msg[pos++] = (byte)MODEL_ID;
        msg[pos++] = (byte)COMMAND_DT1;

        msg[pos++] = (byte)address[0];
        msg[pos++] = (byte)address[1];
        msg[pos++] = (byte)address[2];
        msg[pos++] = (byte)address[3];

        System.arraycopy(data, 0, msg, pos, data.length);
        pos += data.length;

        msg[pos++] = produceChecksum(msg, 5, 5 + 4 + data.length);
        msg[pos++] = (byte)0xF7;
        return msg;
        }

    void logRQ1(byte[] rq1)
        {
        if (!DEBUG) return;
        if (rq1 == null || rq1.length < 15) return;
        System.err.println("RolandJV2080 RQ1 " +
            String.format("%02X %02X %02X %02X", rq1[5] & 0x7F, rq1[6] & 0x7F, rq1[7] & 0x7F, rq1[8] & 0x7F) +
            " size=" +
            String.format("%02X %02X %02X %02X", rq1[9] & 0x7F, rq1[10] & 0x7F, rq1[11] & 0x7F, rq1[12] & 0x7F) +
            " chk=" + String.format("%02X", rq1[13] & 0x7F));
        }

    void logDT1(byte[] dt1)
        {
        if (!DEBUG) return;
        if (dt1 == null || dt1.length < 11) return;
        int payloadLen = dt1.length - 11;
        System.err.println("RolandJV2080 DT1 " +
            String.format("%02X %02X %02X %02X", dt1[5] & 0x7F, dt1[6] & 0x7F, dt1[7] & 0x7F, dt1[8] & 0x7F) +
            " len=" + payloadLen +
            " data=" + (payloadLen > 0 ? String.format("%02X", dt1[9] & 0x7F) : "--") +
            " chk=" + String.format("%02X", dt1[dt1.length - 2] & 0x7F));
        }

    void resetReceived()
        {
        for(int i = 0; i < commonReceived.length; i++) commonReceived[i] = false;
        receivedCountCommon = 0;
        for(int t = 1; t <= 4; t++)
            {
            for(int i = 0; i < toneReceived[t].length; i++) toneReceived[t][i] = false;
            receivedCountTone[t] = 0;
            }
        }

    void loadCommonFromPayload(byte[] payload)
        {
        int len = Math.min(COMMON_SIZE, payload.length);
        System.arraycopy(payload, 0, commonData, 0, len);
        for(int i = 0; i < len; i++)
            {
            if (!commonReceived[i]) { commonReceived[i] = true; receivedCountCommon++; }
            }

        if (receivedCountCommon == COMMON_SIZE)
            {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < MAXIMUM_NAME_LENGTH && i < COMMON_SIZE; i++)
                {
                sb.append((char)(commonData[i] & 0x7F));
                }
            model.set("name", revisePatchName(sb.toString()));
            }
        }

    void loadToneFromPayload(int tone, int offset, byte[] payload)
        {
        if (tone < 1 || tone > 4) return;
        if (offset < 0) return;

        int max = Math.min(payload.length, TONE_SIZE - offset);
        if (max <= 0) return;

        System.arraycopy(payload, 0, toneData[tone], offset, max);
        for(int i = 0; i < max; i++)
            {
            int idx = offset + i;
            if (!toneReceived[tone][idx]) { toneReceived[tone][idx] = true; receivedCountTone[tone]++; }
            }

        if (receivedCountTone[tone] == TONE_SIZE)
            {
            loadToneFromBuffer(tone, toneData[tone]);
            }
        }

    void loadToneFromBuffer(int tone, byte[] buf)
        {
        model.set("tone" + tone + "toneswitch", (buf[TONE_OFFSET_TONE_SWITCH] & 0x01));
        model.set("tone" + tone + "wavegrouptype", (buf[TONE_OFFSET_WAVE_GROUP_TYPE] & 0x03));
        model.set("tone" + tone + "wavegroupid", (buf[TONE_OFFSET_WAVE_GROUP_ID] & 0x7F));

        int waveMSB = (buf[TONE_OFFSET_WAVE_NUMBER_MSB] & 0x7F);
        int waveLSB = (buf[TONE_OFFSET_WAVE_NUMBER_LSB] & 0x7F);
        int waveNumber = ((waveMSB & 0x0F) << 4) | (waveLSB & 0x0F);
        model.set("tone" + tone + "wavenumber", waveNumber + 1);

        model.set("tone" + tone + "tonedelaymode", (buf[TONE_OFFSET_TONE_DELAY_MODE] & 0x7F));
        model.set("tone" + tone + "tonedelaytime", (buf[TONE_OFFSET_TONE_DELAY_TIME] & 0x7F));
        model.set("tone" + tone + "velocityrangelower", (buf[TONE_OFFSET_VELOCITY_RANGE_LOWER] & 0x7F));
        model.set("tone" + tone + "velocityrangeupper", (buf[TONE_OFFSET_VELOCITY_RANGE_UPPER] & 0x7F));
        model.set("tone" + tone + "pitchcoarse", (buf[TONE_OFFSET_COARSE_TUNE] & 0x7F));
        model.set("tone" + tone + "pitchfine", (buf[TONE_OFFSET_FINE_TUNE] & 0x7F));

        model.set("tone" + tone + "lfo1rate", (buf[TONE_OFFSET_LFO1_RATE] & 0x7F));
        model.set("tone" + tone + "lfo1delay", (buf[TONE_OFFSET_LFO1_DELAY_TIME] & 0x7F));
        model.set("tone" + tone + "lfo2rate", (buf[TONE_OFFSET_LFO2_RATE] & 0x7F));
        model.set("tone" + tone + "lfo2delay", (buf[TONE_OFFSET_LFO2_DELAY_TIME] & 0x7F));

        model.set("tone" + tone + "penvdepth", (buf[TONE_OFFSET_PITCH_ENV_DEPTH] & 0x7F));
        model.set("tone" + tone + "penvtime1", (buf[TONE_OFFSET_PITCH_ENV_TIME_1] & 0x7F));
        model.set("tone" + tone + "penvtime2", (buf[TONE_OFFSET_PITCH_ENV_TIME_2] & 0x7F));
        model.set("tone" + tone + "penvtime3", (buf[TONE_OFFSET_PITCH_ENV_TIME_3] & 0x7F));
        model.set("tone" + tone + "penvtime4", (buf[TONE_OFFSET_PITCH_ENV_TIME_4] & 0x7F));
        model.set("tone" + tone + "penvlevel1", (buf[TONE_OFFSET_PITCH_ENV_LEVEL_1] & 0x7F));
        model.set("tone" + tone + "penvlevel2", (buf[TONE_OFFSET_PITCH_ENV_LEVEL_2] & 0x7F));
        model.set("tone" + tone + "penvlevel3", (buf[TONE_OFFSET_PITCH_ENV_LEVEL_3] & 0x7F));
        model.set("tone" + tone + "penvlevel4", (buf[TONE_OFFSET_PITCH_ENV_LEVEL_4] & 0x7F));

        model.set("tone" + tone + "filtertype", (buf[TONE_OFFSET_FILTER_TYPE] & 0x7F));
        model.set("tone" + tone + "cutofffrequency", (buf[TONE_OFFSET_CUTOFF_FREQUENCY] & 0x7F));
        model.set("tone" + tone + "resonance", (buf[TONE_OFFSET_RESONANCE] & 0x7F));

        model.set("tone" + tone + "tvfenvdepth", (buf[TONE_OFFSET_FILTER_ENV_DEPTH] & 0x7F));
        model.set("tone" + tone + "tvfenvtime1", (buf[TONE_OFFSET_FILTER_ENV_TIME_1] & 0x7F));
        model.set("tone" + tone + "tvfenvtime2", (buf[TONE_OFFSET_FILTER_ENV_TIME_2] & 0x7F));
        model.set("tone" + tone + "tvfenvtime3", (buf[TONE_OFFSET_FILTER_ENV_TIME_3] & 0x7F));
        model.set("tone" + tone + "tvfenvtime4", (buf[TONE_OFFSET_FILTER_ENV_TIME_4] & 0x7F));
        model.set("tone" + tone + "tvfenvlevel1", (buf[TONE_OFFSET_FILTER_ENV_LEVEL_1] & 0x7F));
        model.set("tone" + tone + "tvfenvlevel2", (buf[TONE_OFFSET_FILTER_ENV_LEVEL_2] & 0x7F));
        model.set("tone" + tone + "tvfenvlevel3", (buf[TONE_OFFSET_FILTER_ENV_LEVEL_3] & 0x7F));
        model.set("tone" + tone + "tvfenvlevel4", (buf[TONE_OFFSET_FILTER_ENV_LEVEL_4] & 0x7F));

        model.set("tone" + tone + "tonelevel", (buf[TONE_OFFSET_TONE_LEVEL] & 0x7F));
        model.set("tone" + tone + "tonepan", (buf[TONE_OFFSET_TONE_PAN] & 0x7F));

        model.set("tone" + tone + "tvaenvtime1", (buf[TONE_OFFSET_AMP_ENV_TIME_1] & 0x7F));
        model.set("tone" + tone + "tvaenvtime2", (buf[TONE_OFFSET_AMP_ENV_TIME_2] & 0x7F));
        model.set("tone" + tone + "tvaenvtime3", (buf[TONE_OFFSET_AMP_ENV_TIME_3] & 0x7F));
        model.set("tone" + tone + "tvaenvtime4", (buf[TONE_OFFSET_AMP_ENV_TIME_4] & 0x7F));
        model.set("tone" + tone + "tvaenvlevel1", (buf[TONE_OFFSET_AMP_ENV_LEVEL_1] & 0x7F));
        model.set("tone" + tone + "tvaenvlevel2", (buf[TONE_OFFSET_AMP_ENV_LEVEL_2] & 0x7F));
        model.set("tone" + tone + "tvaenvlevel3", (buf[TONE_OFFSET_AMP_ENV_LEVEL_3] & 0x7F));

        model.set("tone" + tone + "outputassign", (buf[TONE_OFFSET_OUTPUT_ASSIGN] & 0x7F));
        model.set("tone" + tone + "mixefxsendlevel", (buf[TONE_OFFSET_MIX_EFX_SEND_LEVEL] & 0x7F));
        model.set("tone" + tone + "chorussendlevel", (buf[TONE_OFFSET_CHORUS_SEND_LEVEL] & 0x7F));
        model.set("tone" + tone + "reverbsendlevel", (buf[TONE_OFFSET_REVERB_SEND_LEVEL] & 0x7F));
        }

    public JFrame sprout()
        {
        JFrame frame = super.sprout();
        return frame;
        }

    public JComponent addNameGlobal(Color color)
        {
        Category globalCategory = new Category(this, "Patch", color);

        VBox vbox = new VBox();
        JComponent comp = new PatchDisplay(this, 3, false);
        vbox.add(comp);

        comp = new StringComponent("Patch Name", this, "name", MAXIMUM_NAME_LENGTH, "Name must be up to 12 ASCII characters.")
            {
            public String replace(String val)
                {
                return revisePatchName(val);
                }

            public void update(String key, Model model)
                {
                super.update(key, model);
                updateTitle();
                }
            };
        vbox.add(comp);

        globalCategory.add(vbox, BorderLayout.WEST);
        return globalCategory;
        }

    public JComponent addCommon(Color color)
        {
        Category category = new Category(this, "Common", color);
        category.makePasteable("name");
        HBox hbox = new HBox();

        JComponent comp;

        comp = new LabelledDial("Tempo", this, "patchtempo", color, 20, 250);
        hbox.add(comp);
        comp = new LabelledDial("Level", this, "patchlevel", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("Pan", this, "patchpanning", color, 0, 127)
            {
            public boolean isSymmetric() { return true; }
            public String map(int value)
                {
                if (value < 64)
                    {
                    return "L" + (64 - value);
                    }
                else if (value == 64) return "--";
                else
                    {
                    return "R" + (value - 64);
                    }
                }
            };
        hbox.add(comp);
        comp = new LabelledDial("Analog", this, "analogfeel", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Feel");
        hbox.add(comp);

        comp = new LabelledDial("Reserve", this, "voicereserve", color, 0, 64);
        ((LabelledDial)comp).addAdditionalLabel("Voice");
        hbox.add(comp);

        comp = new LabelledDial("Octave", this, "octaveshift", color, 0, 6)
            {
            public String map(int val)
                {
                return "" + (val - 3);
                }
            };
        ((LabelledDial)comp).addAdditionalLabel("Shift");
        hbox.add(comp);

        comp = new LabelledDial("Stereo", this, "stereotondepth", color, 0, 6)
            {
            public String map(int val)
                {
                return "" + (val - 3);
                }
            };
        ((LabelledDial)comp).addAdditionalLabel("Depth");
        hbox.add(comp);

        VBox vbox2 = new VBox();
        comp = new Chooser("Key Assign", this, "keyassign", KEY_ASSIGNS);
        vbox2.add(comp);
        comp = new CheckBox("Solo Legato", this, "sololegatoswitch");
        vbox2.add(comp);
        comp = new CheckBox("Portamento", this, "portamentoswitch");
        vbox2.add(comp);
        comp = new Chooser("Porta Mode", this, "portamentomode", PORTAMENTO_MODES);
        vbox2.add(comp);
        comp = new Chooser("Porta Type", this, "portamentotype", PORTAMENTO_TYPES);
        vbox2.add(comp);
        comp = new Chooser("Porta Start", this, "portamentostart", PORTAMENTO_STARTS);
        vbox2.add(comp);
        comp = new CheckBox("Vel Range", this, "velocityrangeswitch");
        vbox2.add(comp);
        comp = new Chooser("Voice Pri", this, "voicepriority", VOICE_PRIORITIES);
        vbox2.add(comp);
        comp = new Chooser("Ctrl Src 2", this, "patchcontrolsource2", PATCH_CONTROL_SOURCES);
        vbox2.add(comp);
        comp = new Chooser("Ctrl Src 3", this, "patchcontrolsource3", PATCH_CONTROL_SOURCES);
        vbox2.add(comp);
        hbox.add(vbox2);

        comp = new LabelledDial("Porta", this, "portamentotime", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Time");
        hbox.add(comp);

        comp = new LabelledDial("Bend", this, "bendrangedown", color, 0, 48);
        ((LabelledDial)comp).addAdditionalLabel("Down");
        hbox.add(comp);
        comp = new LabelledDial("Bend", this, "bendrangeup", color, 0, 12);
        ((LabelledDial)comp).addAdditionalLabel("Up");
        hbox.add(comp);

        comp = new Chooser("EFX Type", this, "efxtype", EFX_TYPES);
        hbox.add(comp);

        VBox vboxEfx = new VBox();
        comp = new Chooser("EFX Ctrl Src 1", this, "efxcontrolsource1", EFX_CONTROL_SOURCES);
        vboxEfx.add(comp);
        comp = new Chooser("EFX Ctrl Src 2", this, "efxcontrolsource2", EFX_CONTROL_SOURCES);
        vboxEfx.add(comp);
        hbox.add(vboxEfx);
        comp = new LabelledDial("EFX", this, "efxmixoutsendlevel", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Mix Out");
        hbox.add(comp);
        comp = new LabelledDial("EFX", this, "efxchorussendlevel", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Ch Send");
        hbox.add(comp);
        comp = new LabelledDial("EFX", this, "efxreverbsendlevel", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Rv Send");
        hbox.add(comp);

        comp = new LabelledDial("Chorus", this, "choruslevel", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Level");
        hbox.add(comp);
        comp = new LabelledDial("Chorus", this, "chorusrate", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Rate");
        hbox.add(comp);
        comp = new LabelledDial("Chorus", this, "chorusdepth", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Depth");
        hbox.add(comp);
        comp = new LabelledDial("Chorus", this, "choruspredelay", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("PreDelay");
        hbox.add(comp);
        comp = new LabelledDial("Chorus", this, "chorusfeedback", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Feedback");
        hbox.add(comp);
        comp = new Chooser("Ch Output", this, "chorusoutput", CHORUS_OUTPUTS);
        hbox.add(comp);

        comp = new LabelledDial("Reverb", this, "reverblevel", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Level");
        hbox.add(comp);
        comp = new LabelledDial("Reverb", this, "reverbtime", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Time");
        hbox.add(comp);
        comp = new LabelledDial("Reverb", this, "reverbhfdamp", color, 0, 17);
        ((LabelledDial)comp).addAdditionalLabel("HF Damp");
        hbox.add(comp);

        comp = new LabelledDial("Delay", this, "delayfeedback", color, 0, 127);
        ((LabelledDial)comp).addAdditionalLabel("Feedback");
        hbox.add(comp);

        VBox vbox3 = new VBox();
        comp = new LabelledDial("Struct", this, "structuretype12", color, 0, 9);
        ((LabelledDial)comp).addAdditionalLabel("1/2");
        vbox3.add(comp);
        comp = new LabelledDial("Struct", this, "structuretype34", color, 0, 9);
        ((LabelledDial)comp).addAdditionalLabel("3/4");
        vbox3.add(comp);
        comp = new Chooser("Booster 3/4", this, "booster34", ON_OFF);
        vbox3.add(comp);
        comp = new Chooser("Clock", this, "clocksource", CLOCK_SOURCES);
        vbox3.add(comp);
        hbox.add(vbox3);

        category.add(hbox, BorderLayout.CENTER);
        return category;
        }

    public JComponent addToneBasic(final int tone, Color color)
        {
        Category category = new Category(this, "Tone", color);

        VBox vbox = new VBox();
        HBox hbox = new HBox();

        JComponent comp = new CheckBox("Tone Switch", this, "tone" + tone + "toneswitch");
        hbox.add(comp);

        comp = new LabelledDial("Wave Group", this, "tone" + tone + "wavegrouptype", color, 0, 2);
        hbox.add(comp);

        comp = new LabelledDial("Wave Group ID", this, "tone" + tone + "wavegroupid", color, 0, 127);
        hbox.add(comp);

        comp = new LabelledDial("Wave Number", this, "tone" + tone + "wavenumber", color, 1, 255);
        hbox.add(comp);

        comp = new LabelledDial("Delay Mode", this, "tone" + tone + "tonedelaymode", color, 0, 7);
        hbox.add(comp);

        comp = new LabelledDial("Delay Time", this, "tone" + tone + "tonedelaytime", color, 0, 127);
        hbox.add(comp);

        comp = new LabelledDial("Level", this, "tone" + tone + "tonelevel", color, 0, 127);
        hbox.add(comp);

        comp = new LabelledDial("Pan", this, "tone" + tone + "tonepan", color, 0, 127);
        hbox.add(comp);

        vbox.add(hbox);

        hbox = new HBox();

        comp = new LabelledDial("Vel Low", this, "tone" + tone + "velocityrangelower", color, 1, 127);
        hbox.add(comp);

        comp = new LabelledDial("Vel High", this, "tone" + tone + "velocityrangeupper", color, 1, 127);
        hbox.add(comp);

        comp = new LabelledDial("Coarse", this, "tone" + tone + "pitchcoarse", color, 0, 96)
            {
            public String map(int value)
                {
                return String.valueOf(value - 48);
                }
            };
        hbox.add(comp);

        comp = new LabelledDial("Fine", this, "tone" + tone + "pitchfine", color, 0, 100)
            {
            public String map(int value)
                {
                return String.valueOf(value - 50);
                }
            };
        hbox.add(comp);

        comp = new LabelledDial("Filter", this, "tone" + tone + "filtertype", color, 0, 4);
        hbox.add(comp);

        comp = new LabelledDial("Cutoff", this, "tone" + tone + "cutofffrequency", color, 0, 127);
        hbox.add(comp);

        comp = new LabelledDial("Res", this, "tone" + tone + "resonance", color, 0, 127);
        hbox.add(comp);

        vbox.add(hbox);

        hbox = new HBox();

        comp = new LabelledDial("LFO1 Rate", this, "tone" + tone + "lfo1rate", color, 0, 127);
        hbox.add(comp);

        comp = new LabelledDial("LFO1 Delay", this, "tone" + tone + "lfo1delay", color, 0, 127);
        hbox.add(comp);

        comp = new LabelledDial("LFO2 Rate", this, "tone" + tone + "lfo2rate", color, 0, 127);
        hbox.add(comp);

        comp = new LabelledDial("LFO2 Delay", this, "tone" + tone + "lfo2delay", color, 0, 127);
        hbox.add(comp);

        vbox.add(hbox);

        hbox = new HBox();

        comp = new LabelledDial("PEnv Depth", this, "tone" + tone + "penvdepth", color, 0, 24);
        hbox.add(comp);

        comp = new LabelledDial("P T1", this, "tone" + tone + "penvtime1", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("P T2", this, "tone" + tone + "penvtime2", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("P T3", this, "tone" + tone + "penvtime3", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("P T4", this, "tone" + tone + "penvtime4", color, 0, 127);
        hbox.add(comp);

        vbox.add(hbox);

        hbox = new HBox();

        comp = new LabelledDial("P L1", this, "tone" + tone + "penvlevel1", color, 0, 126);
        hbox.add(comp);
        comp = new LabelledDial("P L2", this, "tone" + tone + "penvlevel2", color, 0, 126);
        hbox.add(comp);
        comp = new LabelledDial("P L3", this, "tone" + tone + "penvlevel3", color, 0, 126);
        hbox.add(comp);
        comp = new LabelledDial("P L4", this, "tone" + tone + "penvlevel4", color, 0, 126);
        hbox.add(comp);

        vbox.add(hbox);

        hbox = new HBox();

        comp = new LabelledDial("FEnv Depth", this, "tone" + tone + "tvfenvdepth", color, 0, 126);
        hbox.add(comp);

        comp = new LabelledDial("F T1", this, "tone" + tone + "tvfenvtime1", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("F T2", this, "tone" + tone + "tvfenvtime2", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("F T3", this, "tone" + tone + "tvfenvtime3", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("F T4", this, "tone" + tone + "tvfenvtime4", color, 0, 127);
        hbox.add(comp);

        vbox.add(hbox);

        hbox = new HBox();

        comp = new LabelledDial("F L1", this, "tone" + tone + "tvfenvlevel1", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("F L2", this, "tone" + tone + "tvfenvlevel2", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("F L3", this, "tone" + tone + "tvfenvlevel3", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("F L4", this, "tone" + tone + "tvfenvlevel4", color, 0, 127);
        hbox.add(comp);

        vbox.add(hbox);

        hbox = new HBox();

        comp = new LabelledDial("A T1", this, "tone" + tone + "tvaenvtime1", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("A T2", this, "tone" + tone + "tvaenvtime2", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("A T3", this, "tone" + tone + "tvaenvtime3", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("A T4", this, "tone" + tone + "tvaenvtime4", color, 0, 127);
        hbox.add(comp);

        comp = new LabelledDial("A L1", this, "tone" + tone + "tvaenvlevel1", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("A L2", this, "tone" + tone + "tvaenvlevel2", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("A L3", this, "tone" + tone + "tvaenvlevel3", color, 0, 127);
        hbox.add(comp);

        vbox.add(hbox);

        hbox = new HBox();
        comp = new LabelledDial("Out", this, "tone" + tone + "outputassign", color, 0, 3);
        hbox.add(comp);
        comp = new LabelledDial("Mix/EFX", this, "tone" + tone + "mixefxsendlevel", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("Ch Send", this, "tone" + tone + "chorussendlevel", color, 0, 127);
        hbox.add(comp);
        comp = new LabelledDial("Rev Send", this, "tone" + tone + "reverbsendlevel", color, 0, 127);
        hbox.add(comp);

        vbox.add(hbox);
        category.add(vbox, BorderLayout.CENTER);
        return category;
        }

    public String revisePatchName(String name)
        {
        name = super.revisePatchName(name);
        if (name.length() > MAXIMUM_NAME_LENGTH)
            name = name.substring(0, MAXIMUM_NAME_LENGTH);

        StringBuffer nameb = new StringBuffer(name);
        int len = nameb.length();
        for(int i = 0 ; i < len; i++)
            {
            char c = nameb.charAt(i);
            if (c < 32 || c > 127)
                nameb.setCharAt(i, ' ');
            }
        name = nameb.toString();
        return super.revisePatchName(name);
        }

    public String getPatchName(Model model)
        {
        return model.get("name", "Untitled");
        }

    public static String getSynthName() { return "Roland JV-2080"; }

    public boolean getSendsParametersAfterNonMergeParse() { return false; }

    int parseOne(byte[] data)
        {
        if (parseStatus == 0)
            {
            if (data.length != 83)
                return PARSE_FAILED;

            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < MAXIMUM_NAME_LENGTH; i++)
                {
                sb.append((char)(data[9 + i] & 0x7F));
                }
            model.set("name", revisePatchName(sb.toString()));

            parseStatus = 1;
            return PARSE_INCOMPLETE;
            }
        else if (parseStatus >= 1 && parseStatus <= 4)
            {
            if (data.length != 140)
                return PARSE_FAILED;

            int tone = parseStatus;
            int payloadPos = 9;
            int payloadLen = data.length - 11;
            if (payloadLen <= TONE_OFFSET_REVERB_SEND_LEVEL)
                return PARSE_FAILED;

            model.set("tone" + tone + "toneswitch", (data[payloadPos + TONE_OFFSET_TONE_SWITCH] & 0x01));
            model.set("tone" + tone + "wavegrouptype", (data[payloadPos + TONE_OFFSET_WAVE_GROUP_TYPE] & 0x03));
            model.set("tone" + tone + "wavegroupid", (data[payloadPos + TONE_OFFSET_WAVE_GROUP_ID] & 0x7F));

            int waveMSB = (data[payloadPos + TONE_OFFSET_WAVE_NUMBER_MSB] & 0x7F);
            int waveLSB = (data[payloadPos + TONE_OFFSET_WAVE_NUMBER_LSB] & 0x7F);
            int waveNumber = ((waveMSB & 0x0F) << 4) | (waveLSB & 0x0F);
            model.set("tone" + tone + "wavenumber", waveNumber + 1);

            model.set("tone" + tone + "tonedelaymode", (data[payloadPos + TONE_OFFSET_TONE_DELAY_MODE] & 0x7F));
            model.set("tone" + tone + "tonedelaytime", (data[payloadPos + TONE_OFFSET_TONE_DELAY_TIME] & 0x7F));
            model.set("tone" + tone + "velocityrangelower", (data[payloadPos + TONE_OFFSET_VELOCITY_RANGE_LOWER] & 0x7F));
            model.set("tone" + tone + "velocityrangeupper", (data[payloadPos + TONE_OFFSET_VELOCITY_RANGE_UPPER] & 0x7F));
            model.set("tone" + tone + "pitchcoarse", (data[payloadPos + TONE_OFFSET_COARSE_TUNE] & 0x7F));
            model.set("tone" + tone + "pitchfine", (data[payloadPos + TONE_OFFSET_FINE_TUNE] & 0x7F));

            model.set("tone" + tone + "lfo1rate", (data[payloadPos + TONE_OFFSET_LFO1_RATE] & 0x7F));
            model.set("tone" + tone + "lfo1delay", (data[payloadPos + TONE_OFFSET_LFO1_DELAY_TIME] & 0x7F));
            model.set("tone" + tone + "lfo2rate", (data[payloadPos + TONE_OFFSET_LFO2_RATE] & 0x7F));
            model.set("tone" + tone + "lfo2delay", (data[payloadPos + TONE_OFFSET_LFO2_DELAY_TIME] & 0x7F));

            model.set("tone" + tone + "penvdepth", (data[payloadPos + TONE_OFFSET_PITCH_ENV_DEPTH] & 0x7F));
            model.set("tone" + tone + "penvtime1", (data[payloadPos + TONE_OFFSET_PITCH_ENV_TIME_1] & 0x7F));
            model.set("tone" + tone + "penvtime2", (data[payloadPos + TONE_OFFSET_PITCH_ENV_TIME_2] & 0x7F));
            model.set("tone" + tone + "penvtime3", (data[payloadPos + TONE_OFFSET_PITCH_ENV_TIME_3] & 0x7F));
            model.set("tone" + tone + "penvtime4", (data[payloadPos + TONE_OFFSET_PITCH_ENV_TIME_4] & 0x7F));
            model.set("tone" + tone + "penvlevel1", (data[payloadPos + TONE_OFFSET_PITCH_ENV_LEVEL_1] & 0x7F));
            model.set("tone" + tone + "penvlevel2", (data[payloadPos + TONE_OFFSET_PITCH_ENV_LEVEL_2] & 0x7F));
            model.set("tone" + tone + "penvlevel3", (data[payloadPos + TONE_OFFSET_PITCH_ENV_LEVEL_3] & 0x7F));
            model.set("tone" + tone + "penvlevel4", (data[payloadPos + TONE_OFFSET_PITCH_ENV_LEVEL_4] & 0x7F));
            model.set("tone" + tone + "filtertype", (data[payloadPos + TONE_OFFSET_FILTER_TYPE] & 0x7F));
            model.set("tone" + tone + "cutofffrequency", (data[payloadPos + TONE_OFFSET_CUTOFF_FREQUENCY] & 0x7F));
            model.set("tone" + tone + "resonance", (data[payloadPos + TONE_OFFSET_RESONANCE] & 0x7F));

            model.set("tone" + tone + "tvfenvdepth", (data[payloadPos + TONE_OFFSET_FILTER_ENV_DEPTH] & 0x7F));
            model.set("tone" + tone + "tvfenvtime1", (data[payloadPos + TONE_OFFSET_FILTER_ENV_TIME_1] & 0x7F));
            model.set("tone" + tone + "tvfenvtime2", (data[payloadPos + TONE_OFFSET_FILTER_ENV_TIME_2] & 0x7F));
            model.set("tone" + tone + "tvfenvtime3", (data[payloadPos + TONE_OFFSET_FILTER_ENV_TIME_3] & 0x7F));
            model.set("tone" + tone + "tvfenvtime4", (data[payloadPos + TONE_OFFSET_FILTER_ENV_TIME_4] & 0x7F));
            model.set("tone" + tone + "tvfenvlevel1", (data[payloadPos + TONE_OFFSET_FILTER_ENV_LEVEL_1] & 0x7F));
            model.set("tone" + tone + "tvfenvlevel2", (data[payloadPos + TONE_OFFSET_FILTER_ENV_LEVEL_2] & 0x7F));
            model.set("tone" + tone + "tvfenvlevel3", (data[payloadPos + TONE_OFFSET_FILTER_ENV_LEVEL_3] & 0x7F));
            model.set("tone" + tone + "tvfenvlevel4", (data[payloadPos + TONE_OFFSET_FILTER_ENV_LEVEL_4] & 0x7F));

            model.set("tone" + tone + "tonelevel", (data[payloadPos + TONE_OFFSET_TONE_LEVEL] & 0x7F));
            model.set("tone" + tone + "tonepan", (data[payloadPos + TONE_OFFSET_TONE_PAN] & 0x7F));

            model.set("tone" + tone + "tvaenvtime1", (data[payloadPos + TONE_OFFSET_AMP_ENV_TIME_1] & 0x7F));
            model.set("tone" + tone + "tvaenvtime2", (data[payloadPos + TONE_OFFSET_AMP_ENV_TIME_2] & 0x7F));
            model.set("tone" + tone + "tvaenvtime3", (data[payloadPos + TONE_OFFSET_AMP_ENV_TIME_3] & 0x7F));
            model.set("tone" + tone + "tvaenvtime4", (data[payloadPos + TONE_OFFSET_AMP_ENV_TIME_4] & 0x7F));
            model.set("tone" + tone + "tvaenvlevel1", (data[payloadPos + TONE_OFFSET_AMP_ENV_LEVEL_1] & 0x7F));
            model.set("tone" + tone + "tvaenvlevel2", (data[payloadPos + TONE_OFFSET_AMP_ENV_LEVEL_2] & 0x7F));
            model.set("tone" + tone + "tvaenvlevel3", (data[payloadPos + TONE_OFFSET_AMP_ENV_LEVEL_3] & 0x7F));

            model.set("tone" + tone + "outputassign", (data[payloadPos + TONE_OFFSET_OUTPUT_ASSIGN] & 0x7F));
            model.set("tone" + tone + "mixefxsendlevel", (data[payloadPos + TONE_OFFSET_MIX_EFX_SEND_LEVEL] & 0x7F));
            model.set("tone" + tone + "chorussendlevel", (data[payloadPos + TONE_OFFSET_CHORUS_SEND_LEVEL] & 0x7F));
            model.set("tone" + tone + "reverbsendlevel", (data[payloadPos + TONE_OFFSET_REVERB_SEND_LEVEL] & 0x7F));

            parseStatus++;
            if (parseStatus == 5)
                {
                parseStatus = 0;
                revise();
                return PARSE_SUCCEEDED;
                }
            return PARSE_INCOMPLETE;
            }
        else
            {
            parseStatus = 0;
            return PARSE_FAILED;
            }
        }

    public int parse(byte[] data, boolean fromFile)
        {
        if (fromFile)
            {
            parseStatus = 0;
            int lastResult = PARSE_FAILED;

            int start = 0;
            while(start < data.length)
                {
                while(start < data.length && data[start] != (byte)0xF0) start++;
                if (start >= data.length) break;

                int end = start;
                while(end < data.length && data[end] != (byte)0xF7) end++;
                if (end >= data.length) break;

                int len = end - start + 1;
                byte[] msg = new byte[len];
                System.arraycopy(data, start, msg, 0, len);
                lastResult = parseOne(msg);
                if (lastResult == PARSE_FAILED)
                    {
                    parseStatus = 0;
                    return PARSE_FAILED;
                    }
                start = end + 1;
                }

            if (lastResult == PARSE_SUCCEEDED || lastResult == PARSE_SUCCEEDED_UNTITLED)
                return lastResult;
            else
                return PARSE_FAILED;
            }
        else
            {
            if (data.length < 12) return PARSE_FAILED;
            if (data[0] != (byte)0xF0) return PARSE_FAILED;
            if (data[1] != (byte)0x41) return PARSE_FAILED;
            if (data[3] != (byte)MODEL_ID) return PARSE_FAILED;
            if (data[4] != (byte)COMMAND_DT1) return PARSE_FAILED;

            int a1 = data[5] & 0x7F;
            int a2 = data[6] & 0x7F;
            int a3 = data[7] & 0x7F;
            int a4 = data[8] & 0x7F;
            int addr = linearAddress(a1, a2, a3, a4);

            int payloadPos = 9;
            int payloadLen = data.length - 11;
            if (payloadLen <= 0) return PARSE_FAILED;

            if (DEBUG)
                {
                System.err.println("RolandJV2080 DT1 " +
                    String.format("%02X %02X %02X %02X", a1, a2, a3, a4) +
                    " len=" + payloadLen);
                }

            byte[] payload = new byte[payloadLen];
            System.arraycopy(data, payloadPos, payload, 0, payloadLen);

            // Support both TEMP patch dumps (03 00 ...) and USER patch dumps (11 xx ...)
            int prefix1 = TEMP_PATCH_ADDR_PREFIX[0];
            int prefix2 = TEMP_PATCH_ADDR_PREFIX[1];
            if (a1 == 0x11)
                {
                prefix1 = a1;
                prefix2 = a2;
                }

            int baseCommon = linearAddress(prefix1, prefix2, 0x00, 0x00);
            int baseTone1 = linearAddress(prefix1, prefix2, 0x10, 0x00);
            int baseTone2 = linearAddress(prefix1, prefix2, 0x12, 0x00);
            int baseTone3 = linearAddress(prefix1, prefix2, 0x14, 0x00);
            int baseTone4 = linearAddress(prefix1, prefix2, 0x16, 0x00);

            boolean dumpComplete = (receivedCountCommon == COMMON_SIZE &&
                receivedCountTone[1] == TONE_SIZE && receivedCountTone[2] == TONE_SIZE && receivedCountTone[3] == TONE_SIZE && receivedCountTone[4] == TONE_SIZE);

            // If we're receiving the start of a dump (common base), reset buffers so UI will update cleanly.
            if (addr == baseCommon)
                {
                resetReceived();
                }

            // Front-panel dumps don't always start at baseCommon.  If we already have a complete dump and
            // we see any new DT1 in the current patch address ranges, treat it as a new dump and reset.
            if (dumpComplete)
                {
                boolean inRange =
                    ((addr >= baseCommon && addr < baseCommon + COMMON_SIZE) ||
                    (addr >= baseTone1 && addr < baseTone1 + TONE_SIZE) ||
                    (addr >= baseTone2 && addr < baseTone2 + TONE_SIZE) ||
                    (addr >= baseTone3 && addr < baseTone3 + TONE_SIZE) ||
                    (addr >= baseTone4 && addr < baseTone4 + TONE_SIZE));
                if (inRange)
                    {
                    resetReceived();
                    }
                }

            if (addr >= baseCommon && addr < baseCommon + COMMON_SIZE)
                {
                int offset = addr - baseCommon;
                int max = Math.min(payload.length, COMMON_SIZE - offset);
                if (max > 0)
                    {
                    System.arraycopy(payload, 0, commonData, offset, max);
                    for(int i = 0; i < max; i++)
                        {
                        int idx = offset + i;
                        if (!commonReceived[idx]) { commonReceived[idx] = true; receivedCountCommon++; }
                        }
                    if (receivedCountCommon == COMMON_SIZE)
                        {
                        StringBuilder sb = new StringBuilder();
                        for(int i = 0; i < MAXIMUM_NAME_LENGTH && i < COMMON_SIZE; i++)
                            {
                            sb.append((char)(commonData[i] & 0x7F));
                            }
                        model.set("name", revisePatchName(sb.toString()));
                        loadCommonFromBuffer(commonData);
                        }
                    }
                }
            else if (addr >= baseTone1 && addr < baseTone1 + TONE_SIZE)
                {
                loadToneFromPayload(1, addr - baseTone1, payload);
                }
            else if (addr >= baseTone2 && addr < baseTone2 + TONE_SIZE)
                {
                loadToneFromPayload(2, addr - baseTone2, payload);
                }
            else if (addr >= baseTone3 && addr < baseTone3 + TONE_SIZE)
                {
                loadToneFromPayload(3, addr - baseTone3, payload);
                }
            else if (addr >= baseTone4 && addr < baseTone4 + TONE_SIZE)
                {
                loadToneFromPayload(4, addr - baseTone4, payload);
                }

            if (receivedCountCommon == COMMON_SIZE && receivedCountTone[1] == TONE_SIZE && receivedCountTone[2] == TONE_SIZE && receivedCountTone[3] == TONE_SIZE && receivedCountTone[4] == TONE_SIZE)
                {
                revise();
                return PARSE_SUCCEEDED;
                }
            return PARSE_INCOMPLETE;
            }
        }

    public void revise()
        {
        super.revise();
        String nm = model.get("name", "Init Patch");
        String newnm = revisePatchName(nm);
        if (!nm.equals(newnm))
            model.set("name", newnm);
        }

    public Object[] emitAll(Model tempModel, boolean toWorkingMemory, boolean toFile)
        {
        return new Object[0];
        }

    int computePatchCommonDT1WriteOffset(int offset)
        {
        int o = offset & 0x7F;
        int bias = 0;
        if (o >= DT1_WRITE_BIAS_PATCH_TEMPO_AND_LATER_START)
            bias = DT1_WRITE_BIAS_PLUS_ONE;
        else if (o >= DT1_WRITE_BIAS_CHORUS_REVERB_DELAY_START && o <= DT1_WRITE_BIAS_CHORUS_REVERB_DELAY_END)
            bias = DT1_WRITE_BIAS_MINUS_ONE;
        return (o + bias) & 0x7F;
        }

    public byte[] emit(String key)
        {
        Integer offset = COMMON_KEY_TO_OFFSET.get(key);
        if (offset == null)
            return null;

        int o = offset.intValue() & 0x7F;
        int writeOffset = computePatchCommonDT1WriteOffset(o);

        int[] addr = buildAddress(0x00, writeOffset);
        byte[] payload;
        if (key.equals("patchtempo"))
            {
            int tempo = model.get(key, 120);
            if (tempo < 20) tempo = 20;
            if (tempo > 250) tempo = 250;
            int hi = (tempo >>> 4) & 0x7F;
            int lo = (tempo & 0x0F) & 0x7F;
            payload = new byte[] { (byte)hi, (byte)lo };
            }
        else
            {
            int val = model.get(key, 0) & 0x7F;
            payload = new byte[] { (byte)val };
            }

        byte[] dt1 = buildDT1(addr, payload);
        if (DEBUG)
            {
            System.err.println("RolandJV2080 emit key=" + key + " offset=" + String.format("%02X", o) + " writeOffset=" + String.format("%02X", writeOffset) + " len=" + payload.length);
            logDT1(dt1);
            }
        return dt1;
        }

    public byte[] requestCurrentDump()
        {
        // Request the TEMP/edit-buffer patch common block (03 00 00 00)
        return buildRQ1(buildAddress(0x00, 0x00), new int[] { 0x00, 0x00, 0x00, COMMON_SIZE });
        }

    public void performRequestCurrentDump()
        {
        resetReceived();

        byte[] common = requestCurrentDump();
        byte[] tone1 = buildRQ1(buildAddress(0x10, 0x00), new int[] { 0x00, 0x00, 0x01, 0x01 });
        byte[] tone2 = buildRQ1(buildAddress(0x12, 0x00), new int[] { 0x00, 0x00, 0x01, 0x01 });
        byte[] tone3 = buildRQ1(buildAddress(0x14, 0x00), new int[] { 0x00, 0x00, 0x01, 0x01 });
        byte[] tone4 = buildRQ1(buildAddress(0x16, 0x00), new int[] { 0x00, 0x00, 0x01, 0x01 });

        tryToSendSysex(common);
        simplePause(50);
        tryToSendSysex(tone1);
        simplePause(50);
        tryToSendSysex(tone2);
        simplePause(50);
        tryToSendSysex(tone3);
        simplePause(50);
        tryToSendSysex(tone4);
        }

    public boolean getAlwaysChangesPatchesOnRequestDump() { return false; }
    }
