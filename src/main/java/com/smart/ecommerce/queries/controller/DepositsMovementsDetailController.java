package com.smart.ecommerce.queries.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.ecommerce.logging.SystemLog;
import com.smart.ecommerce.queries.contract.DepositsMovementsDetailContract;
import com.smart.ecommerce.queries.model.dto.DepositsMovementsDetailParamDTO;
import com.smart.ecommerce.queries.model.dto.InfoTokenDto;
import com.smart.ecommerce.queries.service.DepositsMovementsDetailService;
import com.smart.ecommerce.queries.util.GenericResponse;
import com.smart.ecommerce.queries.util.InfoToken;

@RestController
@RequestMapping("DepositsMovementsDetailController")
public class DepositsMovementsDetailController implements DepositsMovementsDetailContract {

  @Autowired DepositsMovementsDetailService dmdService;
  
  @Override
  @PostMapping(value = "/getDepositsMovementsDto", produces = MediaType.APPLICATION_JSON_VALUE)
  public GenericResponse getDepositsMovementsDto(HttpServletRequest request,
    DepositsMovementsDetailParamDTO dto) {
    InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
    String idOperation = SystemLog.newTxnIdOperation();
    return dmdService.getDepositsMovementsDto(idOperation, dto, infoTokenDto);
  }

}
