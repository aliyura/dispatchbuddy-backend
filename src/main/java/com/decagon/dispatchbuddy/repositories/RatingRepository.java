package com.decagon.dispatchbuddy.repositories;

import com.decagon.dispatchbuddy.entities.DispatcherRating;
import com.decagon.dispatchbuddy.entities.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends MongoRepository<DispatcherRating, String> {
    Optional<DispatcherRating> findByRequest(Request request);
    Page<List<DispatcherRating>> findAllByDispatcherId(Pageable pageable, String Uuid);

}
