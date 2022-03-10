package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

@Data
public class PaymentBreakdownDto {

	private Double bankCommission;
	private Double bankIvaCommission;
	private Double smartCommission;
	private Double commissionSmart;
	private Double smartIvaCommission;
	private Double totalAmountSmart;
	private Double total;
	private Double amountDepositedSmart;

}
