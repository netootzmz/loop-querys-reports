package com.smart.ecommerce.queries.repository;

import com.smart.ecommerce.queries.model.dto.*;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionsDetailRepository {


    public List<ClientAffiliationResponseDto> getTransactionsDetailRepository(TransactionsDto transactionsDto);
    
    List<ClientAffiliationResponseDto> getTransactionsDetailRepositoryByResponseCode(TransactionsByCodeDto transactionsDto);

    public List<ClientAffiliationResponseDetailDto> getTransactionsDetailOperation(TransactionsDetailOperationDto transactionsDetailOperationDto);
    //public List<ClientAffiliationResponseDto> getTransactionsDetailOperation(TransactionsDetailOperationDto transactionsDetailOperationDto)

    public List<ClientAffiliationResponseDto> getMovements(MovementsDto movementsDto);

    public List<ClientAffiliationResponseDto> getMovementsDetail(TransactionsDetailOperationDto transactionsDetailOperationDto);

    public List<UserDetailDto> getUserDetail(Integer userId);

    List<ParamsTransactionsConciliationDto> getMerchantNumberByRfc(/*MovementsDto*/ MovementsParamsDto dto);

    BigDecimal getFeeCommissions(String rfc, Integer idPeriodFee , Integer idFee);
    
    Integer getNextValSeq();


}
