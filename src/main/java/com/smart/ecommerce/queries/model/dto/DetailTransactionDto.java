package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

@Data
public class DetailTransactionDto {
  private String referenceNumber;
  private DetailSaleDto detailSale; 
}
