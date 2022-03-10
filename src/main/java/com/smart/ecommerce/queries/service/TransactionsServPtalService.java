package com.smart.ecommerce.queries.service;

import com.smart.ecommerce.queries.model.dto.InfoTokenDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailOperationDto;
import com.smart.ecommerce.queries.model.dto.TransactionsServPtalDto;
import com.smart.ecommerce.queries.util.GenericResponse;

public interface TransactionsServPtalService {
  GenericResponse getTransactionsServPtal(String idOperation, TransactionsServPtalDto dto, InfoTokenDto infoTokenDto);
  GenericResponse getTransactionsDetailOperation(String idOperation, TransactionsDetailOperationDto dto, InfoTokenDto infoTokenDto);
}
