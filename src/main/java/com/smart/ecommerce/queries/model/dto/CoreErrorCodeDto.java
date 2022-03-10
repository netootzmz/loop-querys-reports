package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CoreErrorCodeDto {

  private Integer error_code_id;
  private String code;
  private String message;
  private Integer status;
  private String user_admission;
  private Timestamp date_admission;
  private String user_change;
  private Timestamp date_change;

}
