package com.optimised.cylonAlarms.repository.alarmsToModemQueue;

import com.optimised.cylonAlarms.model.alarmsToModemQueue.AlarmModemQueue;
import org.springframework.data.repository.CrudRepository;

public interface AlarmModemQueueRepo extends CrudRepository<AlarmModemQueue,Long> {
    AlarmModemQueue findById(long id);
}


