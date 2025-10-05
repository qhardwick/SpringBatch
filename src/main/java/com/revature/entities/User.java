package com.revature.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    // Year is typically an int but I am using String here to more closely match what I need for
    // the project where I am actually implementing Spring Batch:
    private String year;

    // @Data should provide all of these, but for whatever reason the IntelliJ compiler has no problem with the compile-time
    // generation until it actually tries to compile:
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
