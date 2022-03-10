package com.smart.ecommerce.queries.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionsDetailsClientServPtalDto {
    private String email;
    private String phone;
    private String clientName;
    private String address;
    private Double amount;
}
