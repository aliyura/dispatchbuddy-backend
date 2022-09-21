package com.decagon.dispatchbuddy.pojos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GmailDTO {
    private String subject;
    private String toEmail;
    private String body;
}
