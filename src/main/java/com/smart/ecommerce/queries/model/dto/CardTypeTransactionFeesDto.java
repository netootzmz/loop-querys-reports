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

import lombok.Data;
import lombok.ToString;

/**
 * <code>CardTypeTransactionFeesDto</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */

/**
 * Creates a new instance of card type transaction fees dto.
 */
@Data

/**
 * To string.
 *
 * @return java.lang. string
 */
@ToString
public class CardTypeTransactionFeesDto {

    /** card type. */
    private String cardType;

    /** product brand. */
    private ProductBrandDto productBrand;

    /**
     * Gets the card type name.
     *
     * @return card type name
     */
    public String getCardTypeName() {
        String cardTypeName = "";
        if (cardType.equalsIgnoreCase("1")) {
            cardTypeName = "credit card";
        }else if (cardType.equalsIgnoreCase("2")) {
            cardTypeName = "Debit";
        }
        return cardTypeName;
    }
}
