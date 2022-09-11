package com.decagon.dispatchbuddy.pojos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RatingDto {
    private String comment;
    private Double rating;
    private String requestUuid;
    private String uuid;
}
