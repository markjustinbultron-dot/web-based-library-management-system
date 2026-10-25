package com.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String name;

    // Added to satisfy MySQL 'surname' requirement
    private String surname = "";

    @Column(nullable = false, unique = true)
    private String email;

    private String idNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    private boolean banned = false;

    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;

    public User() {
    }

    public User(String name, String email, String idNumber, String phoneNumber, String password, Role role) {
        this.name = name;
        this.email = email;
        this.idNumber = idNumber;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}