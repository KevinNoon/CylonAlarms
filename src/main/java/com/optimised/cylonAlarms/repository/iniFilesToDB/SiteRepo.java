package com.optimised.cylonAlarms.repository.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface SiteRepo extends CrudRepository<Site,Long> {

    Site findFirstByNameOrDirectory(String name, String director);
}
