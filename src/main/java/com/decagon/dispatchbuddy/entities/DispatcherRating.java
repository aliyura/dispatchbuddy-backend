package com.decagon.dispatchbuddy.entities;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Document("dispatcher_rating")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatcherRating implements Serializable {

    @Id
    private String id;
    private String comment;
    private String rating;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User dispatcher;
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="request_id", nullable = false)
    private Request request;



    @PrePersist
    private void setCreatedAt() {
        createdDate = new Date();
    }
    @Override
    public boolean equals(Object rating) {
        return this.id.equals(((DispatcherRating)rating).getId());
    }

}
