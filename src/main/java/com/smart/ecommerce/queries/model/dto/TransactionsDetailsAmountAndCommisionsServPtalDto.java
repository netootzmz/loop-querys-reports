package com.smart.ecommerce.queries.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionsDetailsAmountAndCommisionsServPtalDto {
    private String transactionAmount;
    private String baseRateCommission;
    private String totalCommission;
    private String commissionOnRate;
    private String tax;
}
