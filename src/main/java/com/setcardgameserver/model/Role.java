package com.setcardgameserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Table
@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Role extends BaseEntity implements Serializable {
    @Column(name = "Name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

    @Column(name = "Description", nullable = false)
    private String description;
}
