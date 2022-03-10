package com.smart.ecommerce.queries.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.ecommerce.logging.SystemLog;
import com.smart.ecommerce.queries.contract.TransactionsServPtalContract;
import com.smart.ecommerce.queries.model.dto.InfoTokenDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailOperationDto;
import com.smart.ecommerce.queries.model.dto.TransactionsServPtalDto;
import com.smart.ecommerce.queries.service.TransactionsServPtalService;
import com.smart.ecommerce.queries.util.GenericResponse;
import com.smart.ecommerce.queries.util.InfoToken;

@RestController
@RequestMapping("TransactionsServPtalController")
public class TransactionsServPtalController implements TransactionsServPtalContract {
  
  @Autowired private TransactionsServPtalService tspService;

  @Override
  @PostMapping(value = "/getTransactionsServPtal", produces = MediaType.APPLICATION_JSON_VALUE)
  public GenericResponse getTransactionsServPtal(HttpServletRequest request,
    TransactionsServPtalDto dto) {
    InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
    String idOperation = SystemLog.newTxnIdOperation();
    return tspService.getTransactionsServPtal(idOperation, dto, infoTokenDto);
  }
  
  @Override
  @PostMapping(value = "/getTransactionsDetailServPta", produces = MediaType.APPLICATION_JSON_VALUE)
  public GenericResponse getTransactionsDetailServPta(HttpServletRequest request, @RequestBody TransactionsDetailOperationDto dto) {
      InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
      String idOperation = SystemLog.newTxnIdOperation();

      return tspService.getTransactionsDetailOperation(idOperation, dto, infoTokenDto);
  }

}
