package com.optimised.cylonAlarms.repository.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.alarm.AlarmStr;
import com.optimised.cylonAlarms.model.iniFilesToDB.controller.Controller;
import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AlarmStrRepo extends CrudRepository<AlarmStr,Long> {

    AlarmStr findFirstByNumberAndNet(Integer number, Net site);

    @Transactional
    @Modifying
    @Query("UPDATE AlarmStr SET existing=false ")
    public int existingToFalse();

    @Transactional
    @Modifying
    @Query("DELETE FROM AlarmStr WHERE existing=false ")
    public void deleteByExistingFalse();

    @Query("SELECT a FROM AlarmStr a JOIN a.net n WHERE a.number = ?1 AND n.address = ?2")
    List<AlarmStr> getAlarmStr(int alarmStr, int net);
}
