package com.optimised.cylonAlarms.services.alarm;

import com.optimised.cylonAlarms.model.queueToAlarm.Alarm;
import com.optimised.cylonAlarms.repository.queueToAlarm.AlarmRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmService {
    @Autowired
    AlarmRepo alarmRepo;

    public void save(Alarm alarm){
        alarmRepo.save(alarm);
    }
    public List<Alarm> list(){
       return (List<Alarm>) alarmRepo.findAll();
    }
    public void delete(Alarm alarm){
        alarmRepo.delete(alarm);
    }
}
