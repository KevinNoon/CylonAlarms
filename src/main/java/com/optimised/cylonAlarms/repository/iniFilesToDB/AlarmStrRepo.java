package com.optimised.cylonAlarms.repository.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.alarm.AlarmStr;
import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AlarmStrRepo extends CrudRepository<AlarmStr,Long> {

    AlarmStr findFirstByNumberAndNet(Integer number, Net site);
}
