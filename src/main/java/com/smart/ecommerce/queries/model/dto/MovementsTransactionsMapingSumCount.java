/* ----------------------------------------------------------------------------
 * All rights reserved Smart Payment Services.
 *
 * This software contains information that is exclusive property of Smart,this
 * information is considered confidential.
 * It is strictly forbidden the copy or spreading of any part of this document
 * in any format, whether mechanic or electronic.
 * ---------------------------------------------------------------------------
 */
package com.smart.ecommerce.queries.model.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

/**
 * <code>MovementsTransactionsMapingSumCount</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */
@Data

/**
 * To string.
 *
 * @return java.lang. string
 */
@ToString
public class MovementsTransactionsMapingSumCount {

    /** no txn. */
    private Integer noTxn;

    /** sale. */
    private BigDecimal sale;

    /** refund. */
    private BigDecimal refund;

    /**
     * Creates a new instance of movements maping sum count.
     *
     * @param tc tc
     */
    public MovementsTransactionsMapingSumCount(TransactionsConciliationDto tc) {
        noTxn = 1;
        sale = getBigDecimalByDouble(tc.getTransactionAmount());
        refund = getBigDecimalByDouble(tc.getRefundAmount());
    }

    /**
     * Creates a new instance of movements maping sum count.
     */
    public MovementsTransactionsMapingSumCount() {
        noTxn = 0;
        sale = BigDecimal.ZERO;
        refund = BigDecimal.ZERO;
    }

    /**
     * Adds the.
     *
     * @param tc tc
     */
    public void add(TransactionsConciliationDto tc) {
        noTxn++;
        sale = sale.add(getBigDecimalByDouble(tc.getTransactionAmount()));
        refund = refund.add(getBigDecimalByDouble(tc.getRefundAmount()));
    }

    /**
     * Merge.
     *
     * @param another another
     * @return movements maping sum count
     */
    public MovementsTransactionsMapingSumCount merge(MovementsTransactionsMapingSumCount another) {
        noTxn += another.noTxn;
        sale = sale.add(another.sale);
        refund = refund.add(another.refund);
        return this;
    }

    /**
     * Gets the big decimal by double.
     *
     * @param value value
     * @return big decimal by double
     */
    private BigDecimal getBigDecimalByDouble(Double value) {
        if (value != null) {
            return BigDecimal.valueOf(value.doubleValue());
        }else {
            return BigDecimal.ZERO;
        }
    }

}
