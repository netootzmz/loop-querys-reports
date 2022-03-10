package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

@Data
public class StatementAccountDto {
  private String merchantNumber;
  private String dateTransaction;
  private DetailSaleDto detailSale; 
}
