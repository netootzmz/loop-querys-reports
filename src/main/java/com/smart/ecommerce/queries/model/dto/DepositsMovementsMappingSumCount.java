package com.smart.ecommerce.queries.model.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DepositsMovementsMappingSumCount {

  private Integer noTxn;
  private BigDecimal depositAmount;
  private BigDecimal commissionsCharged;
  private BigDecimal ivacommissions;

  public DepositsMovementsMappingSumCount(TransactionsConciliationDto tc) {
    noTxn = 1;
    depositAmount = getBigDecimalByDouble(tc.getPaymentBreakdown().getTotalAmountSmart());
    //depositAmount = getBigDecimalByDouble(tc.getTransactionAmount())
    commissionsCharged = getBigDecimalByDouble(tc.getPaymentBreakdown().getSmartCommission());
    ivacommissions = getBigDecimalByDouble(tc.getPaymentBreakdown().getSmartIvaCommission());
  }

  public DepositsMovementsMappingSumCount() {
    noTxn = 0;
    depositAmount = BigDecimal.ZERO;
    commissionsCharged = BigDecimal.ZERO;
    ivacommissions = BigDecimal.ZERO;
  }

  public void add(TransactionsConciliationDto tc) {
    noTxn++;
    depositAmount = depositAmount.add(getBigDecimalByDouble(tc.getPaymentBreakdown().getTotalAmountSmart()));
    //depositAmount = depositAmount.add(getBigDecimalByDouble(tc.getTransactionAmount()));
    commissionsCharged = commissionsCharged.add(getBigDecimalByDouble(tc.getPaymentBreakdown().getSmartCommission()));
    ivacommissions = ivacommissions.add(getBigDecimalByDouble(tc.getPaymentBreakdown().getSmartIvaCommission()));
  }

  public DepositsMovementsMappingSumCount merge(DepositsMovementsMappingSumCount another) {
    noTxn += another.noTxn;
    depositAmount = depositAmount.add(another.depositAmount);
    commissionsCharged = commissionsCharged.add(another.commissionsCharged);
    ivacommissions = ivacommissions.add(another.ivacommissions);
    return this;
  }

  private BigDecimal getBigDecimalByDouble(Double value) {
    if (value != null) {
      return BigDecimal.valueOf(value.doubleValue());
    } else {
      return BigDecimal.ZERO;
    }
  }

}
