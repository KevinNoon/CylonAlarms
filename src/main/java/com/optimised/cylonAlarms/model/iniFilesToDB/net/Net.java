package com.optimised.cylonAlarms.model.iniFilesToDB.net;

import com.optimised.cylonAlarms.model.iniFilesToDB.alarm.AlarmStr;
import com.optimised.cylonAlarms.model.iniFilesToDB.site.Site;
import com.optimised.cylonAlarms.model.iniFilesToDB.controller.Controller;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "Net")
public class Net implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ManyToOne
    private Site site;

    private String name;
    private Integer address;
    private Boolean existing;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "id")
    private Set<Controller> controllerEntities = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "id")
    private Set<AlarmStr> alarmStrEntities = new HashSet<>();

    public Net(Integer id, Site site, String name, Integer address, Boolean existing) {
        this.id = id;
        this.site = site;
        this.name = name;
        this.address = address;
        this.existing = existing;

    }
}
