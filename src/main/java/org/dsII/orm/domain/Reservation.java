package org.dsII.orm.domain;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
public class Reservation {

    private int reservationId;
    private String onName;
    private String telephone;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private LocalDateTime createdDate;
    private User createdBy;

    public Reservation(int reservationId) {
        this.reservationId = reservationId;
    }

    public Reservation(int reservationId, String onName, String telephone, LocalDateTime timeStart, LocalDateTime timeEnd, LocalDateTime createdDate, User createdBy) {
        this.reservationId = reservationId;
        this.onName = onName;
        this.telephone = telephone;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
    }
}
