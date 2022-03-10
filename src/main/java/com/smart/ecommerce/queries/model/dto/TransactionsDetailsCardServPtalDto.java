package com.smart.ecommerce.queries.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionsDetailsCardServPtalDto {
    private String cardType;
    private String clientBank;
    private String cardBrand;
    private String fourDigitCard;
}
