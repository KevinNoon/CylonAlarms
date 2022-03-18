package com.optimised.cylonAlarms.model.alarmsToIPQueue.queue;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "AlarmQueue")
public class AlarmQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String SiteName;
    Integer SiteNumber;
    Integer Acknowledged;
    Timestamp Submitted;
    String AlarmPacket;
}
