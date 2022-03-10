package com.smart.ecommerce.queries.service;

import com.smart.ecommerce.queries.model.dto.*;
import com.smart.ecommerce.queries.util.GenericResponse;

public interface TransactionsDetailService {


    GenericResponse getTransactionsDetailDto(String idOperation, TransactionsDto transactionsDto, InfoTokenDto infoTokenDto);
    
    GenericResponse getTransactionsDetailByRespondeCodeDto(String idOperation, TransactionsByCodeDto transactionsDto, InfoTokenDto infoTokenDto);

    GenericResponse getTransactionsDetailOperation(String idOperation, TransactionsDetailOperationDto transactionsDetailOperationDto, InfoTokenDto infoTokenDto);

    GenericResponse getMovements(String idOperation, MovementsDto movementsDto, InfoTokenDto infoTokenDto);

    GenericResponse getMovementsTransactions(String idOperation, MovementsParamsDto dto, InfoTokenDto infoTokenDto);

    GenericResponse getMovementsDetail(String idOperation, TransactionsDetailOperationDto transactionsDetailOperationDto, InfoTokenDto infoTokenDto);

    GenericResponse getUserDetail(  InfoTokenDto infoTokenDto);

    GenericResponse getTransactionsConciliationByDateAt(TransactionsDetailDto dto,InfoTokenDto infoTokenDto);

    GenericResponse getReportTransactionsConciliationHeader(ParamsTransactionsConciliationDto dto ,InfoTokenDto infoTokenDto);

    GenericResponse getReportTransactionsConciliationDetail(ParamsTransactionsConciliationDto dto ,InfoTokenDto infoTokenDto);

    GenericResponse getReportTransactionsConciliedMacth(ParamsTransactionsConciliationDto dto ,InfoTokenDto infoTokenDto);

    GenericResponse getReportTransactionsConciliedNotMacth(ParamsTransactionsConciliationDto dto ,InfoTokenDto infoTokenDto);

    GenericResponse getReportTransactionsConciliedMacthHeader(ParamsTransactionsConciliationDto dto ,InfoTokenDto infoTokenDto);

    GenericResponse getReportTransactionsConciliedNotMacthHeader(ParamsTransactionsConciliationDto dto ,InfoTokenDto infoTokenDto);

    GenericResponse getClient(InfoTokenDto infoTokenDto);

    GenericResponse getStatementAccountDetail(ParamsTransactionsConciliationDto dto ,InfoTokenDto infoTokenDto);

    GenericResponse getStatementAccount(ParamsTransactionsConciliationDto dto ,InfoTokenDto infoTokenDto);

    GenericResponse getBitacoraAccountStatus(RequestBitacoraAccountStatus dto,InfoTokenDto infoTokenDto);

    GenericResponse getTransactionFeesByCardType(ParamsTransactionsConciliationDto dto ,InfoTokenDto infoTokenDto);
}
