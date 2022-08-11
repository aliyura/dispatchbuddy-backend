package com.decagon.dispatchbuddy.pojos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest<T> {
    private String pickupLocation;
    private String destination;
    private Double payableAmount;
    private String size;
    private String distance;
}
