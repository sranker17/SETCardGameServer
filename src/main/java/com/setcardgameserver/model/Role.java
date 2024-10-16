package com.setcardgameserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;

@Table
@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "Name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

    @Column(name = "Description", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(updatable = false, name = "CreatedAt")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private Date updatedAt;
}
