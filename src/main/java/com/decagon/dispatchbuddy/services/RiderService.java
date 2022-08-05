package com.decagon.dispatchbuddy.services;

import com.decagon.dispatchbuddy.entities.Request;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.enums.AccountType;
import com.decagon.dispatchbuddy.enums.AuthProvider;
import com.decagon.dispatchbuddy.enums.Status;
import com.decagon.dispatchbuddy.enums.UserRole;
import com.decagon.dispatchbuddy.interfaceimpl.GoogleOauthProvider;
import com.decagon.dispatchbuddy.pojos.*;
import com.decagon.dispatchbuddy.repositories.RequestRepository;
import com.decagon.dispatchbuddy.repositories.UserRepository;
import com.decagon.dispatchbuddy.util.App;
import com.decagon.dispatchbuddy.util.AuthDetails;
import com.decagon.dispatchbuddy.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RiderService {

    @Autowired
    private App app;
    @Autowired
    private final Response response;
    @Autowired
    private final AuthDetails authDetails;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;


    public APIResponse requestRider(RequestRider request) {
        User rider = userRepository.findByUuid(request.getRiderUuid()).orElse(null);
        if (rider == null)
            return response.failure("Unable to find this Rider ");
        else if (request.getName() == null)
            return response.failure("User Name Required");
        else if (request.getEmail() == null)
            return response.failure("User Email Required");
        else if (request.getPhone() == null)
            return response.failure("User Phone Number Required");
        else if (request.getPickupLocation() == null)
            return response.failure("Pickup Location Required");
        else if (request.getDestination() == null)
            return response.failure("Destination Required");
        else {

            Request requestRider = new Request();
            requestRider.setRiderName(rider.getName());
            requestRider.setRiderUuid(rider.getUuid());
            requestRider.setRiderPhone(rider.getPhoneNumber());
            requestRider.setUserName(request.getName());
            requestRider.setUserEmail(request.getEmail());
            requestRider.setUserPhoneNumber(request.getPhone());
            requestRider.setPickupLocation(request.getPickupLocation());
            requestRider.setDestination(request.getDestination());
            requestRider.setStatus(Status.PC);
            requestRider.setRequestId(app.generateSerialNumber("REQ"));
            requestRider.setCreatedDate(new Date());

            Request savedRequest = requestRepository.save(requestRider);
            if (savedRequest != null) {
                return response.success(savedRequest);
            } else {
                return response.failure("Unable to contact Rider!");
            }
        }
    }

    public APIResponse updateRequestStatus(String requestId, Status status, String statusReason) {
        Request request = requestRepository.findById(requestId).orElse(null);
        List<Request> requestList = requestRepository.findAllByStatus(Status.AC);
        if (!requestList.isEmpty() && status == Status.AC)
            return response.failure("You already have an active ride");

        if (request != null) {
            request.setStatus(status);
            request.setStatusReason(statusReason);
            request.setLastModifiedDate(new Date());
            return response.success(requestRepository.save(request));
        } else {
            return response.failure("Request not found");
        }

    }
    public APIResponse getRequests(OAuth2Authentication authentication, int page) {
        User rider = authDetails.getAuthorizedUser(authentication);
        Page<Request> request = requestRepository.findAllByRiderUuid(rider.getUuid(),PageRequest.of(page,20));
        if (!request.isEmpty()) {
            return response.success(request);
        } else {
            return response.failure("No request found");
        }
    }


}