package com.optimised.cylonAlarms.model.iniFilesToDB.alarm;

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

@Table(name = "Alarm_Str")
public class AlarmStr implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

 //   @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ManyToOne
    private Net net;

    private Integer number;
    private String message;
    private Boolean existing;

}
