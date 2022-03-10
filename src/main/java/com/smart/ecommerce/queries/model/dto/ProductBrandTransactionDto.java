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
 * <code>ProductBrandTransaction</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */

/**
 * Creates a new instance of product brand transaction.
 */
@Data

/**
 * To string.
 *
 * @return java.lang. string
 */
@ToString
public class ProductBrandTransactionDto {

    /** invoiced amunt. */
    private BigDecimal invoicedAmount;

    /** number duly authorized payments. */
    private BigDecimal numberDulyAuthorizedPayments;

    /** exchange fee. */
    private BigDecimal exchangeFee; /* % de la transaccion */

    /** discount rate. */
    private BigDecimal discountRate; /* % de la transaccion */

    /** amount charged discount rate. */
    private BigDecimal amountChargedDiscountRate;

    /** penalties. */
    private BigDecimal penalties; /* cuotas por otros conceptos */

    /** total amount card payment reception service. */
    private BigDecimal totalAmountCardPaymentReceptionService;
}
