package com.decagon.dispatchbuddy.services;

import com.decagon.dispatchbuddy.entities.DispatcherRating;
import com.decagon.dispatchbuddy.entities.Request;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.pojos.APIResponse;
import com.decagon.dispatchbuddy.pojos.RatingDto;
import com.decagon.dispatchbuddy.repositories.RatingRepository;
import com.decagon.dispatchbuddy.repositories.RequestRepository;
import com.decagon.dispatchbuddy.repositories.UserRepository;
import com.decagon.dispatchbuddy.util.App;
import com.decagon.dispatchbuddy.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    @Autowired
    private App app;
    @Autowired
    private final Response response;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final RequestRepository requestRepository;

    public APIResponse rateDispatcher (RatingDto ratingDto) {
        User dispatcher = userRepository.findByUuid(ratingDto.getUuid()).orElse(null);
        Request request = requestRepository.findByRiderUuid(ratingDto.getRequestUuid()).orElse(null);
        DispatcherRating rated = ratingRepository.findByRequest(request).orElse(null);

        if (dispatcher == null)
            return response.failure("Unable to find this Dispatcher for rating");
        if (request == null)
            return response.failure("Unable to find this Rider");
        if (rated == null)
            return response.failure("You can only rate once");
        if(ratingDto.getRating()<0)
            return response.failure("Rating can not be less than zero");
        if (ratingDto.getRating()>5)
            return response.failure("Rating can not be greater than 5");


        String rate = app.rating2DCP(ratingDto.getRating());

        try {
            DispatcherRating rating = DispatcherRating.builder()
                    .id(app.makeUIID())
                    .rating(rate)
                    .dispatcher(dispatcher)
                    .comment(ratingDto.getComment())
                    .request(request)
                    .build();
            ratingRepository.save(rating);
            return response.success("Rating created successfully");

        } catch (Exception e) {
            return response.failure("Couldn't create rating: " + e.getMessage());
        }
    }


    public APIResponse findDispatchRatingByUuid(Pageable page,  String Uuid){
        User dispatcherToFind = userRepository.findByUuid(Uuid).orElse(null);
        Page<List<DispatcherRating>> ratings = ratingRepository.findAllByDispatcherId(page,Uuid);
        if(dispatcherToFind==null)
            return response.failure("No Dispatcher with the Uuid: " +Uuid + " was found");
        if (!ratings.isEmpty())
                return response.success(ratings);

        return response.success("No ratings found for this dispatcher yet");

    }
}

