package com.optimised.cylonAlarms.repository.queueToAlarm;

import com.optimised.cylonAlarms.model.queueToAlarm.Alarm;
import org.springframework.data.repository.CrudRepository;

public interface AlarmRepo extends CrudRepository<Alarm,Long> {
    Alarm findById(long id);
}
