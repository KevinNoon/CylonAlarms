package com.optimised.cylonAlarms.model.iniFilesToDB.controller;

import com.optimised.cylonAlarms.model.iniFilesToDB.net.Net;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "Controller")
public class Controller implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

  //  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ManyToOne
    private Net net;

    private String name;
    private Integer address;
    private Boolean existing;
}
