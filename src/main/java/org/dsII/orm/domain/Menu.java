package org.dsII.orm.domain;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class Menu {
    private int menuId;
    private LocalDate date;
    private LocalDate createdDate;

    public Menu(int menuId, LocalDate date, LocalDate createdDate) {
        this.menuId = menuId;
        this.date = date;
        this.createdDate = createdDate;
    }

    // Setters
    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
