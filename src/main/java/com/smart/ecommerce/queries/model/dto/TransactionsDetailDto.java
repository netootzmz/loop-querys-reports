package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TransactionsDetailDto {


    public Date initDate;
    public String token;
    public Date endDate;
    public String initHour;
    public String endHour;
    public String hierarchyId;
    public Integer productId;
    public Integer operationTypeId;
    public Double saleAmount;
    public Integer bankCardBin;
    public Integer bankCardTypeId;
    public Integer paymentLinkStatusId;
    public Integer paymentStatusId;




}
