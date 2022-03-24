package com.optimised.cylonAlarms.services.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import com.optimised.cylonAlarms.repository.iniFilesToDB.SiteRepo;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService {
    @Autowired
    SiteRepo siteRepo;

    public void AddUpdate(Site siteEntity) {

        try {
            Site WNini = siteRepo.findFirstByNameOrDirectory(siteEntity.getName(),siteEntity.getDirectory());
            if (!(WNini == null)){
                siteEntity.setId(WNini.getId());
            }
            siteRepo.save(siteEntity);
        } catch (
                ConstraintViolationException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Site> list(){
        return (List<Site>) siteRepo.findAll();
    }

    public void delete(Site site){
        siteRepo.delete(site);
    }

    public void deleteNoExisting(){
        siteRepo.deleteByExistingFalse();
    }

    public void setExisting(){
        siteRepo.existingToFalse();
    }
}
