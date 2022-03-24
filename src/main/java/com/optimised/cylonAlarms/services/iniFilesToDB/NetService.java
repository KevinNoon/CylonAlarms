package com.optimised.cylonAlarms.services.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.controller.Controller;
import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import com.optimised.cylonAlarms.repository.iniFilesToDB.NetRepo;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetService {
    @Autowired
    NetRepo netRepo;

    public void addUpdate(Net netEntity){
        try {
            Net net = netRepo.findFirstByNameAndSite(netEntity.getName(),netEntity.getSite());
            if (!(net == null)){
                netEntity.setId(net.getId());
            }
            netRepo.save(netEntity);
        } catch (
                ConstraintViolationException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<Net> list(){
        return (List<Net>) netRepo.findAll();
    }
    public void delete(Net net){
        netRepo.delete(net);
    }
    public void deleteNoExisting(){
        netRepo.deleteByExistingFalse();
    }
    public void setExisting(){
        netRepo.existingToFalse();
    }

    public Net getNet(int net,int site){
        return netRepo.getNetName(net, site).get(0);
    }

}
