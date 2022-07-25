package com.decagon.dispatchbuddy.services;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.pojos.APIResponse;
import com.decagon.dispatchbuddy.pojos.UserRequest;
import com.decagon.dispatchbuddy.pojos.UserRequestWithUsername;
import com.decagon.dispatchbuddy.repositories.UserRepository;
import com.decagon.dispatchbuddy.util.App;
import com.decagon.dispatchbuddy.util.Response;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MessagingService {

    @Autowired
    private final App app;
    @Autowired
    private final Response response;
    @Autowired
    private RestTemplate rest;
    private final UserRepository userRepository;
    //@Value("${lenos.twilio.id}")
    private String twilioId="ACc404d34ef1992f4ee102d14d52327775";
    //@Value("${lenos.twilio.token}")
    private String twilioToken="6ddb3dba8963812daf0bacef37005bc2";
    //@Value("${lenos.twilio.number}")
    private String twilioNumber="+12243026369";
    private String smsBaseURL="https://www.bulksmsnigeria.com/api/v1/sms/create";
    private String smsToken="Ak2NPcmNEq4p3GCfK2Ecv6tQA68nslopTnnNZhF91XSz5NgdrYylS855rbc1";
    private final LocalStorageService memcached;


    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setCacheControl("no-cache");
        return headers;
    }


    public APIResponse sendSMS(String recipient, String textMessage) {
        recipient=app.getFormattedNumber(recipient);
        app.print(""+recipient);
        app.print(""+textMessage);
        app.print("BulkSMSNigeria Sending...");
        try {
            String endpoint=smsBaseURL
                    + "?api_token=" + smsToken
                    + "&to=" + recipient
                    + "&from=Dispatchbuddy"
                    + "&body=" + textMessage
                    + "&gateway=0"
                    + "&append_sender=0";

            app.print(endpoint);
            HttpEntity<String> entity = new HttpEntity<String>(this.getHeaders());
            ResponseEntity<String> response = rest.exchange(endpoint
                    , HttpMethod.POST, entity, String.class);

            app.print("is success: "+response.getStatusCode().is2xxSuccessful());
            app.print("Status: "+response.getStatusCode());
            app.print("body"+response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                app.print("Message Sent");
                return new APIResponse("Message Sent", true, response.getBody());
            } else {
                app.print("Failed");
                return new APIResponse("Failed to send Message", false, response.getBody());
            }
        }catch (Exception ex){
            app.print("Unable to send Message");
            ex.printStackTrace();
            return new APIResponse(ex.getMessage(), false, null);
        }
    }
    public APIResponse sendQuickSMS(String recipient,String textMessage){
        recipient=app.getFormattedNumber(recipient);
        app.print(""+recipient);
        app.print(""+textMessage);
        app.print("id"+twilioId);
        app.print("token"+twilioToken);
        app.print("Sending...");
     try {
         Twilio.init(twilioId, twilioToken);
         Message message = Message.creator(new PhoneNumber(recipient), new PhoneNumber(twilioNumber), textMessage).create();
         if (message.getSid() != null)
             return response.success(message.getSid());
         else
             return response.failure("Unable to send SMS Message");
     }catch (Exception ex){
         ex.printStackTrace();
         return  response.failure(ex.getMessage());
     }
    }

    public APIResponse generateAndSendOTP(UserRequestWithUsername userRequest) {
        User appUser = userRepository.findByEmail(userRequest.getUsername()).orElse(userRepository.findByPhoneNumber(userRequest.getUsername()).orElse(null));
        if (appUser != null) {
            Long otp=app.generateOTP();
            memcached.save(appUser.getUuid(), String.valueOf(otp), 0);
            //send SMS
            APIResponse messengerResponse= this.sendQuickSMS(appUser.getPhoneNumber(),"Your dispatchbuddy OTP is "+otp);
             if(!messengerResponse.isSuccess())
                return response.failure("Unable to send OTP");
             else
               return response.success("OTP sent to "+appUser.getPhoneNumber());
        } else {
            return response.failure("Account not found");
        }
    }

    public APIResponse sendTeamSMS(String textMessage) {
        try {
            return response.success(true);
        }catch (Exception ex){
            ex.printStackTrace();
            return response.failure(ex.getMessage());
        }
    }
}
