package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RequestBitacoraAccountStatus {
    private Date initDate;
    private Date endDate;
    private Integer idMembership;
}
