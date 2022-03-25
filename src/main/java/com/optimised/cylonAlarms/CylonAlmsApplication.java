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
    private static int OFFSET_OB = 7;
    private static int OFFSET_CYL = 4;
    private static int OFFSET;
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
            System.out.println(alarmQueue[0] );
            if (alarmQueue[0] == 0x55) {
                OFFSET = OFFSET_OB;
            } else {
                OFFSET = OFFSET_CYL;
            }
            System.out.println(OFFSET);

            alarm.setSiteName(alarmQueueEntity.getSiteName());
            alarm.setSiteNumber(alarmQueueEntity.getSiteNumber());

//            System.out.println("Sub " + alarmQueueEntity.getSubmitted());
//            System.out.println("Num " + alarmQueueEntity.getSiteNumber());

            Integer siteNo = alarmQueueEntity.getSiteNumber();
            Integer dotNetNo =  (alarmQueue[OFFSET + 4] & 0xFF);
            if (dotNetNo == 0) dotNetNo = 1;
            alarm.setUCC4Number(dotNetNo);

            System.out.println("DotNet No " + dotNetNo);
            alarm.setUCC4Name(netService.getNet(dotNetNo,siteNo).getName());

            int alarmType = (alarmQueue[OFFSET + 6] & 0xFF);

            alarm.setAlarmType(alarmType);
            System.out.println("AlarmType =" + alarm.getAlarmType());

            Integer controllerNo = 0;
            String controllerName = "";
            if (alarmType < 3){
                controllerNo = (alarmQueue[OFFSET + 8] & 0xFF);
                controllerName = controllerService.getController(controllerNo,dotNetNo).getName();
            }
            if (alarmType == 9){
                 controllerNo = (alarmQueue[OFFSET + 12] & 0xFF);
                controllerName = controllerService.getController(controllerNo,dotNetNo).getName();
            }

            alarm.setUC16Name(controllerName);
            alarm.setUC16Number(controllerNo);

            String alarmStr = "";
            if (alarmType == 1 || alarmType == 9){
                Integer alarmStrNo = (alarmQueue[OFFSET + 32] & 0xFF);
                if (alarmStrNo > 0)
                alarmStr = alarmStrService.getAlarmStr(alarmStrNo,dotNetNo).getMessage();
            }

            switch (alarmType){
                case 1:{
                    Type01(alarm,alarmQueue);
                    if (alarm.getAlarmMessage().length() == 0){
                        alarm.setAlarmMessage(alarmStr);
                    }
                    break;
                }
                case 2:{
                    Type02(alarm,alarmQueue);
                    break;
                }
                case 3:{
                    Type03(alarm,alarmQueue);
                    break;
                }
                case 4:{
                    Type04(alarm,alarmQueue);
                    break;
                }
                case 5:{
                    Type05(alarm,alarmQueue);
                    break;
                }
                case 8:{
 //                   System.out.println("In Type 8");
                    Type08(alarm,alarmQueue);
 //                   System.out.println(alarm);
                    break;
                }
                case 9:{
 //                   System.out.println("In Type 9");
                    Type09(alarm,alarmQueue);
 //                   System.out.println(alarm);
                    break;
                }
                case 11:{
 //                   System.out.println("In Type 11");
                    Type11(alarm,alarmQueue);
 //                   System.out.println(alarm);
                    break;
                }
                case 12:{
 //                   System.out.println("In Type 12");
                    Type12(alarm,alarmQueue);
 //                   System.out.println(alarm);
                    break;
                }
                case 13:{
                    System.out.println("In Type 13");
                    Type13(alarm,alarmQueue);
                    System.out.println(alarm);
                    break;
                }
            }
        });
    }

    private static void Type01(Alarm alarm, Byte[] packet) {
        alarm.setPriority(packet[OFFSET + 7]);
        alarm.setStartedAt(calcTime(packet[OFFSET + 18], packet[OFFSET + 19], packet[OFFSET + 20], packet[OFFSET + 21]));
        alarm.setEndedAt(calcTime(packet[OFFSET + 22], packet[OFFSET + 23], packet[OFFSET + 24], packet[OFFSET + 25]));

        if (packet[OFFSET + 50] != 0) {
            alarm.setTriggerPointName(getText(packet, OFFSET + 35, OFFSET + 42));
            alarm.setTriggerPointNumber(packet[OFFSET + 50] & 0xff);
            alarm.setTriggerPointType(packet[OFFSET + 51] != 0);
            alarm.setTriggerPointUnit(packet[OFFSET + 52] + "");
            if ((packet[OFFSET + 50] & 0xFF) != 255) {
                alarm.setTriggerPointValue(getValue(packet[OFFSET + 53], packet[OFFSET + 54], packet[OFFSET + 55], packet[OFFSET + 56]));
            }
        }

        alarm.setAlarmNumber(packet[OFFSET + 13] & 0xff);
        alarm.setProgramModuleNumber(packet[OFFSET + 14] & 0xff);
        alarm.setStringNumber(packet[OFFSET + 65] & 0xFF);
        if (packet[OFFSET + 66] != 0)
            alarm.setAlarmMessage(getAlarmMessage(packet, OFFSET + 66));
        alarm.setExtraBits(packet[OFFSET + 15] & 0xFF);
        alarm.setExtraInteger(get2ByteInt(packet[OFFSET + 16], packet[OFFSET + 17]));
    }

    private static void Type02(Alarm alarm, Byte[] packet) {
        alarm.setPriority(packet[OFFSET + 12]);
        alarm.setProgramModuleNumber(255);
        alarm.setAlarmMessage("System Alarm,");
        alarm.setExtraBits(1);
        alarm.setStartedAt(calcTime(packet[OFFSET + 22], packet[OFFSET + 23], packet[OFFSET + 24], packet[OFFSET + 25]));

    }

    private static void Type03(Alarm alarm, Byte[] packet) {
        alarm.setPriority(1);
        alarm.setStartedAt(calcTime(packet[OFFSET + 8], packet[OFFSET + 9], packet[OFFSET + 10], packet[OFFSET + 11]));
        alarm.setAlarmMessage(getOffLineMessage(packet," Ctrl_", OFFSET + 12));
    }

    private static void Type04(Alarm alarm, Byte[] packet) {
        alarm.setPriority(1);
        alarm.setStartedAt(calcTime(packet[OFFSET + 8], packet[OFFSET + 9], packet[OFFSET + 10], packet[OFFSET + 11]));
        alarm.setAlarmMessage(DataConversions.getOffLineMessage(packet," Net_", OFFSET + 12));
    }

    private static void Type05(Alarm alarm, Byte[] packet) {
        alarm.setPriority(1);
        alarm.setStartedAt(calcTime(packet[OFFSET + 8], packet[OFFSET + 9], packet[OFFSET + 10], packet[OFFSET + 11]));
        alarm.setAlarmMessage(setServiceAlarm(packet,OFFSET + 12));
    }

    private static void Type08(Alarm alarm, Byte[] packet) {
        alarm.setPriority(1);
        alarm.setStartedAt(calcTime(packet[OFFSET + 8], packet[OFFSET + 9], packet[OFFSET + 10], packet[OFFSET + 11]));
        alarm.setAlarmMessage(setDialOutFailed(packet,OFFSET + 12));
    }

    private static void Type09(Alarm alarm, Byte[] packet) {


        alarm.setPriority(packet[OFFSET + 17]);
        alarm.setStartedAt(DataConversions.calcTime(packet[OFFSET + 21], packet[OFFSET + 22], packet[OFFSET + 23], packet[OFFSET + 24]));
        alarm.setEndedAt(DataConversions.calcTime(packet[OFFSET + 25], packet[OFFSET + 26], packet[OFFSET + 27], packet[OFFSET + 28]));

        if (packet[OFFSET + 84] != 0 && packet[OFFSET + 85] != 0) {
            alarm.setTriggerPointName(getText(packet,OFFSET + 44,OFFSET + 83));
            alarm.setTriggerPointNumber(get2ByteInt(packet[OFFSET + 84], packet[OFFSET + 85]));
            alarm.setTriggerPointType(packet[OFFSET + 86] != 0);
            alarm.setTriggerPointValue(getValue(packet[OFFSET + 88], packet[OFFSET + 89], packet[OFFSET + 90], packet[OFFSET + 91]));
            alarm.setTriggerPointUnit(packet[OFFSET + 87] + "");
        }

        alarm.setAlarmNumber(get2ByteInt(packet[OFFSET + 13], packet[OFFSET + 14]));
        alarm.setProgramModuleNumber(get2ByteInt(packet[OFFSET + 15], packet[OFFSET + 16]));
        if (packet[OFFSET + 95] != 0)
            alarm.setAlarmMessage(getAlarmMessage(packet, OFFSET + 97));
        alarm.setExtraBits(packet[OFFSET + 18]);
        if (alarm.getExtraBits() == 0){
            alarm.setStringNumber(get2ByteInt(packet[OFFSET + 39], packet[OFFSET + 40]));
        } else {
            alarm.setStringNumber(get2ByteInt(packet[OFFSET + 41], packet[OFFSET + 42]));
        }
        alarm.setExtraInteger(get2ByteInt(packet[OFFSET + 19], packet[OFFSET + 20]));
        //System.out.println(alarm);
    }


    private static void Type11(Alarm alarm, Byte[] packet) {
        alarm.setPriority(packet[OFFSET + 7]);
        alarm.setStartedAt(calcTime(packet[OFFSET + 8], packet[OFFSET + 9], packet[OFFSET + 10], packet[OFFSET + 11]));
        alarm.setAlarmMessage(setWirelessSensorAlarm(packet,OFFSET + 12));
    }

    private static void Type12(Alarm alarm, Byte[] packet) {
        alarm.setPriority(packet[OFFSET + 7]);
        alarm.setStartedAt(calcTime(packet[OFFSET + 8], packet[OFFSET + 9], packet[OFFSET + 10], packet[OFFSET + 11]));
        alarm.setAlarmMessage(getAlarmMessage(packet, OFFSET + 16));
    }

    private static void Type13(Alarm alarm,Byte[] packet) {
        alarm.setPriority(packet[OFFSET + 7]);
        alarm.setStartedAt(calcTime(packet[OFFSET + 8], packet[OFFSET + 9], packet[OFFSET + 10], packet[OFFSET + 11]));
        alarm.setAlarmMessage(ModbusMessage(packet, OFFSET + 12));
    }

    public static String setServiceAlarm(Byte[] packet, int messageStart){
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

    public static String setDialOutFailed(Byte[] packet, int messageStart){
        String message = "Dial Out Failed";
        message += " Port_" + (packet[messageStart] & 0xFF);
        message += " Queue_" + (packet[messageStart + 1] & 0xFF);
        message += " Phone No_" + getText(packet, messageStart + 2, packet.length - 2);

        return message;
    }

    public static String setWirelessSensorAlarm(Byte[] packet, int messageStart){
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

    private static String ModbusMessage(Byte[] packet, int messageStart) {
        String status = "Modbus device_";
        status += status + (packet[messageStart] & 0xFF);
        status += getAlarmMessage(packet,messageStart + 2);

        switch (packet[messageStart + 1]){
            case 0: {status += " offline"; break;}
            case 1: {status += " Online"; break;}
            case 2: {status += " offline disabled"; break;}
            default: status += " offline";
        }


//        String Status = "offline";
//        if (packet[messageStart] == 1) Status = "Online";
//        if (packet[messageStart] == 2) Status = "offline disabled";
//        Status = "External modbus device: Modbus " + (packet[16] & 0xFF) + " is " + Status;
        return status;
    }
}
