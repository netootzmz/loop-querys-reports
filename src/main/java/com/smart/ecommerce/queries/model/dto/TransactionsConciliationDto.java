package com.smart.ecommerce.queries.model.dto;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionsConciliationDto {

  private String merchantNumber;
  private String referenceNumber;
  private String authorizationNumber;
  private String cardNumber;
  private Date createAT;
  private String createATStr;
  private String merchantName;
  private PaymentBreakdownDto paymentBreakdown;
  private StatusDto status;
  private Double transactionAmount;
  private Double refundAmount;
  private String transactionDate;
  private String transactionDateShort;
  private String folioTxn;
  private DetailDto detail;
  private Integer amountDeposited;
  private Double amountTreasury;
  private String clientId;

}
