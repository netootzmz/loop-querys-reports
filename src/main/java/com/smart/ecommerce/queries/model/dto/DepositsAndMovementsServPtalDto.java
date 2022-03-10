package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DepositsAndMovementsServPtalDto {
    public String startDate;
    public String endDate;
    public String groupId;
    public String grouperId;
    public String reasonSocialId;
    public String branchId;
    public String paymentReference;
    public String clabe;
    public String rfc;
}
