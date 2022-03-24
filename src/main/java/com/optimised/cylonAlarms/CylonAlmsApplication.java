package com.optimised.cylonAlarms;

import com.optimised.cylonAlarms.model.alarmsToIPQueue.queue.AlarmQueue;
import com.optimised.cylonAlarms.model.queueToAlarm.Alarm;
import com.optimised.cylonAlarms.services.alarmsToIPQueue.AlarmQueueService;
import com.optimised.cylonAlarms.model.iniFilesToDB.alarm.AlarmStr;
import com.optimised.cylonAlarms.model.iniFilesToDB.controller.Controller;
import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import com.optimised.cylonAlarms.services.iniFilesToDB.AlarmStrService;
import com.optimised.cylonAlarms.services.iniFilesToDB.ControllerService;
import com.optimised.cylonAlarms.services.iniFilesToDB.NetService;
import com.optimised.cylonAlarms.services.iniFilesToDB.SiteService;
import com.optimised.cylonAlarms.tools.iniFilesToDB.Converions;
import com.optimised.cylonAlarms.tools.queueToAlarm.DataConversions;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Options;
import org.ini4j.Profile;
import org.ini4j.Wini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static com.optimised.cylonAlarms.tools.alarmsToIPQueue.GetAlarm.getRawAlarm;
import static com.optimised.cylonAlarms.tools.alarmsToIPQueue.GetAlarm.sendUpdateQueue;
import static com.optimised.cylonAlarms.tools.iniFilesToDB.HexString.*;
import static com.optimised.cylonAlarms.tools.queueToAlarm.DataConversions.*;

@Component
@EnableScheduling
@SpringBootApplication
public class CylonAlmsApplication {
    private static String unitronPath;
    private static String wn3000IniPath;
    private static String sitePath;

    public static void main(String[] args) {
        SpringApplication.run(CylonAlmsApplication.class, args);
    }

    @Autowired
    SiteService siteService;
    @Autowired
    NetService netService;
    @Autowired
    ControllerService controllerService;
    @Autowired
    AlarmStrService alarmStrService;
    @Autowired
    AlarmQueueService alarmQueueService;

   // @Scheduled(initialDelay = 1000, fixedRate = 2000000)
    public void GetWn3000ini() {
        setPaths();
        setExisting();
        try {
            Wini ini = new Wini(new File(wn3000IniPath));
            int noOfSites = ini.get("SiteList", "TotalSites", int.class);
            for (int n = 1; n < noOfSites + 1; n++) {
                Integer siteNo = Converions.tryParseInt(ini.get("SiteList", "Site" + n));
                String siteSection = "Site" + siteNo;

                Site wn3000ini = new Site();

                wn3000ini.setSiteNumber(siteNo);
                wn3000ini.setAlarmScan(Converions.tryParseInt(ini.get(siteSection, "AlarmScan")));
                wn3000ini.setDirectory(ini.get(siteSection, "Directory"));
                wn3000ini.setInternet(Converions.tryParseInt(ini.get(siteSection, "Internet")));
                wn3000ini.setIDCode(ini.get(siteSection, "IDCode"));
                wn3000ini.setIPAddr(ini.get(siteSection, "IPAddr"));
                wn3000ini.setName(ini.get(siteSection, "Name"));
                wn3000ini.setNetwork(Converions.tryParseInt(ini.get(siteSection, "Network")));
                wn3000ini.setPort(Converions.tryParseInt(ini.get(siteSection, "Port")));
                wn3000ini.setRemote(Converions.tryParseInt(ini.get(siteSection, "Remote")));
                wn3000ini.setTelephone(ini.get(siteSection, "Telephone"));
                wn3000ini.setExisting(true);

                siteService.AddUpdate(wn3000ini);

                //Get a list of the network controllers sections
                String siteDir = ini.get(siteSection, "Directory");
                Wini siteIni = new Wini(new File(unitronPath + siteDir + sitePath));
                Set<String> keys = siteIni.keySet();
                List<String> os = keys.stream().filter(x -> x.contains("OS")).collect(Collectors.toList());

                os.forEach(outstation -> {

                    int osNo = Integer.parseInt(outstation.substring(2));
                    Profile.Section sec = siteIni.get(outstation);
                    Net netEntity = new Net(null, wn3000ini, sec.get("Name"), osNo,true);
                    netService.addUpdate(netEntity);

                    try {
                        File myObj = new File(unitronPath + siteDir + "\\DBase\\" + osNo + "\\AlarmStr.txt");
                        if (myObj.exists()) {
                            String[] almStr;
                            Scanner myReader = new Scanner(myObj);
                            while (myReader.hasNextLine()) {
                                String data = myReader.nextLine();
                                almStr = data.split("\t");
                                if (almStr.length == 2) {
                                    AlarmStr alarmStrEntity = new
                                            AlarmStr(null, netEntity, Integer.parseInt(almStr[0]), almStr[1],true);
                                    alarmStrService.addUpdate(alarmStrEntity);
                                }
                            }
                            myReader.close();
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }

                    sec.forEach((y, x) -> {
                        int address;
                        if (y.contains("UC16_")) {
                            if (y.contains("UC16_B")) {
                                address = Integer.parseInt(y.substring(20));
                            } else {
                                address = Integer.parseInt(y.substring(5));
                            }
                            Controller controllerEntity = new Controller(null, netEntity, x, address,true);
                            controllerService.addUpdate(controllerEntity);
                        }
                    });
                });
            }

        } catch (InvalidFileFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Removing deleted sites");
        deleteNoExisting();
        System.out.println("Finished INI Update");
    }

    private static void setPaths() {
        Options opt = new Options();
        try {
            opt.load(new File("main.opt"));
            if (!opt.containsKey("unitronPath")) {
                opt.add("unitronPath", "C:\\UnitronUC32\\");
                opt.add("wn3000IniPath", "system\\WN3000.ini");
                opt.add("sitePath", "\\System\\site.ini");
            }
            unitronPath = opt.get("unitronPath");
            wn3000IniPath = unitronPath + opt.get("wn3000IniPath");
            sitePath = opt.get("sitePath");
            try {
                opt.store(new File("main.opt"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  void setExisting(){
        siteService.setExisting();
        netService.setExisting();
        controllerService.setExisting();
        alarmStrService.setExisting();
    }

    private void deleteNoExisting(){
        alarmStrService.deleteNoExisting();
        controllerService.deleteNoExisting();
        netService.deleteNoExisting();
        siteService.deleteNoExisting();

    }

   // @Scheduled(initialDelay = 3000, fixedRate =  50000)
    public void updateAlarmQueue() {
        //Set the path required from the external file
        setPaths();

        //Get a list of sites from the database
        List<Site> siteList = siteService.list();

        //Get the alarms from all the sites.
        siteList.forEach(siteEntity -> {
            //Check that the sites are IP and set to be scanned
            System.out.println(siteEntity.getName());
            if ((siteEntity.getInternet() == 1) && (siteEntity.getAlarmScan() == 1)){

                //set the port number of the controller
                int portNo = siteEntity.getPort();
                // If port in database is zero the set it to Cylon default
                if (portNo == 0) portNo = 4950;

                //Set a default last time (For newly scanned sites)
                char[] lastTime = {1, 0, 0, 0, 0};
                //Get the last alarm time from the database.
                String timeStr = siteEntity.getLastAlarmTime();
               //If the database has a time then set last time to it.
                if (!(timeStr == null)) {
                    lastTime = HexToCharArray(timeStr,0);
                }

                //Cycle through the alarms until they are all read.
                do{
                    char alarmBufferNo = lastTime[4];

                    //Update the lastTime from the alarm time from the packet
                    Byte[] rawAlarm = getRawAlarm(siteEntity.getIPAddr(),lastTime,portNo);
                    lastTime = GetAlarmTime(rawAlarm);

                    if (alarmBufferNo != lastTime[4] && lastTime[4] != 0) {

                        AlarmQueue alarmQueue = new AlarmQueue();
                        alarmQueue.setSiteName(siteEntity.getName());
                        alarmQueue.setSiteNumber(siteEntity.getSiteNumber());
                        alarmQueue.setAcknowledged(0);
                        alarmQueue.setSubmitted(Timestamp.valueOf(LocalDateTime.now()));
                        alarmQueue.setAlarmPacket(rawAlarm);

                        alarmQueueService.save(alarmQueue);
                        siteEntity.setLastAlarmTime(CharArrayToHexStr(lastTime));
                        siteService.AddUpdate(siteEntity);
                    }
                    sendUpdateQueue(siteEntity.getIPAddr(),portNo);
               } while (lastTime[0] != 0);

            }
        });
    }

     @Scheduled(initialDelay = 1000, fixedRate = 2000000)
    public void queueToAlarm(){
        List<AlarmQueue> alarmQueueList = alarmQueueService.list();
        alarmQueueList.forEach(alarmQueueEntity -> {
            Alarm alarm = new Alarm();

            Byte[] alarmQueue = alarmQueueEntity.getAlarmPacket();//HexToIntArray(alarmQueueEntity.getAlarmPacket(),2);
            alarm.setSiteName(alarmQueueEntity.getSiteName());
            alarm.setSiteNumber(alarmQueueEntity.getSiteNumber());

            Integer siteNo = alarmQueueEntity.getSiteNumber();
            Integer dotNetNo =  (alarmQueue[11] & 0xFF);
            if (dotNetNo == 0) dotNetNo = 1;
            alarm.setUCC4Number(dotNetNo);
            alarm.setUCC4Name(netService.getNet(dotNetNo,siteNo).getName());

            int alarmType = (alarmQueue[13] & 0xFF);

            alarm.setAlarmType(alarmType);

            Integer controllerNo = 0;
            String controllerName = "";
            if (alarmType < 3){
                controllerNo = (alarmQueue[15] & 0xFF);
                controllerName = controllerService.getController(controllerNo,dotNetNo).getName();
            }
            if (alarmType == 9){
                 controllerNo = (alarmQueue[19] & 0xFF);
                controllerName = controllerService.getController(controllerNo,dotNetNo).getName();
            }

            alarm.setUC16Name(controllerName);
            alarm.setUC16Number(controllerNo);

            String alarmStr = "";
            if (alarmType == 1 || alarmType == 9){
                Integer alarmStrNo = (alarmQueue[39] & 0xFF);
                if (alarmStrNo > 0)
                alarmStr = alarmStrService.getAlarmStr(alarmStrNo,dotNetNo).getMessage();
            }

            switch (alarmType){
                case 1:{
                    Type01(alarm,alarmQueue);
                    if (alarm.getAlarmMessage().length() == 0){
                        alarm.setAlarmMessage(alarmStr);
                    }
                    System.out.println(alarmQueueEntity.getSubmitted());
                    System.out.println(alarm);
                    break;
                }
//                case 2:{
//                    Type02(alarm,alarmQueue);
//                    System.out.println(alarm);
//                    break;
//                }
//                case 3:{
//                    Type03(alarm,alarmQueue);
//                    System.out.println(alarm);
//                    break;
//                }
//                case 4:{
//                    Type04(alarm,alarmQueue);
//                    System.out.println(alarm);
//                    break;
//                }
//                case 5:{
//                    Type05(alarm,alarmQueue);
//                    System.out.println(alarm);
//                    break;
//                }
//                case 8:{
//                    Type08(alarm,alarmQueue);
//                    System.out.println(alarm);
//                    break;
//                }
            }

        });
    }

    private static void Type01(Alarm alarm, Byte[] packet) {
        System.out.println("In alarm 01");
        alarm.setPriority(packet[14]);
        System.out.println(alarm.getPriority());
        alarm.setStartedAt(DataConversions.calcTime(packet[25] & 0xFF, packet[26] & 0xFF, packet[27] & 0xFF, packet[28] & 0xFF));
        System.out.println(packet[26] & 0xFF);
        alarm.setEndedAt(DataConversions.calcTime(packet[29], packet[30], packet[31], packet[32]));

        if (packet[57] != 0) {
            alarm.setTriggerPointName(getText(packet, 42, 49));
            alarm.setTriggerPointNumber(packet[57] & 0xff);
            alarm.setTriggerPointType(packet[58] != 0);
            alarm.setTriggerPointUnit(packet[59] + "");
            if ((packet[57] & 0xFF) != 255) {
                alarm.setTriggerPointValue(getValue(packet[60], packet[61], packet[62], packet[63]));
            }
        }

        alarm.setAlarmNumber(packet[20] & 0xff);
        alarm.setProgramModuleNumber(packet[21] & 0xff);
        alarm.setStringNumber(packet[72] & 0xFF);
        if (packet[73] != 0)
            alarm.setAlarmMessage(DataConversions.getAlarmMessage(packet, 73));
        alarm.setExtraBits(packet[22] & 0xFF);
        alarm.setExtraInteger(get2ByteInt(packet[23], packet[23]));
    }
//
//    private static void Type02(Alarm alarm, byte[] packet) {
//        alarm.setPriority(packet[17]);
//        alarm.setProgramModuleNumber(255);
//        alarm.setAlarmMessage("System Alarm,");
//        alarm.setExtraBits(1);
//        alarm.setStartedAt(DataConversions.calcTime(packet[27], packet[28], packet[29], packet[30]));
//
//    }
//
//    private static void Type03(Alarm alarm, byte[] packet) {
//        alarm.setPriority(1);
//        alarm.setStartedAt(DataConversions.calcTime(packet[13], packet[14], packet[15], packet[16]));
//        alarm.setAlarmMessage(DataConversions.getOffLineMessage(packet," Ctrl_"));
//    }
//
//    private static void Type04(Alarm alarm, byte[] packet) {
//        alarm.setPriority(1);
//        alarm.setStartedAt(DataConversions.calcTime(packet[13], packet[14], packet[15], packet[16]));
//        alarm.setAlarmMessage(DataConversions.getOffLineMessage(packet," Net_"));
//    }
//
//    private static void Type05(Alarm alarm, byte[] packet) {
//        alarm.setPriority(1);
//        alarm.setStartedAt(DataConversions.calcTime(packet[13], packet[14], packet[15], packet[16]));
//        alarm.setAlarmMessage(setServiceAlarm(packet));
//    }
//
//    private static void Type08(Alarm alarm, byte[] packet) {
//        alarm.setPriority(1);
//        alarm.setStartedAt(DataConversions.calcTime(packet[13], packet[14], packet[15], packet[16]));
//        alarm.setAlarmMessage(setDialOutFailed(packet));
//    }
//
//    private static void Type09(Alarm alarm, byte[] packet) {
//
//
//        alarm.setPriority(packet[22]);
//        alarm.setStartedAt(DataConversions.calcTime(packet[26], packet[27], packet[28], packet[29]));
//        alarm.setEndedAt(DataConversions.calcTime(packet[30], packet[31], packet[32], packet[33]));
//
//        if (packet[89] != 0 && packet[90] != 0) {
//            alarm.setTriggerPointName(getText(packet,49,89));
//            alarm.setTriggerPointNumber(get2ByteInt(packet[89], packet[90]));
//            alarm.setTriggerPointType(packet[90] != 0);
//            alarm.setTriggerPointValue(getValue(packet[93], packet[94], packet[95], packet[96]));
//            alarm.setTriggerPointUnit(packet[93] + "");
//        }
//
//        alarm.setAlarmNumber(get2ByteInt(packet[18], packet[19]));
//        alarm.setProgramModuleNumber(get2ByteInt(packet[20], packet[21]));
//        if (packet[100] != 0)
//            alarm.setAlarmMessage(getAlarmMessage(packet, 102));
//        alarm.setExtraBits(packet[23]);
//        if (alarm.getExtraBits() == 0){
//            alarm.setStringNumber(get2ByteInt(packet[44], packet[45]));
//        } else {
//            alarm.setStringNumber(get2ByteInt(packet[46], packet[47]));
//        }
//        alarm.setExtraInteger(get2ByteInt(packet[24], packet[25]));
//        //System.out.println(alarm);
//    }
//
//
//    public static String setServiceAlarm(byte[] packet){
//        String message = "Net Fault ";
//        if (packet[17] == 0) message += " Not servicing";
//        if (packet[18] == 0) message += " Port setup";
//        if (packet[19] == 0) message += " Setup Checksum";
//        if (packet[20] == 0) message += " Local Globals";
//        if (packet[21] == 0) message += " Wide In Globals";
//        if (packet[22] == 0) message += " Wide Out Globals";
//        if (packet[23] == 0) message += " Schedule Checksum";
//        if (packet[24] == 0) message += " Daylight Saving";
//        if (message.equals("Net Fault")) message = "Net OK";
//        return message;
//    }
//
//    public static String setDialOutFailed(byte[] packet){
//        String message = "Dial Out Failed";
//        message += " Port_" + (packet[17] & 0xFF);
//        message += " Queue_" + (packet[18] & 0xFF);
//        message += " Phone No_" + getText(packet, 19, packet.length - 2);
//
//        return message;
//    }
}
