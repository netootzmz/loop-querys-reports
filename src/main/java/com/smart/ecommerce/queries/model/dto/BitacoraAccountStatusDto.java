package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BitacoraAccountStatusDto {
    private Integer bitacoraAccountStatusId;
    private LocalDateTime initDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private Integer statusId;
    private Integer sendStatusId;
    private String urlFile;
    private Integer idMembership;
}
