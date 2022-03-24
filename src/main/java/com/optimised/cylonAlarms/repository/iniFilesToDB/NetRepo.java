package com.optimised.cylonAlarms.repository.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.controller.Controller;
import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NetRepo extends CrudRepository<Net,Long> {

    Net findFirstByNameAndSite(String name, Site site);

    @Transactional
    @Modifying
    @Query("UPDATE Net SET existing=false ")
    public int existingToFalse();

    @Transactional
    @Modifying
    @Query("DELETE FROM Net WHERE existing=false ")
    public void deleteByExistingFalse();

    @Query("SELECT n FROM Net n JOIN n.site s WHERE n.address = ?1 AND s.siteNumber = ?2")
    List<Net> getNetName(int net, int site);

}
