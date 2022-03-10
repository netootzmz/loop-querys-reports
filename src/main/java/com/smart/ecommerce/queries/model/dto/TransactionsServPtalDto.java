package com.smart.ecommerce.queries.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionsServPtalDto {
  @JsonProperty(value = "approval", required = false)
  @ApiModelProperty(name = "approval", required = false,
          value = "Represents the approval.",
          example = "123456")
  private String approval;

  @JsonProperty(value = "cardBrand", required = false)
  @ApiModelProperty(name = "cardBrand", required = false,
          value = "Represents the card brand : VISA, MASTERCARD, AMERICAN EXPRESS.",
          example = "VISA")
  private String cardBrand;

  @JsonProperty(value = "cardType", required = false)
  @ApiModelProperty(name = "cardType", required = false,
          value = "Represents the card type: 1.- credit card, 2.- debit.",
          example = "1")
  private String cardType;

  @JsonProperty(value = "transmitter", required = false)
  @ApiModelProperty(name = "transmitter", required = false,
          value = "Represents the transmitter.",
          example = "BANCOMER")
  private String transmitter;

  @JsonProperty(value = "endDate", required = false)
  @ApiModelProperty(name = "endDate", required = false,
    value = "Represents the end date transaction",
    example = "2021-12-31")
  private String endDate;

  @JsonProperty(value = "endHour", required = false)
  @ApiModelProperty(name = "endHour", required = false,
          value = "Represents the end hour in the format 24 hrs.",
          example = "23:59:59")
  private String endHour;

  @JsonProperty(value = "initDate", required = false)
  @ApiModelProperty(name = "initDate", required = false,
    value = "Represents the init date transaction",
    example = "2021-01-01")
  private String initDate;

  @JsonProperty(value = "initHour", required = false)
  @ApiModelProperty(name = "initHour", required = false,
          value = "Represents the init hour in the format 24 hrs.",
          example = "00:00:00")
  private String initHour;

  @JsonProperty(value = "operationTypeId", required = false)
  @ApiModelProperty(name = "operationTypeId", required = false,
          value = "Represents the operation type id like type transaction id.",
          example = "2")
  private Integer operationTypeId;

  @JsonProperty(value = "paymentReference", required = false)
  @ApiModelProperty(name = "paymentReference", required = false,
          value = "Represents the payment reference.",
          example = "000012345678")
  private String paymentReference;

  @JsonProperty(value = "productId", required = false)
  @ApiModelProperty(name = "productId", required = false,
          value = "Represents the product id like type smartlink.",
          example = "1")
  private Integer productId;

  @JsonProperty(value = "saleAmount", required = false)
  @ApiModelProperty(name = "saleAmount", required = false)
  private Double saleAmount;

  @JsonProperty(value = "token", required = false)
  @ApiModelProperty(name = "token", required = false)
  private String token;
  
  @JsonProperty(value = "eci", required = false)
  @ApiModelProperty(name = "eci", required = false)
  private String eci;
  
  @JsonProperty(value = "codeMsi", required = false)
  @ApiModelProperty(name = "codeMsi", required = false,
    value = "Represents the code months without interest.",
    example = "3")
  private String codeMsi;
  
  @JsonProperty(value = "responseCode", required = false)
  @ApiModelProperty(name = "responseCode", required = false,
    value = "Represents the response code.",
    example = "00")
  private String responseCode;
  
  @JsonProperty(value = "cardNumberEnd", required = false)
  @ApiModelProperty(name = "cardNumberEnd", required = false,
    value = "Represents the card number end.",
    example = "00")
  private String cardNumberEnd;
  
  @JsonProperty(value = "inputMode", required = false)
  @ApiModelProperty(name = "inputMode", required = false,
    value = "Represents the input mode.",
    example = "00")
  private Integer inputMode;
  
  @JsonProperty(value = "hierarchyLevelId", required = false)
  @ApiModelProperty(name = "hierarchyLevelId", required = false,
    value = "Represents the hierarchy level id.",
    example = "2")
  private Integer hierarchyLevelId;
  
  @JsonProperty(value = "transactionStartDate", required = false)
  @ApiModelProperty(name = "transactionStartDate", required = false,
    value = "Represents the transaction start date",
    example = "2021-01-01")
  private String transactionStartDate;
  
  @JsonProperty(value = "transactionEndDate", required = false)
  @ApiModelProperty(name = "transactionEndDate", required = false,
    value = "Represents the transaction end date",
    example = "2021-12-31")
  private String transactionEndDate;
  
  @JsonProperty(value = "merchant", required = false)
  @ApiModelProperty(name = "merchant", required = false,
    value = "Represents the merchant .",
    example = "8494578")
  private String merchant;

  @JsonProperty(value = "authorizerStatus", required = false)
  @ApiModelProperty(name = "authorizerStatus", required = false,
    value = "Represents the authorizerStatus .",
    example = "8494578")
  private String authorizerStatus;

  @JsonProperty(value = "posEntryModeId", required = false)
  @ApiModelProperty(name = "posEntryModeId", required = false,
    value = "Represents the id entry mode 0 - Deslizada, 1 - Manual.",
    example = "1")
  private Integer posEntryModeId;
  
  /*
  @JsonProperty(value = "referenceNumber", required = false)
  @ApiModelProperty(name = "referenceNumber", required = false,
    value = "Represents the reference number",
    example = "8494578")
  private String referenceNumber
  */
  
}
