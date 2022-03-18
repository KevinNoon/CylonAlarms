package com.optimised.cylonAlarms.repository.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface NetRepo extends CrudRepository<Net,Long> {

    Net findFirstByNameAndSite(String name, Site site);
}
