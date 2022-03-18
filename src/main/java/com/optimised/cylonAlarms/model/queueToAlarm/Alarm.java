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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Timestamp Summited;
    private int AlarmID ;
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

    public Timestamp getSummited() {
        return Summited;
    }

    public void setSummited(Timestamp summited) {
        Summited = summited;
    }

    public int getAlarmID() {
        return AlarmID;
    }

    public void setAlarmID(int alarmID) {
        AlarmID = alarmID;
    }

    public int getConnectedAlarmID() {
        return ConnectedAlarmID;
    }

    public void setConnectedAlarmID(int connectedAlarmID) {
        ConnectedAlarmID = connectedAlarmID;
    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public int getSiteNumber() {
        return SiteNumber;
    }

    public void setSiteNumber(int siteNumber) {
        SiteNumber = siteNumber;
    }

    public String getUCC4Name() {
        return UCC4Name;
    }

    public void setUCC4Name(String UCC4Name) {
        this.UCC4Name = UCC4Name;
    }

    public int getUCC4Number() {
        return UCC4Number;
    }

    public void setUCC4Number(int UCC4Number) {
        this.UCC4Number = UCC4Number;
    }

    public String getUC16Name() {
        return UC16Name;
    }

    public void setUC16Name(String UC16Name) {
        this.UC16Name = UC16Name;
    }

    public int getUC16Number() {
        return UC16Number;
    }

    public void setUC16Number(int UC16Number) {
        this.UC16Number = UC16Number;
    }

    public int getPriority() {
        return Priority;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }

    public Timestamp getStartedAt() {
        return StartedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        StartedAt = startedAt;
    }

    public Timestamp getEndedAt() {
        return EndedAt;
    }

    public void setEndedAt(Timestamp endedAt) {
        EndedAt = endedAt;
    }

    public int getAlarmType() {
        return AlarmType;
    }

    public void setAlarmType(int alarmType) {
        AlarmType = alarmType;
    }

    public String getTriggerPointName() {
        return TriggerPointName;
    }

    public void setTriggerPointName(String triggerPointName) {
        TriggerPointName = triggerPointName;
    }

    public int getTriggerPointNumber() {
        return TriggerPointNumber;
    }

    public void setTriggerPointNumber(int triggerPointNumber) {
        TriggerPointNumber = triggerPointNumber;
    }

    public boolean isTriggerPointType() {
        return TriggerPointType;
    }

    public void setTriggerPointType(boolean triggerPointType) {
        TriggerPointType = triggerPointType;
    }

    public Float getTriggerPointValue() {
        return TriggerPointValue;
    }

    public void setTriggerPointValue(Float triggerPointValue) {
        TriggerPointValue = triggerPointValue;
    }

    public String getTriggerPointUnit() {
        return TriggerPointUnit;
    }

    public void setTriggerPointUnit(String triggerPointUnit) {
        TriggerPointUnit = triggerPointUnit;
    }

    public int getAlarmNumber() {
        return AlarmNumber;
    }

    public void setAlarmNumber(int alarmNumber) {
        AlarmNumber = alarmNumber;
    }

    public int getProgramModuleNumber() {
        return ProgramModuleNumber;
    }

    public void setProgramModuleNumber(int programModuleNumber) {
        ProgramModuleNumber = programModuleNumber;
    }

    public String getAlarmMessage() {
        return AlarmMessage;
    }

    public void setAlarmMessage(String alarmMessage) {
        AlarmMessage = alarmMessage;
    }

    public int getUCC4SysStatus() {
        return UCC4SysStatus;
    }

    public void setUCC4SysStatus(int UCC4SysStatus) {
        this.UCC4SysStatus = UCC4SysStatus;
    }

    public int getUC16SysAlarms() {
        return UC16SysAlarms;
    }

    public void setUC16SysAlarms(int UC16SysAlarms) {
        this.UC16SysAlarms = UC16SysAlarms;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getAcknowledgedBy() {
        return AcknowledgedBy;
    }

    public void setAcknowledgedBy(String acknowledgedBy) {
        AcknowledgedBy = acknowledgedBy;
    }

    public String getAcknowledgedAt() {
        return AcknowledgedAt;
    }

    public void setAcknowledgedAt(String acknowledgedAt) {
        AcknowledgedAt = acknowledgedAt;
    }

    public boolean isSuppressed() {
        return Suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        Suppressed = suppressed;
    }

    public int getStringNumber() {
        return StringNumber;
    }

    public void setStringNumber(int stringNumber) {
        StringNumber = stringNumber;
    }

    public int getExtraBits() {
        return ExtraBits;
    }

    public void setExtraBits(int extraBits) {
        ExtraBits = extraBits;
    }

    public int getExtraInteger() {
        return ExtraInteger;
    }

    public void setExtraInteger(int extraInteger) {
        ExtraInteger = extraInteger;
    }

    public String getExtraString() {
        return ExtraString;
    }

    public void setExtraString(String extraString) {
        ExtraString = extraString;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "AlarmID=" + AlarmID +
                ", ConnectedAlarmID=" + ConnectedAlarmID +
                ", SiteName='" + SiteName + '\'' +
                ", SiteNumber=" + SiteNumber +
                ", UCC4Name='" + UCC4Name + '\'' +
                ", UCC4Number=" + UCC4Number +
                ", UC16Name='" + UC16Name + '\'' +
                ", UC16Number=" + UC16Number +
                ", Priority=" + Priority +
                ", StartedAt=" + StartedAt +
                ", EndedAt=" + EndedAt +
                ", AlarmType=" + AlarmType +
                ", TriggerPointName='" + TriggerPointName + '\'' +
                ", TriggerPointNumber=" + TriggerPointNumber +
                ", TriggerPointType=" + TriggerPointType +
                ", TriggerPointValue=" + TriggerPointValue +
                ", TriggerPointUnit='" + TriggerPointUnit + '\'' +
                ", AlarmNumber=" + AlarmNumber +
                ", ProgramModuleNumber=" + ProgramModuleNumber +
                ", AlarmMessage='" + AlarmMessage + '\'' +
                ", UCC4SysStatus=" + UCC4SysStatus +
                ", UC16SysAlarms=" + UC16SysAlarms +
                ", Note='" + Note + '\'' +
                ", AcknowledgedBy='" + AcknowledgedBy + '\'' +
                ", AcknowledgedAt='" + AcknowledgedAt + '\'' +
                ", Suppressed=" + Suppressed +
                ", StringNumber=" + StringNumber +
                ", ExtraBits=" + ExtraBits +
                ", ExtraInteger=" + ExtraInteger +
                ", ExtraString='" + ExtraString + '\'' +
                '}';
    }
}
