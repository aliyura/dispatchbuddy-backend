package com.decagon.dispatchbuddy.pojos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddLocationRequest<T> {
    private String []locations;
}
