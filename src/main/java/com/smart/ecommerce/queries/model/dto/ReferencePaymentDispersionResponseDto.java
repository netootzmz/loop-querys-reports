package com.smart.ecommerce.queries.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@ToString
public class ReferencePaymentDispersionResponseDto {

    private Long referencePaymentDispersionId;

    private int dispersionTrackingId;

    private int dispersionStatusId;

    private String dispersionStatusCve;
    private String clabe;
    private String paymentReference;

    private String description;

    private String commerce;

    private int amount;

    private String dispersion_date;

    private String payment_code;

    private String payment_detail;

    private int statusId;

    private String userByRegister;

    private LocalDateTime createdAt;
}
