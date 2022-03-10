package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

@Data
public class TransactionsConciliationHeaderDto {
	private String merchantNumber;
	private Integer totalEntry;
	private Integer totalAmount;
}
