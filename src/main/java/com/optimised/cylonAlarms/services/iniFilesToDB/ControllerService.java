package com.optimised.cylonAlarms.services.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.controller.Controller;
import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import com.optimised.cylonAlarms.repository.iniFilesToDB.ControllerRepo;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControllerService {
    @Autowired
    ControllerRepo controllerRepo;

    public void addUpdate(Controller controllerEntity){
        try {
            Controller ctrl = controllerRepo.findFirstByNameAndNet(controllerEntity.getName(),controllerEntity.getNet());
            if (!(ctrl == null)){
                controllerEntity.setId(ctrl.getId());
            }
            controllerRepo.save(controllerEntity);
        } catch (
                ConstraintViolationException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<Controller> list(){
        return (List<Controller>) controllerRepo.findAll();
    }
    public void delete(Controller controller){
        controllerRepo.delete(controller);
    }
    public void deleteNoExisting(){
        controllerRepo.deleteByExistingFalse();
    }
    public void setExisting(){
        controllerRepo.existingToFalse();
    }

    public Controller getController(int controller, int net){
        return controllerRepo.getNetName(controller, net).get(0);
    }
}
