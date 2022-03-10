package com.smart.ecommerce.queries.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionsByCodeDto {
  
  @JsonProperty(value = "startDate", required = true)
  @ApiModelProperty(name = "startDate", required = true,
          value = "Represents the startDate.", example = "1990-01-01")
  private String startDate;
  
  @JsonProperty(value = "endDate", required = true)
  @ApiModelProperty(name = "endDate", required = true,
          value = "Represents the endDate.", example = "1990-12-31")
  private String endDate;
  
  @JsonProperty(value = "saleAmount", required = false)
  @ApiModelProperty(name = "saleAmount", required = false)
  private Double saleAmount;
  
  @JsonProperty(value = "cardNumber", required = true)
  @ApiModelProperty(name = "cardNumber", required = true,
          value = "Represents the folio card number.", example = "491674******1631")
  public String cardNumber;
  
  @JsonProperty(value = "folioTxn", required = true)
  @ApiModelProperty(name = "folioTxn", required = true,
          value = "Represents the folio Transaction.", example = "FOL_00001")
  public String folioTxn;
  
  @JsonProperty(value = "token", required = true)
  @ApiModelProperty(name = "token", required = true,
          value = "Represents the token.", example = "eyJhbGciOi")
  public String token;

}
