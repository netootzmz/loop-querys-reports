package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

@Data
public class StatusDto {
	
	private Boolean concilied;
	private String conciliedAt;
	private String processed;
	private String processedAt;
	
}
