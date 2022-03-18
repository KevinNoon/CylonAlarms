package com.optimised.cylonAlarms.tools.common;

public class Constants {
    //    static final String CYLON_DIR = "C:\\UnitronUC32";
    //public static final String CYLON_DIR = "Y:\\UnitronUC32";
    public static final String WN3000 = "\\System\\WN3000.ini";

    public static final String GET_ALL_ALARM_QUEUE = "SELECT * FROM AlarmQueue order by Submitted, SiteNumber Asc";

    public static final String ALARM_TABLE =
            "SiteName,SiteNumber,UCC4Name,UCC4Number,UC16Name,Uc16Number," +
                    "Priority,StartedAt,EndedAt,AlarmType,TriggerPointName," +
                    "TriggerPointNumber,TriggerPointType,TriggerPointValue,TriggerPointUnit," +
                    "AlarmNumber,ProgramModuleNumber,AlarmMessage,UCC4SysStatus,Uc16SysAlarms," +
                    "StringNumber,ExtraBits,ExtraInteger,ExtraString";
}
