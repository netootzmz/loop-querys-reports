package com.smart.ecommerce.queries.contract;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;

import com.smart.ecommerce.queries.model.dto.DepositsMovementsDetailParamDTO;
import com.smart.ecommerce.queries.util.GenericResponse;

import io.swagger.annotations.ApiOperation;

public interface DepositsMovementsDetailContract {
  @ApiOperation(value = "Método para obtener los depositos y movimientos por referencia de pago", notes = "")
  public GenericResponse getDepositsMovementsDto(HttpServletRequest request, @RequestBody DepositsMovementsDetailParamDTO dto);
}
