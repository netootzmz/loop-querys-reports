package com.smart.ecommerce.queries.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ParamsTransactionsConciliationDto {
  private Date startDate;   
  private Date endDate;
  private String merchantNumber;
  private String token;
  private String action;
}
