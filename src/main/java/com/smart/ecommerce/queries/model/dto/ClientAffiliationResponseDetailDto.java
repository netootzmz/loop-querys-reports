package com.smart.ecommerce.queries.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientAffiliationResponseDetailDto {

    private String paymentDate;
    private String paymentHour;
    private String hierarchyId;
    private String linkDate;
    private String linkHour;
    private String linkConcept;
    private String cancelConcept;
    private Integer linkEstatus;
    private Integer linkUser;
    private Integer operationTypeId;
    private String operationType;
    private Double amount;
    private String paymentStatus;
    private Double monthsWithoutInterest;
    private String authorizerReplyMessage;
    private String autCode;
    private String returnOperation;
    private String folioTxn;
    private String cardNumber;
    private String userName;
    private String lastName;
    private String tradeReference;
    private String cardBrand;
    private String linkEstatusDescription;
    private String refSgNumber;
    private String refSpNumber;
    private String referenceNumber;
    private String authorizationNumber;
    private String cardType;
    private String cveMsi;
    private String subTotal;
    private String transactionDate;
    private String transactionHour;
    private String amountStr;
    private String address;
    private String email;
    private String phone;
    private String clientName;

    private Integer haveTip;

    private String amountTip;

//    'public' String getCardTypeName() '{'
//        String cardTypeName = ""
//        'if' (cardType.equalsIgnoreCase("1")) '{'
//            cardTypeName = "credit card"
//        '}''else if' (cardType.equalsIgnoreCase("2")) '{'
//            cardTypeName = "Debit"
//        '}'
//        return cardTypeName
//    '}'

}
