package com.decagon.dispatchbuddy.repositories;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.enums.AccountType;
import com.decagon.dispatchbuddy.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

    Optional<User> findByUuid(String uuid);

    @Query(value="{'coveredLocations': ?0, 'coveredLocations': ?1}")
    Page<User> searchRider(String from, String to, Pageable pageable);

    Boolean existsByEmail(String email);

    Boolean existsByPhoneNumber(String phoneNumber);

    Boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

    Page<List<User>> findAllByRole(Pageable pageable, UserRole role);
    Page<User> findAll(Pageable pageable);


    Page<List<User>> findAllByAccountType(Pageable pageable, AccountType type);
}
