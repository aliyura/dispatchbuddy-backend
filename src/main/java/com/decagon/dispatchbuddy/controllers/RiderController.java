package com.decagon.dispatchbuddy.controllers;

import com.decagon.dispatchbuddy.entities.Request;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.enums.Status;
import com.decagon.dispatchbuddy.pojos.*;
import com.decagon.dispatchbuddy.services.MessagingService;
import com.decagon.dispatchbuddy.services.RiderService;
import com.decagon.dispatchbuddy.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api")
public class RiderController {

    private final UserService userService;
    private  final RiderService riderService;
    private final MessagingService messagingService;

    @PostMapping("/rider/add-locations")
    public APIResponse<User> addLocationsToCover(OAuth2Authentication authentication,@RequestBody AddLocationRequest request){
        return userService.addLocationToCover(authentication,request);
    }

    @GetMapping("/rider/search")
    public APIResponse<Page<User>> getUserById(@RequestParam("page") int page, @RequestParam("pickup") String from, @RequestParam("destination") String destination){
        return userService.searchRider(page,from, destination);
    }

    @PostMapping("/rider/request")
    public APIResponse<Request> requestRider(@RequestBody RequestRider requestRider){
        return riderService.requestRider(requestRider);
    }

    @PostMapping("/rider/accept-request/{id}")
    public APIResponse<Request> acceptRequest(@PathVariable("id") String requestId){
       return riderService.updateRequestStatus(requestId, Status.AC,"Accepted");
    }

    @PostMapping("/rider/close-request/{id}")
    public APIResponse<Request> closeRequest(@PathVariable("id") String requestId){
        return riderService.updateRequestStatus(requestId, Status.CO,"Closed");
    }

    @PostMapping("/rider/reject-request")
    public APIResponse<Request> acceptRequest(@RequestBody RejectRequest request){
        return riderService.updateRequestStatus(request.getId(), Status.RJ,request.getRejectionReason());
    }

    @GetMapping("/rider/requests")
    public APIResponse<Request> getRequests(OAuth2Authentication authentication, @RequestParam("page") int page){
        return riderService.getRequests(authentication,page);
    }
}
