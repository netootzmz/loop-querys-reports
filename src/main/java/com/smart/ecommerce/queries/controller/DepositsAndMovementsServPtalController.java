package com.smart.ecommerce.queries.controller;

import com.smart.ecommerce.logging.SystemLog;
import com.smart.ecommerce.queries.contract.DepositsAndMovementsServPtalContract;
import com.smart.ecommerce.queries.model.dto.DepositsAndMovementsServPtalDto;
import com.smart.ecommerce.queries.model.dto.InfoTokenDto;
import com.smart.ecommerce.queries.service.DepositsAndMovementsServPtalService;
import com.smart.ecommerce.queries.service.TransactionsServPtalService;
import com.smart.ecommerce.queries.util.GenericResponse;
import com.smart.ecommerce.queries.util.InfoToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("DepositsAndMovementsServPtalController")
public class DepositsAndMovementsServPtalController implements DepositsAndMovementsServPtalContract {
  
  @Autowired private DepositsAndMovementsServPtalService depositsAndMovementsServPtalService;

  @Override
  @PostMapping(value = "/getDepositsAndMovementsServPtal", produces = MediaType.APPLICATION_JSON_VALUE)
  public GenericResponse getDepositsAndMovementsServPtal(HttpServletRequest request,
    DepositsAndMovementsServPtalDto dto) {
    InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
    String idOperation = SystemLog.newTxnIdOperation();

    return depositsAndMovementsServPtalService.getDepositsAndMovementsServPtal(idOperation, dto, infoTokenDto);
  }
}
