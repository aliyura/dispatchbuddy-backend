package com.decagon.dispatchbuddy.entities;
import com.decagon.dispatchbuddy.enums.AccountType;
import com.decagon.dispatchbuddy.enums.AuthProvider;
import com.decagon.dispatchbuddy.enums.UserRole;
import com.decagon.dispatchbuddy.enums.Status;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Document("users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    @NotNull
    private String name;
    @Indexed(unique = true)
    private String uuid;
    @Indexed(unique = true)
    private String email;
    private String password;
    @Indexed(unique = true)
    private String phoneNumber;
    private String gender;
    private String city;
    private String country;

    private String dp;
    private String dateOfBirth;

    private String thirdPartyToken;

    private Boolean isEnabled = true;
    private String otp;
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;
    @Enumerated(EnumType.STRING)
    AccountType accountType;

    @Enumerated(EnumType.STRING)
    Status status;

    @Enumerated(EnumType.STRING)
    UserRole role;
    Set<String> coveredLocations;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    @OneToMany(mappedBy = "dispatcher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DispatcherRating> rating = new HashSet<>();





    public User(User user){
        this.isEnabled = user.getIsEnabled();
        this.id = user.getId();
        this.name = user.getName();
        this.uuid = user.getUuid();
        this.authProvider = user.getAuthProvider();
        this.email = user.getEmail();
        this.phoneNumber=user.getPhoneNumber();
        this.password = user.getPassword();
        this.dateOfBirth=user.getDateOfBirth();
        this.city = user.getCity();
        this.status=user.getStatus();
        this.accountType=user.getAccountType();
        this.dp=user.getDp();
        this.thirdPartyToken=user.getThirdPartyToken();
        this.role=user.getRole();
        this.gender=user.getGender();
        this.createdDate=user.getCreatedDate();
        this.updatedDate = user.getUpdatedDate();
    }

    @PrePersist
    private void setCreatedAt() {
        createdDate = new Date();
    }

    @PreUpdate
    private void setUpdatedAt() {
        updatedDate = new Date();
    }

    @Override
    public boolean equals(Object user) {
        return this.id.equals(((User)user).getId());
    }
}