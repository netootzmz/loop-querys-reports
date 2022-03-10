package com.smart.ecommerce.queries.service;

import java.util.List;

import com.smart.ecommerce.queries.model.dto.DepositsMovementsDTO;
import com.smart.ecommerce.queries.model.dto.DepositsMovementsDetailParamDTO;
import com.smart.ecommerce.queries.model.dto.InfoTokenDto;
import com.smart.ecommerce.queries.model.dto.SettledTransactionDTO;
import com.smart.ecommerce.queries.model.dto.TransactionsConciliationDto;
import com.smart.ecommerce.queries.util.GenericResponse;

public interface DepositsMovementsDetailService {
  GenericResponse getDepositsMovementsDto(String idOperation, DepositsMovementsDetailParamDTO dto, InfoTokenDto infoTokenDto);
  DepositsMovementsDTO getDepositsMovements(DepositsMovementsDetailParamDTO dto);
  List<SettledTransactionDTO> getSettledTransaction(DepositsMovementsDetailParamDTO dto); 
  List<TransactionsConciliationDto> getTransactionsConciliation(List<String> reference); 
}
