package com.optimised.cylonAlarms.services.alarmsToIPQueue;

import com.optimised.cylonAlarms.model.alarmsToIPQueue.AlarmIPQueue;
import com.optimised.cylonAlarms.repository.alarmsToIPQueue.AlarmIPQueueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmIPQueueService {
    @Autowired
    AlarmIPQueueRepo alarmQueueRepo;

    public void save(AlarmIPQueue alarmQueue){
        alarmQueueRepo.save(alarmQueue);
    }
    public List<AlarmIPQueue> list(){
       return (List<AlarmIPQueue>) alarmQueueRepo.findAll();
    }
    public void delete(AlarmIPQueue alarmQueue){
        alarmQueueRepo.delete(alarmQueue);
    }
}
