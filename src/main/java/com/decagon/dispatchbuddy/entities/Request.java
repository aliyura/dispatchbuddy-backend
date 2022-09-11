package com.decagon.dispatchbuddy.entities;
import com.decagon.dispatchbuddy.enums.Status;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Document("requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    @NotNull
    private String requestId;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String pickupLocation;
    private String destination;
    private String riderName;
    private String riderUuid;
    private String riderPhone;
    private Double payableAmount;
    private String size;
    private String distance;
    @Enumerated(EnumType.STRING)
    Status status;
    private String statusReason;
    @OneToMany(mappedBy = "dispatcher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DispatcherRating> rating = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @PrePersist
    private void setCreatedDate() {
        createdDate = new Date();
    }

    @PreUpdate
    private void setLastModifiedDate() {
        lastModifiedDate = new Date();
    }

    @Override
    public boolean equals(Object user) {
        return this.id.equals(((Request)user).getId());
    }
}