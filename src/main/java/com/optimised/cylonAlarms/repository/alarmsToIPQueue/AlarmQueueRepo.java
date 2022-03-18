package com.optimised.cylonAlarms.repository.alarmsToIPQueue;

import com.optimised.cylonAlarms.model.alarmsToIPQueue.queue.AlarmQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AlarmQueueRepo extends CrudRepository<AlarmQueue,Long> {
    AlarmQueue findById(long id);
}
