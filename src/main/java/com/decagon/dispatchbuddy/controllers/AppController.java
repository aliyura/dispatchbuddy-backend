package com.decagon.dispatchbuddy.controllers;
import com.decagon.dispatchbuddy.pojos.APIResponse;
import com.decagon.dispatchbuddy.services.MessagingService;
import com.decagon.dispatchbuddy.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping(path = "api")
public class AppController {

    private final MessagingService messagingService;

    @GetMapping("/ping")
    public APIResponse<String> ping() {
        messagingService.sendWhatsappMessage("+2348064160204","1234");
        return new APIResponse<String>("success", true, "I am alive");
    }

    @PostMapping(value="/test", produces = {MediaType.APPLICATION_XML_VALUE}, consumes = { MediaType.ALL_VALUE})
    public String test(@RequestBody String path) {
        String customerID = path.substring(path.indexOf("CustomerID") + 11, path.lastIndexOf("CustomerID")-6).trim();
        return customerID;
    }
}