package com.decagon.dispatchbuddy.services;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.enums.*;
import com.decagon.dispatchbuddy.interfaceimpl.GoogleOauthProvider;
import com.decagon.dispatchbuddy.pojos.*;
import com.decagon.dispatchbuddy.repositories.UserRepository;
import com.decagon.dispatchbuddy.util.App;
import com.decagon.dispatchbuddy.util.AuthDetails;
import com.decagon.dispatchbuddy.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private App app;
    @Autowired
    private final Response response;
    @Autowired
    private final AuthDetails authDetails;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessagingService messagingService;
    private  final GoogleOauthProvider googleOauthProvider;
    private final LocalStorageService memcached;

    private  final  FileStorageService fileStorage;



    public APIResponse signUp(User user) {

        User userByEmail = userRepository.findByEmail(user.getEmail()).orElse(null);
        User userByMobile = userRepository.findByPhoneNumber(user.getPhoneNumber()).orElse(null);

        if (user.getEmail() == null)
            return response.failure("Email Address Required");
        else if (user.getPassword() == null)
            return response.failure("User Password Required");
        else if (userByEmail != null)
            return response.failure("Account already exist!");
        else if (userByMobile != null)
            return response.failure("Account already exist!");
        else if (!app.validEmail(user.getEmail()))
            return response.failure("Invalid Email Address!");
        else if (!app.validNumber(user.getPhoneNumber()))
            return response.failure("Invalid Mobile Number!");
        else {

            if (user.getAccountType() == null)
                user.setAccountType(AccountType.DISPATCHER);
            if (user.getRole() == null)
                user.setRole(UserRole.USER);

            user.setStatus(Status.PV);
            user.setIsEnabled(false);
            user.setCreatedDate(new Date());
            user.setLastLoginDate(new Date());
            user.setUuid(app.makeUIID());
            user.setAuthProvider(AuthProvider.EMAIL);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            if (savedUser != null) {
                UserRequestWithUsername request = new UserRequestWithUsername();
                request.setUsername(user.getPhoneNumber()!=null?user.getPhoneNumber():user.getEmail());
                messagingService.generateAndSendOTP(request);
                return response.success(savedUser);
            }
            else{
                return response.failure("Unable to create Account!");
        }
        }
    }
    public APIResponse googleSignUp(User request) {

        if (request.getThirdPartyToken() == null)
            return response.failure("Third party token must be provided");

        app.print("##############LOGIN WITH GOOGLE");
        app.print("Request:");
        app.print(request);
        // generate uuid for user
        ThirdPartyOauthResponse thirdPartyOauthResponse = googleOauthProvider.authentcate(request.getThirdPartyToken());
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            User existingUser = userRepository.findByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber()).orElse(null);
            return response.failure("Account Already Exist!");
        }

        // generate uuid for user
        String generatedUuid = app.makeUIID();
        User user = User.builder().name(thirdPartyOauthResponse.getFirstName() + " " + thirdPartyOauthResponse.getLastName())
                .email(thirdPartyOauthResponse.getEmail())
                .isEnabled(Boolean.TRUE)
                .uuid(generatedUuid).dp(thirdPartyOauthResponse.getImage())
                .status(Status.AC)
                .createdDate(new Date())
                .lastLoginDate(new Date())
                .thirdPartyToken(thirdPartyOauthResponse.getIdToken())
                .authProvider(AuthProvider.GOOGLE)
                .role(UserRole.USER)
                .accountType(AccountType.DISPATCHER)
                .authProvider(request.getAuthProvider()).build();

        user = userRepository.save(user);
        app.print("Saved user:");
        app.print(user);
        return  response.success(user);
    }

    public APIResponse logout(){
       return  response.success("success");
    }

    public APIResponse verifyUser(UserVerificationRequest request) {
        User user = userRepository.findByPhoneNumber(request.getUsername()).orElse(
                userRepository.findByEmail(request.getUsername()).orElse(null)
        );
        if (user != null) {
            String otp= memcached.getValueByKey(user.getUuid());
            app.print("Stored OTP:"+otp);
            app.print("Provided OTP:"+request.getOtp());

            if (otp!=null && otp.equals(request.getOtp())) {
                user.setLastModifiedDate(new Date());
                user.setIsEnabled(true);
                user.setStatus(Status.AC);

                return response.success(userRepository.save(user));
            } else {
                return response.failure("Invalid OTP");
            }
        } else {
            return response.failure("Account not found");
        }
    }

    public APIResponse resetPassword(UserRequest request) {
        User user = userRepository.findByEmail(request.getUsername()).orElse(
                userRepository.findByPhoneNumber(request.getUsername()).orElse(null)
        );
        if (user != null) {
            user.setLastModifiedDate(new Date());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            return response.success(userRepository.save(user));
        } else {
            return response.failure("Account not found");
        }
    }

    public APIResponse updateUserProfileById(OAuth2Authentication authentication, User newDetails) {
        User user=authDetails.getAuthorizedUser(authentication); 
        if (user != null) {

            if (newDetails.getName() != null)
                user.setName(newDetails.getName());
            if (newDetails.getAccountType() != null)
                user.setAccountType(newDetails.getAccountType());
            if (newDetails.getRole() != null)
                user.setRole(newDetails.getRole());
            if (newDetails.getCountry() != null)
                user.setCountry(newDetails.getCountry());
            if (newDetails.getCity() != null)
                user.setCity(newDetails.getCity());
            if (newDetails.getDateOfBirth() != null)
                user.setDateOfBirth(newDetails.getDateOfBirth());
            if (newDetails.getGender() != null)
                user.setGender(newDetails.getGender());

            if (newDetails.getPhoneNumber() != null) {
                if(app.validNumber(newDetails.getPhoneNumber())) {
                    User appUser=userRepository.findByPhoneNumber(newDetails.getPhoneNumber()).orElse(null);
                    if(appUser==null || appUser.getId()==user.getId()) {
                        user.setPhoneNumber(newDetails.getPhoneNumber());
                    }
                }
                else{
                    return  response.failure("Account already exist with this phone number");
                }
            }
            if (newDetails.getEmail() != null) {
                if(app.validEmail(newDetails.getEmail())) {
                    User appUser=userRepository.findByEmail(newDetails.getEmail()).orElse(null);
                    if(appUser==null || appUser.getId()==user.getId()) {
                        user.setEmail(newDetails.getEmail());
                    }
                }else{
                    return  response.failure("Account already exist with this Email");
                }
            }

            user.setLastModifiedDate(new Date());
            user.setUpdatedDate(new Date());

           return response.success(userRepository.save(user));
        } else {
          return  response.failure("Account not found");
        }
    }


    public APIResponse updateUserProfilePictureById(OAuth2Authentication authentication, MultipartFile image) {
        User user=authDetails.getAuthorizedUser(authentication);
        if (user != null) {

            if (image!=null && app.validImage(image.getOriginalFilename())) {

                String fileName = "dp" + user.getUuid() + ".jpg";
                APIResponse fileResponse = fileStorage.uploadFile(fileName, image);
                if (fileResponse.isSuccess())
                    user.setDp(fileResponse.getPayload().toString());
                else
                    return  response.failure(fileResponse.getMessage());

                user.setLastModifiedDate(new Date());
                user.setUpdatedDate(new Date());

                return response.success(userRepository.save(user));
            }else{
                return  response.failure("Invalid Display Picture");
            }
        } else {
            return  response.failure("Account not found");
        }
    }



    public APIResponse updateUserStatusById(String userId, Status status) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setStatus(status);
            user.setLastModifiedDate(new Date());
            if (status.equals(Status.AC))
                user.setIsEnabled(true);

            if (user.getAccountType().equals(AccountType.DISPATCHER)) {
                if (user.getStatus() == Status.AC) {
                    messagingService.sendSMS(user.getPhoneNumber(), "Congratulations! Your dispatcher account has been approved, you can now login to your dashboard");
                }
            }

            return response.success(userRepository.save(user));
        } else {
            return response.failure("Account not found");
        }
    }

    public APIResponse findUsersByRole(Pageable page,String  role){
        UserRole requestedRole=null;
        try {
            requestedRole=UserRole.valueOf(role.toUpperCase());
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
        if(requestedRole!=null) {
            Page<List<User>> userList = userRepository.findAllByRole(page, requestedRole);
            if (!userList.isEmpty())
                return response.success(userList);
            else
                return response.failure("No " + role.toLowerCase() + " Available");
        }else{
            return  response.failure("No ("+role+") found as a user Role");
        }
    }


    public APIResponse findUsersByAccountType(Pageable pageable,String  type){
        AccountType requestedType=null;
        try {
            requestedType=AccountType.valueOf(type.toUpperCase());
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
        if(requestedType!=null) {
            Page<List<User>> userList = userRepository.findAllByAccountType(pageable, requestedType);
            if (!userList.isEmpty())
                return response.success(userList);
            else
                return response.failure("No  Account found from " + type.toLowerCase() + "");
        }else{
            return  response.failure("No ("+type+") found as an Account type");
        }
    }

    public APIResponse findUserByUuid(String  uuid) {
        User  user = userRepository.findByUuid(uuid).orElse(null);
        return user != null ? response.success(user) : response.failure("User not found");
    }


    public APIResponse findUserById(String  id) {
        User  user = userRepository.findById(id).orElse(null);
        return user != null ? response.success(user) : response.failure("User not found");
    }


    public APIResponse deleteUerById(String userId){
        User user = userRepository.findById(userId).orElse(null);
        if(user!=null)
          userRepository.deleteById(userId);
        return response.success(user);
    }
}