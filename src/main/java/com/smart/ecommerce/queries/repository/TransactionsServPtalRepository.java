package com.smart.ecommerce.queries.repository;

import java.util.List;

import com.smart.ecommerce.queries.model.dto.ClientAffiliationResponseDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailOperationDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsServPtalDto;

public interface TransactionsServPtalRepository {
  List<ClientAffiliationResponseDto> getTransactionsServPtalRepository(TransactionsServPtalDto transactionsDto);
  List<ClientAffiliationResponseDto> getTransactionsServPtalWithSP(TransactionsServPtalDto dto);
  TransactionsDetailsServPtalDto getTransactionsDetailServPtalRepository(TransactionsDetailOperationDto dto);
  TransactionsDetailsServPtalDto getTransactionsDetailServPtalWithSP(TransactionsDetailOperationDto dto);

  String getSaleRate(String clientId, String cardType);
}
