package com.smart.ecommerce.queries.model.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DepositsMovementsDetailDTO {
  private String hours;
  private String operation;
  private String referenceNumber;
  private String merchantNumber;
  private BigDecimal amount;
  private BigDecimal amountTip;
  private BigDecimal commission;
  private String cardType;
  private BigDecimal rate;
  private BigDecimal totalCommission;
  private BigDecimal ivaCommission;
  private BigDecimal totalDeposited;
}
