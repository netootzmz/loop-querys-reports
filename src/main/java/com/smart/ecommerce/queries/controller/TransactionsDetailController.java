package com.smart.ecommerce.queries.controller;


import com.smart.ecommerce.logging.SystemLog;
import com.smart.ecommerce.queries.contract.TransactionsDetailContract;
import com.smart.ecommerce.queries.model.dto.*;
import com.smart.ecommerce.queries.service.TransactionsDetailService;
import com.smart.ecommerce.queries.util.GenericResponse;
import com.smart.ecommerce.queries.util.InfoToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("TransactionsDetailController")
public class TransactionsDetailController implements TransactionsDetailContract {

    @Autowired
    private TransactionsDetailService transactionsDetailService;


    @Override
    @PostMapping(value = "/getTransactionsDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getTransactionsDetail(HttpServletRequest request, @RequestBody TransactionsDto transactionsDto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        String idOperation = SystemLog.newTxnIdOperation();

        return transactionsDetailService.getTransactionsDetailDto(idOperation, transactionsDto, infoTokenDto);
    }
    
    @Override
    @PostMapping(value = "/getTransactionsDetailByResponseCode", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getTransactionsDetailByResponseCode(HttpServletRequest request, @RequestBody TransactionsByCodeDto transactionsDto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        String idOperation = SystemLog.newTxnIdOperation();

        return transactionsDetailService.getTransactionsDetailByRespondeCodeDto(idOperation, transactionsDto, infoTokenDto);
    }

    @Override
    @PostMapping(value = "/getTransactionsDetailOperation", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getTransactionsDetailOperation(HttpServletRequest request, @RequestBody TransactionsDetailOperationDto transactionsDetailOperationDto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        String idOperation = SystemLog.newTxnIdOperation();

        return transactionsDetailService.getTransactionsDetailOperation(idOperation, transactionsDetailOperationDto, infoTokenDto);
    }


    @Override
    @PostMapping(value = "/getMovements", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getMovements(HttpServletRequest request, @RequestBody /*MovementsDto*/ MovementsParamsDto movementsDto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        String idOperation = SystemLog.newTxnIdOperation();

        //return transactionsDetailService.getMovements(idOperation, movementsDto, infoTokenDto)
        return transactionsDetailService.getMovementsTransactions(idOperation, movementsDto, infoTokenDto);
    }

    @Override
    @PostMapping(value = "/getMovementsDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getMovementsDetail(HttpServletRequest request, @RequestBody TransactionsDetailOperationDto transactionsDetailOperationDto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        String idOperation = SystemLog.newTxnIdOperation();

        return transactionsDetailService.getMovementsDetail(idOperation, transactionsDetailOperationDto, infoTokenDto);
    }

    @Override
    @PostMapping(value = "/getUserDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getUserDetail(HttpServletRequest request) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        String idOperation = SystemLog.newTxnIdOperation();

        return transactionsDetailService.getUserDetail(infoTokenDto);
    }

    @Override
    @PostMapping(value = "getTransactionsConciliationByDateAt", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getTransactionsConciliationByDateAt(HttpServletRequest req, @RequestBody TransactionsDetailDto dto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(req);
        return transactionsDetailService.getTransactionsConciliationByDateAt(dto, infoTokenDto);
    }

    @Override
    @PostMapping(value = "getReportTransactionsConciliationHeader", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getReportTransactionsConciliationHeader(HttpServletRequest req, @RequestBody ParamsTransactionsConciliationDto dto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(req);
        return transactionsDetailService.getReportTransactionsConciliationHeader(dto, infoTokenDto);
    }

    @Override
    @PostMapping(value = "getReportTransactionsConciliationDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getReportTransactionsConciliationDetail(HttpServletRequest req, @RequestBody ParamsTransactionsConciliationDto dto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(req);
        return transactionsDetailService.getReportTransactionsConciliationDetail(dto, infoTokenDto);
    }

    @Override
    @PostMapping(value = "getReportTransactionsConciliedMacth", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getReportTransactionsConciliedMacth(HttpServletRequest req) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(req);
        return transactionsDetailService.getReportTransactionsConciliedMacth(null, infoTokenDto);
    }

    @Override
    @PostMapping(value = "getReportTransactionsConciliedNotMacth", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getReportTransactionsConciliedNotMacth(HttpServletRequest req) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(req);
        return transactionsDetailService.getReportTransactionsConciliedNotMacth(null, infoTokenDto);
    }

    @Override
    @PostMapping(value = "getReportTransactionsConciliedMacthHeader", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getReportTransactionsConciliedMacthHeader(HttpServletRequest req) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(req);
        return transactionsDetailService.getReportTransactionsConciliedMacthHeader(null, infoTokenDto);
    }

    @Override
    @PostMapping(value = "getReportTransactionsConciliedNotMacthHeader", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getReportTransactionsConciliedNotMacthHeader(HttpServletRequest req) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(req);
        return transactionsDetailService.getReportTransactionsConciliedNotMacthHeader(null, infoTokenDto);
    }

    @Override
    @PostMapping(value = "/getClient")
    public GenericResponse getClient(HttpServletRequest req) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(req);
        return transactionsDetailService.getClient(infoTokenDto);
    }

    @Override
    @PostMapping(value = "/getStatementAccount")
    public GenericResponse getStatementAccount(HttpServletRequest request, @RequestBody
            ParamsTransactionsConciliationDto dto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        return transactionsDetailService.getStatementAccount(dto, infoTokenDto);
    }

    @Override
    @PostMapping(value = "/getStatementAccountDetail")
    public GenericResponse getStatementAccountDetail(HttpServletRequest request, @RequestBody
            ParamsTransactionsConciliationDto dto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        return transactionsDetailService.getStatementAccountDetail(dto, infoTokenDto);
    }

    @Override
    @PostMapping(value = "/getBitacoraAccountStatus")
    public GenericResponse getBitacoraAccountStatus(HttpServletRequest request, @RequestBody
            RequestBitacoraAccountStatus dto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        return transactionsDetailService.getBitacoraAccountStatus(dto,infoTokenDto);
    }

    @Override
    @PostMapping(value = "/getTransactionFeesByCardType")
    public GenericResponse getTransactionFeesByCardType(HttpServletRequest request, @RequestBody
            ParamsTransactionsConciliationDto dto) {
        InfoTokenDto infoTokenDto = InfoToken.getInfoToken(request);
        return transactionsDetailService.getTransactionFeesByCardType(dto, infoTokenDto);
    }

}
