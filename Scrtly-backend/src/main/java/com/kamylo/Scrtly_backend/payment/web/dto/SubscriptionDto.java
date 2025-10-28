package com.kamylo.Scrtly_backend.payment.web.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {
    private Long id;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime currentPeriodEnd;
}
