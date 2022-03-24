package com.optimised.cylonAlarms.tools.alarmsToIPQueue;

import org.yaml.snakeyaml.util.ArrayUtils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static com.optimised.cylonAlarms.tools.iniFilesToDB.Checksums.calCheckSum;

public class GetAlarm {

    public static Byte[] getRawAlarm(String hostIP, char[] lastTimeInfo, int port) {
        //Header
        char device = 'P';
        char siteNoByte1 = 0; //Set to 0 for getting alarms
        char siteNoByte2 = 0; //Set to 0 for getting alarms
        char netAddress = 0; //Use net set by Site configuration
        char packetSize = 15; //Length of alarm packet
        char subNetAddress = 0; //Set to 0 when getting alarms
        char getCommand = 'G'; //Code to get info from a site

        //Payload
        char requestCode = 208; //Code to get an alarm
        char alarmQueueNo = 1; //Set to default can be set 1,2 or 3
        char t1 = lastTimeInfo[0]; //Time of last alarm Byte 1
        char t2 = lastTimeInfo[1]; //Time of last alarm Byte 2
        char t3 = lastTimeInfo[2]; //Time of last alarm Byte 3
        char t4 = lastTimeInfo[3]; //Time of last alarm Byte 4
        char alarmQueueInfo = lastTimeInfo[4]; //Alarm queue info

        int bytesRead = 0;

        //Create the packet to send to Net controller
        char[] packet = {device, siteNoByte1, siteNoByte2, netAddress, packetSize, subNetAddress,
                getCommand, requestCode, alarmQueueNo
                , t1, t2, t3, t4, alarmQueueInfo, (char) 0};

        //Calulate the check sum
        packet[14] = (char) (calCheckSum(packet) & 0xFF);

        byte[] rawAlarm = new byte[0];
        try (Socket socket = new Socket(hostIP, port)) {

            //Create a stream to send message to controller
            OutputStream output = socket.getOutputStream();

            //Send the message to the controller
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.ISO_8859_1), true);
            writer.print(packet);
            //Need to flush the buffer
            writer.flush();

            //Create a stream to accept the message from the controller
            InputStream input = socket.getInputStream();

            //The message length is unknown therefore create an array long enough to accept any length
            byte[] rawAlarmPacket = new byte[4080];

            //Get the number of bytes read
            bytesRead = input.read( rawAlarmPacket, 0, rawAlarmPacket.length);
            //Create an array the same length as the packet from the controller
            rawAlarm = new byte[bytesRead];
            //Copy the message the array to be returned by the function
            System.arraycopy(rawAlarmPacket,0,rawAlarm,0,bytesRead);

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }

        Byte[] rawAlarmByte = new Byte[rawAlarm.length];
        for (int i = 0; i < rawAlarm.length; i++) {
            rawAlarmByte[i] = rawAlarm[i];
        }
        return rawAlarmByte;
    }

    public static void sendUpdateQueue(String hostIP, int port) {
        //Header
        char device = 'P';
        char siteNoByte1 = 0; //Set to 0 for getting alarms
        char siteNoByte2 = 0; //Set to 0 for getting alarms
        char netAddress = 0; //Use net set by Site configuration
        char packetSize = 9; //Length of alarm packet
        char subNetAddress = 0; //Set to 0 when getting alarms
        char changeCommand = 'C'; //Code to change info from a site
        //Payload
        char updateQueue = 233; //Code to update Queue

        //Create the packet to send to Net controller
        char[] packet = {device, siteNoByte1, siteNoByte2, netAddress, packetSize, subNetAddress,
                changeCommand, updateQueue, (char) 0};

        //Calulate the check sum
        packet[8] = (char) (calCheckSum(packet) & 0xFF);
        try (Socket socket = new Socket(hostIP, port)) {

            //Create a stream to send message to controller
            OutputStream output = socket.getOutputStream();

            //Send the message to the controller
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.ISO_8859_1), true);
            writer.print(packet);
            //Need to flush the buffer
            writer.flush();
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }

}
