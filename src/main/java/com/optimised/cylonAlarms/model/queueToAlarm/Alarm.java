package com.optimised.cylonAlarms.model.queueToAlarm;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "Alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
 //   private Integer id;
 //   private Timestamp Summited;
    private Integer alarmID ;
    private int ConnectedAlarmID;
    private String SiteName;
    private int SiteNumber;
    private String UCC4Name;
    private int UCC4Number;
    private String UC16Name;
    private int UC16Number;
    private int Priority;
    private Timestamp StartedAt;
    private Timestamp EndedAt;
    private int AlarmType;
    private String TriggerPointName;
    private int TriggerPointNumber;
    private boolean TriggerPointType;
    private Float TriggerPointValue;
    private String TriggerPointUnit;
    private int AlarmNumber;
    private int ProgramModuleNumber;
    private String AlarmMessage;
    private int UCC4SysStatus;
    private int UC16SysAlarms;
    private String Note;
    private String AcknowledgedBy;
    private String AcknowledgedAt;
    private boolean Suppressed;
    private int StringNumber;
    private int ExtraBits;
    private int ExtraInteger;
    private String ExtraString;


}
