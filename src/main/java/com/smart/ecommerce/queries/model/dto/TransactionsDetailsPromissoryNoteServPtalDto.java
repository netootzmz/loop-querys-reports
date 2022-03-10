package com.smart.ecommerce.queries.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionsDetailsPromissoryNoteServPtalDto {
    private String urlLogo;
    private String merchantName;
    private String optionalMessage;
    private String address;
    private String saleAmount;
    private String tippingAmount;
    private Boolean hasTipping;
    private String totalAmount;
    private String statusPromissoryNote;
    private String authorizationNumber;
    private String merchantNumber;
    private String transactionType;
    private String payDate;
    private String referenceNumber;
    private String folio;
    private String cardNumber;
    private String cardIssuer;
    private String acquirer;
    private String eci;
    private String cardType;
    private String product;
    private String cardTypeId;
    private String typeCurrency;
}
