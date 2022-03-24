package com.optimised.cylonAlarms.services.alarmsToIPQueue;

import com.optimised.cylonAlarms.model.alarmsToIPQueue.queue.AlarmQueue;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import com.optimised.cylonAlarms.repository.alarmsToIPQueue.AlarmQueueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmQueueService {
    @Autowired
    AlarmQueueRepo alarmQueueRepo;

    public void save(AlarmQueue alarmQueue){
        alarmQueueRepo.save(alarmQueue);
    }
    public List<AlarmQueue> list(){
       return (List<AlarmQueue>) alarmQueueRepo.findAll();
    }
    public void delete(AlarmQueue alarmQueue){
        alarmQueueRepo.delete(alarmQueue);
    }
}
