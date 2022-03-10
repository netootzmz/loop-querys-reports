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
 * <code>MovementsMapingSumCount</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */

/**
 * Hash code.
 *
 * @return int
 */
@Data

/**
 * To string.
 *
 * @return java.lang. string
 */
@ToString
public class MovementsCommissionsMapingSumCount {

    /** no txn. */
    private Integer noTxn;

    /** rate. */
    private BigDecimal rate;

    /**
     * Creates a new instance of movements maping sum count.
     *
     * @param tc tc
     */
    public MovementsCommissionsMapingSumCount(TransactionsConciliationDto tc) {
        noTxn = 1;
        rate = getBigDecimalByDouble(tc.getPaymentBreakdown().getTotalAmountSmart());
    }

    /**
     * Creates a new instance of movements maping sum count.
     */
    public MovementsCommissionsMapingSumCount() {
        noTxn = 0;
        rate = BigDecimal.ZERO;
    }

    /**
     * Adds the.
     *
     * @param tc tc
     */
    public void add(TransactionsConciliationDto tc) {
        noTxn++;
        rate = rate.add(getBigDecimalByDouble(tc.getPaymentBreakdown().getTotalAmountSmart()));
    }

    /**
     * Merge.
     *
     * @param another another
     * @return movements maping sum count
     */
    public MovementsCommissionsMapingSumCount merge(MovementsCommissionsMapingSumCount another) {
        noTxn += another.noTxn;
        rate = rate.add(another.rate);
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
