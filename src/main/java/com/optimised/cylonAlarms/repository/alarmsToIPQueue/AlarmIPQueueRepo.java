package com.optimised.cylonAlarms.repository.alarmsToIPQueue;

import com.optimised.cylonAlarms.model.alarmsToIPQueue.AlarmIPQueue;
import org.springframework.data.repository.CrudRepository;

public interface AlarmIPQueueRepo extends CrudRepository<AlarmIPQueue,Long> {
    AlarmIPQueue findById(long id);
}
