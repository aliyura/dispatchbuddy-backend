package com.decagon.dispatchbuddy.controllers;


import com.decagon.dispatchbuddy.entities.DispatcherRating;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.pojos.APIResponse;
import com.decagon.dispatchbuddy.pojos.RatingDto;
import com.decagon.dispatchbuddy.services.RatingService;
import com.decagon.dispatchbuddy.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "rate")
public class RatingController {
    private final RatingService ratingService;
    private final UserService userService;
    @PostMapping("/dispatcher")
    public APIResponse rateDispatcher(@RequestBody RatingDto ratingDto){
        return ratingService.rateDispatcher(ratingDto);
    }

    @GetMapping("/get-all-ratings-by-dispatch-rider/{Uuid}")
    public APIResponse<List<DispatcherRating>> getRatingsForDispatchRider(@PathVariable("Uuid") String Uuid, @RequestParam int page, @RequestParam int size){
        return ratingService.findDispatchRatingByUuid(PageRequest.of(page,size, Sort.by("Uuid").descending()),Uuid);
    }

    @GetMapping("/get-all-dispatchers")
    public APIResponse<List<User>> getAllUsers(@RequestParam int page, @RequestParam int size){
        return userService.getAllUsers(PageRequest.of(page,size, Sort.by("id").descending()));
    }
}
