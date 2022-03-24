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

@Table(name = "Alarm_QueueKN")
public class AlarmQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    @Column(name = "site_name")
    String SiteName;
    @Column(name = "site_number")
    Integer SiteNumber;
    Integer Acknowledged;
    Timestamp Submitted;
    @Column(name = "alarm_packet")
    Byte[] AlarmPacket;
}
