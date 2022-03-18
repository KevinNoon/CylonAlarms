package com.optimised.cylonAlarms;

import com.optimised.cylonAlarms.model.alarmsToIPQueue.queue.AlarmQueue;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static com.optimised.cylonAlarms.tools.alarmsToIPQueue.GetAlarm.getRawAlarm;
import static com.optimised.cylonAlarms.tools.alarmsToIPQueue.GetAlarm.sendUpdateQueue;
import static com.optimised.cylonAlarms.tools.iniFilesToDB.HexString.*;

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

//    @Scheduled(initialDelay = 1000, fixedRate = 2000000)
    public void GetWn3000ini() {
        setPaths();
        setExisting(false);
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
            System.out.println("Removing deleted sites");
            deleteNoExisting();
            System.out.println("Finished INI Update");
        } catch (InvalidFileFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private  void setExisting(Boolean state){
        List<Site> siteList = siteService.list();
        siteList.forEach(siteEntity -> {
            siteEntity.setExisting(state);
            siteService.AddUpdate(siteEntity);
        });
        List<Net> netList = netService.list();
        netList.forEach(netEntity -> {
            netEntity.setExisting(state);
            netService.addUpdate(netEntity);
        });
        List<Controller> controllerList = controllerService.list();
        controllerList.forEach(controllerEntity -> {
            controllerEntity.setExisting(state);
            controllerService.addUpdate(controllerEntity);
        });
        List<AlarmStr> alarmStrList = alarmStrService.list();
        alarmStrList.forEach(alarmStrEntity -> {
            alarmStrEntity.setExisting(state);
            alarmStrService.addUpdate(alarmStrEntity);
        });
    }

    private void deleteNoExisting(){
        List<AlarmStr> alarmStrList = alarmStrService.list();
        alarmStrList.forEach(alarmStrEntity -> {
            if(!alarmStrEntity.getExisting()) {
                alarmStrService.delete(alarmStrEntity);
            }
        });
        List<Controller> controllerList = controllerService.list();
        controllerList.forEach(controllerEntity -> {
            if(!controllerEntity.getExisting()) {
                controllerService.delete(controllerEntity);
            }
        });
        List<Net> netList = netService.list();
        netList.forEach(netEntity -> {
            if(!netEntity.getExisting()) {
                netService.delete(netEntity);
            }
        });
        List<Site> siteList = siteService.list();
        siteList.forEach(siteEntity -> {
            if(!siteEntity.getExisting()) {
                siteService.delete(siteEntity);
            }
        });
    }

    @Scheduled(initialDelay = 3000, fixedRate =  50000)
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
                    lastTime = HexToCharArray(timeStr);
                }

                //Cycle through the alarms until they are all read.
                do{
                    char alarmBufferNo = lastTime[4];

                    //Update the lastTime from the alarm time from the packet
                    byte[] rawAlarm = getRawAlarm(siteEntity.getIPAddr(),lastTime,portNo);
                    lastTime = GetAlarmTime(rawAlarm);

                    if (alarmBufferNo != lastTime[4] && lastTime[4] != 0) {

                        AlarmQueue alarmQueue = new AlarmQueue();
                        alarmQueue.setSiteName(siteEntity.getName());
                        alarmQueue.setSiteNumber(siteEntity.getSiteNumber());
                        alarmQueue.setAcknowledged(0);
                        alarmQueue.setSubmitted(Timestamp.valueOf(LocalDateTime.now()));
                        alarmQueue.setAlarmPacket(ByteArrayToString(rawAlarm,3));

                        alarmQueueService.save(alarmQueue);
                        siteEntity.setLastAlarmTime(CharArrayToHexStr(lastTime));
                        siteService.AddUpdate(siteEntity);
                    }
                    sendUpdateQueue(siteEntity.getIPAddr(),portNo);
               } while (lastTime[0] != 0);

            }
        });
    }
}
