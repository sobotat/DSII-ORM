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
    private boolean active;

    public User(int userId){
        this.userId = userId;
    }

    public User(int userId, int roleId){
        this.userId = userId;
        this.userRole = new UserRole(roleId);
    }

    public User(int userId, String firstName, String lastName, LocalDate bornDate, String email, String password, UserRole userRole, boolean active) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bornDate = bornDate;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.active = active;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Getter
    @ToString
    public static class UserRole {
        private int roleId;
        private String name;
        private String description;

        public UserRole(int roleId){
            this.roleId = roleId;
        }

        public UserRole(int roleId, String name, String description) {
            this.roleId = roleId;
            this.name = name;
            this.description = description;
        }
    }
}
