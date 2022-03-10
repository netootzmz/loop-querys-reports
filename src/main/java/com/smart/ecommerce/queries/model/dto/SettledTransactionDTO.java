package com.smart.ecommerce.queries.model.dto;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SettledTransactionDTO {
  private String settlementId;
  private String referenceNumber;
  private String merchantNumber;
  private Date createdAt;
  private String createdAtStr;
  private Integer cardType;
  private Long acquirerCommission;
  private Double amountToSettled;
  private Integer transactionConcept;
  private Boolean verifiedTransaction;
  private String folio;
  private Double smartCommission;
  private Double iva;
  private Double dispersed;
  private Double transactionAmount;
  private Double transactionFee;
  private String cardNumber;
  private String authorizationNumber;
  private Long transactionType;
  
  public String getOperation() {
    switch (transactionConcept) {
      case 1:
        return "Venta";
      case 4:
        return "Devolucion";

      default:
        return "Not Operation";
    }
  }
  
  public String getCardTypeName() {
    switch (cardType) {
      case 1:
        return "Debito";

      case 2:
        return "Credito";
        
      default:
        return "Not Card Type";
    }
  }
  
}
