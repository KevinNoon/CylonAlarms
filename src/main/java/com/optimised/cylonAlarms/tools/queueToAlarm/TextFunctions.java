package com.optimised.cylonAlarms.tools.queueToAlarm;

public class TextFunctions {
    public static String getText(Byte[] array, int startIndex, int endIndex) {
        String alarms = "";
        int index = startIndex;
        while (index <= endIndex ) {
            if ((array[index] & 0xFF) > 31 && (array[index] & 0xFF) < 126){
                alarms = alarms + (char)(array[index] & 0xFF);
            } else {
                break;
            }
            index++;
        }
        return alarms;
    }

    public static String getOffLineMessage(Byte[] packet, String prefix,int messagePos) {
        StringBuilder message = new StringBuilder("offline controllers ");
        boolean offline = false;
        int packetIndex = messagePos + 1;
        int count = (packet[messagePos] & 0xFF);
        int ctrlNo = 1;
        int map = 0;
        if (count >= 1) map = 128;
        while (count > 0) {
            if ((map & packet[packetIndex]) < 1) {
                offline = true;
                message.append(prefix).append(ctrlNo);
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

    public static String getModbusMessage(Byte[] packet, int messageStart) {
        String status = "Modbus device_";
        status += status + (packet[messageStart] & 0xFF);
        status += getText(packet,messageStart + 2,packet.length - 1);

        switch (packet[messageStart + 1]){
            case 0: {status += " offline"; break;}
            case 1: {status += " Online"; break;}
            case 2: {status += " offline disabled"; break;}
            default: status += " offline";
        }
        return status;
    }

    public static String getBACNetMessage(Byte[] packet, int messageStart) {
        String message = "BACNet Device " + MathFunctions.get4ByteInt(packet[messageStart],packet[messageStart + 1], packet[messageStart +3], packet[messageStart +4]);
        switch (packet[messageStart + 5]){
            case 0: {message += " offline"; break;}
            case 1: {message += " Online"; break;}
            default: message += " offline";
        }
        message += message + " " + getText(packet,messageStart + 6,packet.length - 1);
        return message;
    }

    public static String getWirelessSensorAlarm(Byte[] packet, int messageStart){
        String message = "Wireless Sensor";
        switch (packet[messageStart]){
            case 0 : {
                message += " Offline"; break;
            }
            case 1 : {
                message += " Online"; break;
            }
            case 2 : {
                message += " Alarm Disabled"; break;
            }
            default: {
                message += " error";
            }
        }

        message += " BaseStation_" + (packet[messageStart + 1] & 0xFF);
        message += " SensorNo_" + (packet[messageStart + 2] & 0xFF);
        message += " SensorType_";
        switch (packet[messageStart + 3]){
            case 1 : {
                message += "SR04"; break;
            }
            case 2 : {
                message += "SR04P"; break;
            }
            case 3 : {
                message += "SR04PT"; break;
            }
            case 4 : {
                message += "SR04PST"; break;
            }
            case 5 : {
                message += "SR04rH"; break;
            }
            case 6 : {
                message += "SR04PrH"; break;
            }
            case 7 : {
                message += "SR04PTrH"; break;
            }
            case 8 : {
                message += "SR65"; break;
            }
            case 9 : {
                message += "SR65_AKF62"; break;
            }
            case 10 : {
                message += "SR65_AKF135"; break;
            }
            case 11 : {
                message += "SR65_AKF192"; break;
            }
            case 12 : {
                message += "SR65_AKF465"; break;
            }
            case 13 : {
                message += "SR65_TF_250"; break;
            }
            case 14 : {
                message += "SR65_VFG"; break;
            }
            case 15 : {
                message += "SR_MDS"; break;
            }
            case 16 : {
                message += "PTM200-1"; break;
            }
            case 17 : {
                message += "PTM200-2"; break;
            }
            default: {
                message += "SR04_"; break;
            }
        }
        String mes = getText(packet, messageStart + 4, messageStart + 36);
        if (mes.length() > 0){message += " BaseName_" + mes;}

        mes  = getText(packet, messageStart + 37, messageStart + 69);
        if (mes.length() > 0){message += " SensorName_" + mes;}
        return message;
    }

    public static String getDialOutFailed(Byte[] packet, int messageStart){
        String message = "Dial Out Failed";
        message += " Port_" + (packet[messageStart] & 0xFF);
        message += " Queue_" + (packet[messageStart + 1] & 0xFF);
        message += " Phone No_" + getText(packet, messageStart + 2, packet.length - 2);

        return message;
    }

    public static String getServiceAlarm(Byte[] packet, int messageStart){
        String message = "Net Fault ";
        if (packet[17] == 0) message += " Not servicing";
        if (packet[18] == 0) message += " Port setup";
        if (packet[19] == 0) message += " Setup Checksum";
        if (packet[20] == 0) message += " Local Globals";
        if (packet[21] == 0) message += " Wide In Globals";
        if (packet[22] == 0) message += " Wide Out Globals";
        if (packet[23] == 0) message += " Schedule Checksum";
        if (packet[24] == 0) message += " Daylight Saving";
        if (message.equals("Net Fault")) message = "Net OK";
        return message;
    }
}
