package com.smart.ecommerce.queries.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionsDetailsInformationServPtalDto {
    private String transactionResult;
    private String response;
    private String date;
    private String hour;
    private String product;
    private String paymentMethod;
    private String typeSale;
    private String typeOperation;
    private String amountCharged;
    private String orderId;
    private String authorizationCode;
    private String reference;
    private String productSolution;
    private String clientId;
}
