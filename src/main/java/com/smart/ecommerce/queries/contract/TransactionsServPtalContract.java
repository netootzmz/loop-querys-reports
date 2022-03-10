package com.smart.ecommerce.queries.contract;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;

import com.smart.ecommerce.queries.model.dto.TransactionsDetailOperationDto;
import com.smart.ecommerce.queries.model.dto.TransactionsServPtalDto;
import com.smart.ecommerce.queries.util.GenericResponse;

import io.swagger.annotations.ApiOperation;

public interface TransactionsServPtalContract {
  @ApiOperation(value = "Método para obtener las transacciones del portal de servicios", notes = "")
  public GenericResponse getTransactionsServPtal(HttpServletRequest request, @RequestBody TransactionsServPtalDto dto);
  
  @ApiOperation(value = "Método para obetener detalle de las tranasacciones mediente el folio de la transaccion", notes = "folioTxn")
  public GenericResponse getTransactionsDetailServPta(HttpServletRequest request, @RequestBody TransactionsDetailOperationDto dto);
}
