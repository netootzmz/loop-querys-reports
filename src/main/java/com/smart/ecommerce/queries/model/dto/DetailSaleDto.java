package com.smart.ecommerce.queries.model.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DetailSaleDto {
  private BigDecimal discountRate;
  private BigDecimal ivaDiscountRate;
  private BigDecimal totalSale;
  private BigDecimal paymentSale;
}
