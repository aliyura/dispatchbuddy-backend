package com.decagon.dispatchbuddy.pojos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRider<T> {
    private String name;
    private String email;
    private String phone;
    private String pickupLocation;
    private String destination;
    private String riderUuid;
    private Double payableAmount;
    private String size;
    private String distance;
}
