package com.decagon.dispatchbuddy.repositories;

import com.decagon.dispatchbuddy.entities.Request;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.enums.AccountType;
import com.decagon.dispatchbuddy.enums.Status;
import com.decagon.dispatchbuddy.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends MongoRepository<Request, String> {
    Optional<Request> findByUserEmail(String email);

    Optional<Request> findByUserPhoneNumber(String phoneNumber);

    Optional<Request> findByRiderUuid(String uuid);
    Page<Request> findAllByRiderUuid(String riderUuid, Pageable pageable);

    List<Request> findAllByStatus(Status status);
}
