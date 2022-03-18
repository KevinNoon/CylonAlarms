package com.optimised.cylonAlarms.tools.queueToAlarm;

import com.optimised.cylonAlarms.model.queueToAlarm.Alarm;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Properties;


public class AlarmTypes {
    static String CYLON_DIR = "C:\\UnitronUC32";

    static public Alarm AlarmPacketToAlarm(MappedByteBuffer wn3000ini, Timestamp submitted, String siteName, int siteNumber, byte[] packet) {

        if (packet.length < 10) return null;
        int alarmType = packet[10] & 0xff;
        Alarm alarm = new Alarm();
        alarm.setSiteName(siteName);
        alarm.setSiteNumber(siteNumber);
        alarm.setSummited(submitted);
        alarm.setUCC4Name(GetUCC4Name(wn3000ini,alarm.getSiteNumber(), packet[8]));

        alarm.setUCC4Number(packet[8]);
        alarm.setPriority(packet[11] & 0xFF);


        try {
            InputStream inputStream;
            String propFileName = "config.properties";
            File external = new File("./" + propFileName);
            if (external.exists()) {
                inputStream = new FileInputStream("./config.properties");
            } else {
                inputStream = ReadAlarmQueue.class.getClassLoader().getResourceAsStream(propFileName);
            }
            Properties prop = new Properties();
            prop.load(inputStream);
            CYLON_DIR = prop.getProperty("cylon.dir");

        } catch (IOException e) {
            e.printStackTrace();
        }


        switch (alarmType) {
            case 1: {
                Type01(alarm,wn3000ini, packet);
                break;
            }
            case 3: {
                Type03(alarm,packet);
                break;
            }
            case 8: {
                Type08(alarm, packet);
                break;
            }
            case 9: {
                Type09(alarm,wn3000ini,packet);
                break;
            }
            case 12: {
                Type12(alarm, packet);
                break;
            }
            case 13: {
                Type13(alarm,packet);
                break;
            }
            default:
                break;
        }
        return alarm;
    }


    private static void Type01(Alarm alarm,MappedByteBuffer wn3000ini,byte[] packet) {

        alarm.setUC16Name(GetUCC16Name( wn3000ini,alarm.getSiteNumber(), packet[8], packet[12]));
        alarm.setUC16Number(packet[12]);

        alarm.setStartedAt(calcTime(packet[22], packet[23], packet[24], packet[25]));
        alarm.setEndedAt(calcTime(packet[26], packet[27], packet[28], packet[29]));
        alarm.setAlarmType(packet[10]);

        if (packet[54] != 0) {
            alarm.setTriggerPointName(getText(packet, 39, 46));
            alarm.setTriggerPointNumber(packet[54] & 0xff);
            alarm.setTriggerPointType(packet[55] != 0);
            if (packet[54] != 255) {
                alarm.setTriggerPointValue(getValue(packet[57], packet[58], packet[59], packet[60]));
            }
            alarm.setTriggerPointUnit(packet[56] + "");
        }
        alarm.setAlarmNumber(packet[37] & 0xff);
        alarm.setProgramModuleNumber(packet[31] & 0xff);
        if (packet[70] != 0)
            alarm.setAlarmMessage(getAlarmMessage(packet, 70));
        alarm.setStringNumber(packet[69] & 0xFF);
        alarm.setExtraBits(packet[19] & 0xFF);
        alarm.setExtraInteger(get2ByteInt(packet[20], packet[21]));
       // System.out.println(alarm);
    }

    private static void Type03(Alarm alarm,byte[] packet) {
        alarm.setStartedAt(calcTime(packet[12], packet[13], packet[14], packet[15]));
        alarm.setAlarmMessage(getOffLineMessage(packet));
        alarm.setAlarmType(10);
        //System.out.println(alarm);
    }

    private static void Type08(Alarm alarm, byte[] packet) {
        alarm.setStartedAt(calcTime(packet[12], packet[13], packet[14], packet[15]));
        alarm.setAlarmMessage("Dial out alarm failed for number " + (packet[17] & 0xFF));
        alarm.setAlarmType(10);
        //System.out.println(alarm);
    }

    private static void Type09(Alarm alarm,MappedByteBuffer wn3000ini,byte[] packet) {
        alarm.setUC16Name(GetUCC16Name( wn3000ini,alarm.getSiteNumber(), packet[8], packet[16]));
        alarm.setUC16Number(packet[16]);

        alarm.setStartedAt(calcTime(packet[25], packet[26], packet[27], packet[28]));
        alarm.setEndedAt(calcTime(packet[29], packet[30], packet[31], packet[32]));
        alarm.setAlarmType(packet[10]);

        if (packet[54] != 0) {
            alarm.setTriggerPointName(getType9Text(packet));
            alarm.setTriggerPointNumber(get2ByteInt(packet[88], packet[89]));
            alarm.setTriggerPointType(packet[90] != 0);
            alarm.setTriggerPointValue(getValue(packet[92], packet[93], packet[94], packet[95]));
            alarm.setTriggerPointUnit(packet[91] + "");
        }
        alarm.setAlarmNumber(get2ByteInt(packet[17], packet[18]));
        alarm.setProgramModuleNumber(get2ByteInt(packet[19], packet[20]));
        if (packet[101] != 0)
            alarm.setAlarmMessage(getAlarmMessage(packet, 101));
        alarm.setStringNumber(get2ByteInt(packet[37], packet[38]));
        alarm.setExtraBits(packet[22] & 0xFF);
        alarm.setExtraInteger(get2ByteInt(packet[23], packet[24]));
        //System.out.println(alarm);
    }

    private static void Type12(Alarm alarm, byte[] packet) {
        alarm.setStartedAt(calcTime(packet[12], packet[13], packet[14], packet[15]));
        alarm.setAlarmMessage(getAlarmMessage(packet, 20));
        alarm.setAlarmType(packet[10]);
        //System.out.println(alarm);
    }

    private static void Type13(Alarm alarm,byte[] packet) {
        alarm.setStartedAt(calcTime(packet[12], packet[13], packet[14], packet[15]));
        alarm.setAlarmMessage(ModbusMessage(packet));
        alarm.setAlarmType(packet[10]);
        //System.out.println(alarm);
    }

    private static String ModbusMessage(byte[] packet) {
        String Status = "offline";
        if (packet[17] == 1) Status = "Online";
        if (packet[17] == 2) Status = "offline disabled";
        Status = "External modbus device: Modbus " + (packet[16] & 0xFF) + " is " + Status;
        return Status;
    }

    private static String getOffLineMessage(byte[] packet) {
        StringBuilder message = new StringBuilder("offline controllers ");
        boolean offline = false;
        int packetIndex = 16 + 1;
        int count = packet[16];
        int ctrlNo = 1;
        int map = 0;
        if (count >= 1) map = 128;
        while (count > 0) {
            if ((map & packet[packetIndex]) < 1) {
                offline = true;
                message.append(" Ctrl_").append(ctrlNo);
            }
            map = map / 2;
            ctrlNo++;
            count--;
            if (((ctrlNo - 1) % 8) == 0) {
                packetIndex++;
                map = 128;
            }
        }
        if (!offline) message = new StringBuilder("all controllers online");
        return message.toString();
    }

    private static String getAlarmMessage(byte[] packet, int start) {
        int end = start;
        while (end < packet.length && packet[end] != 0) {
            end++;
        }
        return getText(packet, start, end);
    }

    private static Float getValue(byte a, byte b, byte c, byte d) {
        byte[] array = {a, b, c, d};
        if ((a & 0xFF) == 255) return 0.0F;
        return ByteBuffer.wrap(array).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    private static int get2ByteInt(byte a, byte b) {
        return (a & 0xFF) * 256 + (b & 0xFF);
    }

    private static Timestamp calcTime(byte a, byte b, byte c, byte d) {
        int timeOffset = ((((a * 256) + b) * 256) + c) * 256 + d;
        if (timeOffset == 0) return null;
        Timestamp original = Timestamp.valueOf("1988-1-1 00:00:00");
        return new Timestamp(original.getTime() + (timeOffset * 1000L));
    }

    private static String getType9Text(byte[] array) {
        int end = 87;
        for (int i = 48; i <= 87; i++) {
            end = i;
            if (array[i] < 32) break;
        }
        if (end == 48) return "";
        return (getText(array, 48, end));
    }

    public static String getText(byte[] array, int startIndex, int endIndex) {
        byte[] slicedArray = new byte[endIndex - startIndex];

        System.arraycopy(array, startIndex, slicedArray, 0, slicedArray.length);
        if (slicedArray[0] == 0) return "";
        String alarm = new String(slicedArray, StandardCharsets.UTF_8);
        if (alarm.matches("[a-zA-Z.0-9 ]*")) {
            return new String(slicedArray, StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }


    static public String GetUCC4Name(MappedByteBuffer wn3000ini,int SiteNo, byte UCC4No){

        String siteDIR = GetSitePath( wn3000ini,SiteNo);
        String path = CYLON_DIR + "//" + siteDIR + "//System//Site.ini";
        return GetINIKey(path,"OS" + UCC4No,"Name");
    }
    static public String GetUCC16Name(MappedByteBuffer wn3000ini,int SiteNo, byte UCC4No, byte UCC16No){
        String siteDIR = GetSitePath(wn3000ini,SiteNo);
        String path = CYLON_DIR + "//" + siteDIR + "//System//Site.ini";
        return GetINIKey(path,"OS" + UCC4No,"UC16_" + UCC16No);
    }

    static public String GetSitePath(MappedByteBuffer wn3000ini,int SiteNo) {
        return GetINIKeyBuffer(wn3000ini,"Site" + SiteNo,"Directory");
    }

    static public String GetINIKey(String file, String section, String key){
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                if (sCurrentLine.equals("["+section+"]")) {
                    while (((sCurrentLine = br.readLine()) != null) && (! sCurrentLine.startsWith("["))){
                        if (sCurrentLine.startsWith(key)) {
                            return sCurrentLine.substring(sCurrentLine.lastIndexOf("=") + 1);
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            return "";
           // e.printStackTrace();
        }
        return "";
    }

    static public String GetINIKeyBuffer(MappedByteBuffer br, String section, String key) {
        StringBuilder value = new StringBuilder();
        int i = 0;
        while (i < br.capacity()) {
            StringBuilder sCurrentLine = new StringBuilder();
            if (((char) br.get(i++)) == 91) {
                while (((char) br.get(i++)) != 93) {
                    sCurrentLine.append((char) br.get(i - 1));
                }
                if (sCurrentLine.toString().equals(section)) {
                    StringBuilder sKey = new StringBuilder();
                    while (!sKey.toString().contains(key)) {
                        br.get(i++);
                        sKey.append((char) br.get(i - 1));
                    }

                    i++;
                    while (((char) br.get(i++)) != 13) {
                        value.append((char) br.get(i - 1));
                    }
                    //System.out.println(value);
                }
            }
        }
        return value.toString();
    }
}
