package edisyn.synth.rolandjv2080;

import edisyn.*;
import edisyn.gui.*;
import java.awt.*;
import javax.swing.*;

public class RolandJV2080 extends Synth
    {
    public static final int MAXIMUM_NAME_LENGTH = 12;

    int parseStatus = 0;

    static final int MODEL_ID = 0x6A;
    static final int COMMAND_RQ1 = 0x11;
    static final int COMMAND_DT1 = 0x12;

    static final int[] TEMP_PATCH_ADDR_PREFIX = { 0x03, 0x00 };

    static final int COMMON_SIZE = 0x48;
    static final int TONE_SIZE = 0x101;

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

    public RolandJV2080()
        {
        model.set("name", "Init Patch   ");

        JComponent soundPanel = new SynthPanel(this);
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        hbox.add(addNameGlobal(Style.COLOR_GLOBAL()));
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
            if (payloadLen <= TONE_OFFSET_CHORUS_SEND_LEVEL)
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
            byte[] payload = new byte[payloadLen];
            System.arraycopy(data, payloadPos, payload, 0, payloadLen);

            int baseCommon = linearAddress(TEMP_PATCH_ADDR_PREFIX[0], TEMP_PATCH_ADDR_PREFIX[1], 0x00, 0x00);
            int baseTone1 = linearAddress(TEMP_PATCH_ADDR_PREFIX[0], TEMP_PATCH_ADDR_PREFIX[1], 0x10, 0x00);
            int baseTone2 = linearAddress(TEMP_PATCH_ADDR_PREFIX[0], TEMP_PATCH_ADDR_PREFIX[1], 0x12, 0x00);
            int baseTone3 = linearAddress(TEMP_PATCH_ADDR_PREFIX[0], TEMP_PATCH_ADDR_PREFIX[1], 0x14, 0x00);
            int baseTone4 = linearAddress(TEMP_PATCH_ADDR_PREFIX[0], TEMP_PATCH_ADDR_PREFIX[1], 0x16, 0x00);

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

    public byte[] requestCurrentDump()
        {
        return new byte[0];
        }

    public void performRequestCurrentDump()
        {
        resetReceived();

        byte[] common = buildRQ1(buildAddress(0x00, 0x00), new int[] { 0x00, 0x00, 0x00, COMMON_SIZE });
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
