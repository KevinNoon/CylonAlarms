package com.optimised.cylonAlarms.repository.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.controller.Controller;
import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ControllerRepo extends CrudRepository<Controller,Long> {

    Controller findFirstByNameAndNet(String name, Net net);

    @Transactional
    @Modifying
    @Query("UPDATE Controller SET existing=false ")
     int existingToFalse();

    @Transactional
    @Modifying
    @Query("DELETE FROM Controller WHERE existing=false ")
     void deleteByExistingFalse();

    @Query("SELECT c FROM Controller c JOIN c.net n WHERE c.address = ?1 AND n.address = ?2")
    List<Controller> getNetName(int controller, int net);
}
