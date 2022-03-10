package com.smart.ecommerce.queries.model.dto;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientAffiliationResponseDto {

  private String paymentDate;

  private String paymentHour;

  private String hierarchyId;

  private String hierarchyName;

  private String transactionDate;

  private String transactionHour;

  private String merchantNumber;

  private String transmitter;

  private Integer emisorId;

  private String referenceNumber;

  private String cardNumber;

  private String cardBrand;

  private Integer operationTypeId;

  private String operationType;

  private String amountStr;

  //private Double amount

  private String paymentStatus;

  private Double monthsWithoutInterest;

  private String authorizerReplyMessage;

  private String returnOperation;

  private String folioTxn;

  private Integer productId;

  private String productDescription;

  private String refSgNumber;

  private String refSpNumber;

  private List<TransactionsConciliationDto> transactionsConciliation;

  private String approvalCode;

  private Double amountTip;

  private String posEntryMode;
  private String cardType;
  private String cardTypeId;

  /*
  'public' 'void' setAmount(Double amount) '{'
    this.amount = amount
  '}'
   */

  
  public String getCardTypeName() {
    String cardTypeName = "";
    if (cardTypeId != null) {
        switch (cardTypeId) {
            case "1":
                cardTypeName = "DÉBITO";
                break;
            case "2":
                cardTypeName = "CRÉDITO";
                break;

            default:
              cardTypeName = "No SE ENCONTRO TIPO DE TARJETA";
                break;
        }
    }
    return cardTypeName;
}
  
  public Double getAmount() {
    Double amount = 0D;
    try {
      if (StringUtils.isNotBlank(amountStr) || StringUtils.isNotEmpty(amountStr)) {
        String amountR = amountStr.replace(",", "");
        amount = Double.valueOf(amountR);
      } else {
        amount = 0D;
      }
    } catch (Exception e) {
      amount = 0D;
    }
    return amount;
  }

  public Integer getPosEntryModeId() {
    
    if (posEntryMode!=null) {
      switch (posEntryMode.toUpperCase()) {
        case "MANUAL":
          return 1;
        
        case "DESLIZADA":
          return 2;

        case "INSERTADA":
          return 3;

        case "FALLBACK":
          return 4;
          
        case "CODIGO DE BARRAS":
          return 5;

        case "COMERCIO ELECTRONICO":
          return 6;
          
        case "CONTACTLESS DESLIZADA":
          return 7;
          
        case "CONTACTLESS CHIP":
          return 8;
          
        case "EN LINEA":
          return 9;
          
        case "TAG NFC":
          return 10;
          
        case "QR":
          return 11;
          
        default:
          return 0;
      }
    }else {
      return 0;
    }
    
  }

}
