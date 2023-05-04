package org.dsII.orm.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class User {

    private int userId;
    private String firstName;
    private String lastName;
    private LocalDate bornDate;
    private String email;

    @ToString.Exclude
    private String password;

    private UserRole userRole;

    public User(int userId, String firstName, String lastName, LocalDate bornDate, String email, String password, UserRole userRole) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bornDate = bornDate;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public static class UserRole {
        private final int roleId;
        private final String name;
        private final String description;

        public UserRole(int roleId, String name, String description) {
            this.roleId = roleId;
            this.name = name;
            this.description = description;
        }

        // Getters
        public int getRoleId() {
            return roleId;
        }
        public String getName() {
            return name;
        }
        public String getDescription() {
            return description;
        }
    }
}
