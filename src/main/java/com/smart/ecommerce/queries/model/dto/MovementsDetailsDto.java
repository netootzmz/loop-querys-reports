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
 * <code>MovementsDetailsDto</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */

/**
 * Creates a new instance of movements details dto.
 */
@Data

/**
 * To string.
 *
 * @return java.lang. string
 */
@ToString
public class MovementsDetailsDto {

    /** name. */
    private String name;

    /** total. */
    private BigDecimal total;
}
