package com.decagon.dispatchbuddy.controllers;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.enums.AuthProvider;
import com.decagon.dispatchbuddy.enums.Status;
import com.decagon.dispatchbuddy.pojos.*;
import com.decagon.dispatchbuddy.services.MessagingService;
import com.decagon.dispatchbuddy.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api")
public class UserController {

    private final UserService userService;
    private final MessagingService messagingService;

    @PostMapping("/user/signup")
    public APIResponse<User> signUp(@RequestBody User user){
        if(user.getAuthProvider()== AuthProvider.GOOGLE)
            return  userService.googleSignUp(user);
        return userService.signUp(user);
    }

    @PostMapping("/user/validate")
    public APIResponse<User> validateUser(@RequestBody UserRequestWithUsername request){
        return messagingService.generateAndSendOTP(request);
    }

    @PostMapping("/user/verify")
    public APIResponse<User> verifyUser(@RequestBody UserVerificationRequest verificationRequest){
        return userService.verifyUser(verificationRequest);
    }

    @PostMapping("/user/password-reset")
    public APIResponse<User> resetPassword(@RequestBody UserRequest userPasswordResetRequest){
        return userService.resetPassword(userPasswordResetRequest);
    }

    @PostMapping("/user/logout")
    public APIResponse<User> resetPassword(){
        return userService.logout();
    }

    @PostMapping("/user/delete/{userId}")
    public APIResponse<User> deleteUserById(@PathVariable String userId){
        return userService.deleteUerById(userId);
    }

    @PutMapping("/user/update")
    public APIResponse<User> updateUserProfileById(OAuth2Authentication authentication, @RequestBody User newDetails){
        return userService.updateUserProfileById(authentication,newDetails);
    }

    @PutMapping("/user/update-dp")
    public APIResponse<User> updateUserProfilePictureById(OAuth2Authentication authentication, @Valid @RequestParam(value = "dp") MultipartFile image){
        return userService.updateUserProfilePictureById(authentication,image);
    }


    @GetMapping("/user/get-by-id/{id}")
    public APIResponse<List<User>> getUserById(@PathVariable String id){
        return userService.findUserById(id);
    }

    @GetMapping("/user/get-by-uuid/{uuid}")
    public APIResponse<List<User>> getUserByUuid(@PathVariable String uuid){
        return userService.findUserByUuid(uuid);
    }

    @GetMapping("/user/get-all-by-role")
    public APIResponse<List<User>> getUsersByType(@RequestParam String role, @RequestParam int page, @RequestParam int size){
        return userService.findUsersByRole(PageRequest.of(page,size, Sort.by("id").descending()),role);
    }
    @GetMapping("/user/get-all-by-account_type")
    public APIResponse<List<User>> getUsersByAccountType(@RequestParam String type, @RequestParam int page, @RequestParam int size){
        return userService.findUsersByAccountType(PageRequest.of(page,size, Sort.by("id").descending()),type);
    }
}
