package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

@Data
public class MovementsResponseDto {
  private String dateTransaction;
  private DetailTransactionDto detailTransaction;
}
