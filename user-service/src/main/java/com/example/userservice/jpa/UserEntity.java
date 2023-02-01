package com.example.userservice.jpa;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String userId;
    @Column(length = 50)
    private String email;
    @Column(length = 50)
    private String name;
    @Column(length = 50, unique = true)
    private String pwd;
    @Column(unique = true)
    private String encryptedPwd;
}
