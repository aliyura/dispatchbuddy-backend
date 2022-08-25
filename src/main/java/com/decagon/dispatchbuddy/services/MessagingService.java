package com.decagon.dispatchbuddy.services;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.pojos.APIResponse;
import com.decagon.dispatchbuddy.pojos.GmailDTO;
import com.decagon.dispatchbuddy.pojos.UserRequestWithUsername;
import com.decagon.dispatchbuddy.repositories.UserRepository;
import com.decagon.dispatchbuddy.util.App;
import com.decagon.dispatchbuddy.util.Response;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
    @Value("${twilio.id}")
    private String twilioId;
    @Value("${twilio.token}")
    private String twilioToken;
    @Value("${twilio.number}")
    private String twilioNumber;
    @Value("bulksmsnigeria.baseURL")
    private String smsBaseURL;
    @Value("bulksmsnigeria.smsToken")
    private String smsToken;

//    @Value("whatsapp.accessKey")
    private String whatsappAccessKey="Bearer EAAKh4CBRdIIBAP9lVEtOhb7uUs0oFhLuFSs5BvVEfdqzM29eZBWsrC4HL5ahh7YBuiZAr3txYUZA2EdZCUg0klVkNvkblK6UvAPaxjb4vdT9r9gs8gUNA7ZC65h5EdZCXrtxLjtLgWKZC9VDZBpycVfydkD3iU98eR7iOnQmZBEr7cthKgffaa1xz";
//    @Value("whatsapp.baseURL")
    private String whatsappBaseURL="https://graph.facebook.com/v14.0/101883642540294/messages";


    @Autowired  @Qualifier("gmail")
    private JavaMailSender mailSender;
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

    public APIResponse sendWhatsappMessage(String recipient, String textMessage) {
        recipient=app.getFormattedNumber(recipient).substring(1);
        app.print(""+recipient);
        app.print(""+textMessage);
        app.print("Whatsapp Message Sending...");
        try {

            String requestBody="{\"messaging_product\":\"whatsapp\",\"to\":\""+recipient+"\",\"type\":\"template\",\"template\":{\"name\":\"dispatch_buddy_access_token\",\"language\":{\"code\":\"en_US\"},\"components\":[{\"type\":\"body\",\"parameters\":[{\"type\":\"text\",\"text\":\""+textMessage+"\"}]}]}}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization",whatsappAccessKey);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = rest.exchange(whatsappBaseURL, HttpMethod.POST, entity, String.class);
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
//            APIResponse messengerResponse= this.sendWhatsappMessage(appUser.getPhoneNumber(),otp.toString());
            //Send Mail
            GmailDTO mail = GmailDTO.builder()
                    .subject("VERIFICATION OTP")
                    .body(otp.toString())
                    .toAddresses(appUser.getEmail())
                    .build();
            APIResponse messengerResponse= this.sendMail(mail);

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



    public APIResponse sendMail(GmailDTO gmailDTO) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(gmailDTO.getToAddresses());
            message.setFrom("dispatchbuddy@gmail.com");
            message.setSubject(gmailDTO.getSubject());
            message.setText(gmailDTO.getBody(), true);
        };

        try {
            mailSender.send(preparator);
            String sent = "Email sent successfully To "+gmailDTO.getToAddresses();
            return  response.success(sent);
        } catch (MailException e) {
            return response.failure("Mail not send because: "+e.getMessage());
        }
    }
}
