package com.optimised.cylonAlarms.repository.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface SiteRepo extends CrudRepository<Site,Long> {

    Site findFirstByNameOrDirectory(String name, String director);

    @Transactional
    @Modifying
    @Query("UPDATE Site SET existing=false")
    public int existingToFalse();

    @Transactional
    @Modifying
    @Query("DELETE FROM Site WHERE existing=false ")
    public void deleteByExistingFalse();

 }
