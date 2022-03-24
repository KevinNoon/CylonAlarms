package com.optimised.cylonAlarms.services.iniFilesToDB;

import com.optimised.cylonAlarms.model.iniFilesToDB.alarm.AlarmStr;
import com.optimised.cylonAlarms.model.iniFilesToDB.controller.Controller;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import com.optimised.cylonAlarms.repository.iniFilesToDB.AlarmStrRepo;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmStrService {
    @Autowired
    AlarmStrRepo alarmStrRepo;

    public void addUpdate(AlarmStr alarmStrEntity){
        try {
            AlarmStr ctrl = alarmStrRepo.findFirstByNumberAndNet(alarmStrEntity.getNumber(),alarmStrEntity.getNet());
            if (!(ctrl == null)){
                alarmStrEntity.setId(ctrl.getId());
            }
            alarmStrRepo.save(alarmStrEntity);
        } catch (
                ConstraintViolationException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<AlarmStr> list(){
        return (List<AlarmStr>) alarmStrRepo.findAll();
    }

    public void delete(AlarmStr alarmStr){
        alarmStrRepo.delete(alarmStr);
    }
    public void deleteNoExisting(){
        alarmStrRepo.deleteByExistingFalse();
    }
    public void setExisting(){
        alarmStrRepo.existingToFalse();
    }
    public AlarmStr getAlarmStr(int alarmStr, int net){
        return alarmStrRepo.getAlarmStr(alarmStr, net).get(0);
    }
}
