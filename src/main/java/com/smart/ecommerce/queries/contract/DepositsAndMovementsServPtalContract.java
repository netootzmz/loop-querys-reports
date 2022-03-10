package com.smart.ecommerce.queries.contract;

import com.smart.ecommerce.queries.model.dto.DepositsAndMovementsServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailOperationDto;
import com.smart.ecommerce.queries.model.dto.TransactionsServPtalDto;
import com.smart.ecommerce.queries.util.GenericResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public interface DepositsAndMovementsServPtalContract {
  @ApiOperation(value = "Servicio para obtener los depositos y movimientos a partir de cierta información " +
          "para el portal de servicios", notes = "")
  public GenericResponse getDepositsAndMovementsServPtal(HttpServletRequest request, @RequestBody DepositsAndMovementsServPtalDto dto);
}
