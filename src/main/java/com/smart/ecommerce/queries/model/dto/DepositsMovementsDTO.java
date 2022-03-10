package com.smart.ecommerce.queries.model.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DepositsMovementsDTO {
  private BigDecimal previousBalance;
  private BigDecimal depositAmount;
  private BigDecimal depositedAmount;
  private BigDecimal newBalance;
  private BigDecimal processedTransactions;
  private BigDecimal commissionsCharged;
  private BigDecimal depositAmountByTxn;
  private BigDecimal chargebacks;
  private BigDecimal tips;
  private BigDecimal ivacommissions;
  private BigDecimal adjustment;
  private BigDecimal others;
  private List<DepositsMovementsDetailDTO> details;
}
