package com.decagon.dispatchbuddy.pojos;
import com.decagon.dispatchbuddy.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginResponse {
    private String bearer;
    private User user;
}
