package com.optimised.cylonAlarms.services.alarmsToIPQueue;

import com.optimised.cylonAlarms.model.alarmsToIPQueue.queue.AlarmQueue;
import com.optimised.cylonAlarms.repository.alarmsToIPQueue.AlarmQueueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmQueueService {
    @Autowired
    AlarmQueueRepo alarmQueueRepo;

    public void save(AlarmQueue alarmQueue){
        alarmQueueRepo.save(alarmQueue);
    }
}
