package com.setcardgameserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table
@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false)
    //TODO switch to Long from UUID and create BaseEntity
    private UUID id;

    @Column(name = "Username", unique = true, length = 100, nullable = false)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "RoleID", referencedColumnName = "ID", nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(updatable = false, name = "CreatedAt")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private Date updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName().toString());

        return List.of(authority);
    }

    @Override
    public String getUsername() {
        return username;
    }
}
