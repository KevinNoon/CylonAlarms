package com.optimised.cylonAlarms.services.alarmsToModemQueue;

import com.optimised.cylonAlarms.model.alarmsToIPQueue.AlarmIPQueue;
import com.optimised.cylonAlarms.model.alarmsToModemQueue.AlarmModemQueue;
import com.optimised.cylonAlarms.repository.alarmsToIPQueue.AlarmIPQueueRepo;
import com.optimised.cylonAlarms.repository.alarmsToModemQueue.AlarmModemQueueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmModemQueueService {
    @Autowired
    AlarmModemQueueRepo alarmQueueRepo;

    public void save(AlarmModemQueue alarmQueue){
        alarmQueueRepo.save(alarmQueue);
    }
    public List<AlarmModemQueue> list(){
       return (List<AlarmModemQueue>) alarmQueueRepo.findAll();
    }
    public void delete(AlarmModemQueue alarmQueue){
        alarmQueueRepo.delete(alarmQueue);
    }
}
