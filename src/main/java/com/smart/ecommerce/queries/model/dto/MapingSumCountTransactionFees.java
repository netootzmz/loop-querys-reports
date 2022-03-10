package com.smart.ecommerce.queries.model.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MapingSumCountTransactionFees {
    private Integer noTxn;
    private BigDecimal invoicedAmount;
    private BigDecimal chargedDiscountRate;
    private BigDecimal cardPaymentService;
    /*Variables por definir*/
    /*
     * private BigDecimal exchangeFee
     * private BigDecimal discountRate
     * private BigDecimal penalties
     *
     * */

    public MapingSumCountTransactionFees(TransactionsConciliationDto tc) {
        noTxn = 1;
        invoicedAmount = getBigDecimalByDouble(tc.getTransactionAmount());
        chargedDiscountRate = getBigDecimalByDouble(tc.getPaymentBreakdown().getCommissionSmart());
        cardPaymentService = getBigDecimalByDouble(tc.getPaymentBreakdown().getTotalAmountSmart());
    }

    public MapingSumCountTransactionFees() {
        noTxn = 0;
        invoicedAmount = BigDecimal.ZERO;
        chargedDiscountRate= BigDecimal.ZERO;
        cardPaymentService= BigDecimal.ZERO;
    }

    public void add(TransactionsConciliationDto tc) {
        noTxn++;
        invoicedAmount = invoicedAmount.add(getBigDecimalByDouble(tc.getTransactionAmount()));
        chargedDiscountRate = chargedDiscountRate.add(getBigDecimalByDouble(tc.getPaymentBreakdown().getCommissionSmart()));
        cardPaymentService = cardPaymentService.add(getBigDecimalByDouble(tc.getPaymentBreakdown().getTotalAmountSmart()));
    }

    public MapingSumCountTransactionFees merge(MapingSumCountTransactionFees another) {
        noTxn += another.noTxn;
        invoicedAmount = invoicedAmount.add(another.invoicedAmount);
        chargedDiscountRate = chargedDiscountRate.add(another.chargedDiscountRate);
        cardPaymentService = cardPaymentService.add(another.cardPaymentService);
        return this;
    }

  /*
  'private' BigDecimal getBigDecimalByInteger(Integer value) '{'
    'if' (value != null) '{'
      return BigDecimal.valueOf(value.doubleValue())
    '}''else' '{'
      return BigDecimal.ZERO
    '}'
  }
  */

    private BigDecimal getBigDecimalByDouble(Double value) {
        if (value != null) {
            return BigDecimal.valueOf(value.doubleValue());
        }else {
            return BigDecimal.ZERO;
        }
    }

}
