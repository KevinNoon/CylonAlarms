package com.optimised.cylonAlarms.repository.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.controller.Controller;
import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ControllerRepo extends CrudRepository<Controller,Long> {

    Controller findFirstByNameAndNet(String name, Net net);
}
