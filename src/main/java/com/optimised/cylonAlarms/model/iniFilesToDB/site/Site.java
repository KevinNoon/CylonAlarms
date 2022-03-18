package com.optimised.cylonAlarms.model.iniFilesToDB.site;

import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Setter
@Getter
@ToString
@Table (name = "Site", uniqueConstraints = @UniqueConstraint(columnNames = {"Directory","Name"}))

public class Site implements Serializable {

    public Site(Integer alarmScan){
        this.alarmScan = alarmScan;
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "site_Number")
    private Integer siteNumber;
    @Column(name = "wn3000ini_Id")
    private Integer wn3000iniId;
    @Column(name = "alarm_scan")
    private Integer alarmScan;
    private String directory;
    private String iDCode;
    private Integer internet;
    private String iPAddr;
    private String name;
    private Integer network;
    private Integer port;
    private Integer remote;
    private String telephone;
    @Column(name = "last_Alarm_Time")
    private String lastAlarmTime;
    private Boolean existing;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "id", cascade = CascadeType.ALL)
    private Set<Net> netEntities = new HashSet<>();

}
