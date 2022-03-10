package com.smart.ecommerce.queries.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DepositsMovementsDetailParamDTO {
  private String paymentReference;
  private String paymentCode;
  private String clientId;
}
