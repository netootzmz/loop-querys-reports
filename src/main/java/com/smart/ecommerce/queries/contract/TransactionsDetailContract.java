package com.smart.ecommerce.queries.contract;

import com.smart.ecommerce.queries.model.dto.*;
import com.smart.ecommerce.queries.util.GenericResponse;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public interface TransactionsDetailContract {


    @ApiOperation(value = "Método para obtener las transacciones", notes = "")
    public GenericResponse getTransactionsDetail(HttpServletRequest request, @RequestBody TransactionsDto transactionsDto);
    
    @ApiOperation(value = "Método para obtener las transacciones por codigo de respuesta", notes = "")
    public GenericResponse getTransactionsDetailByResponseCode(HttpServletRequest request, @RequestBody TransactionsByCodeDto transactionsDto);

    @ApiOperation(value = "Método para obetener detalle de las tranasacciones mediente el folio de la transaccion", notes = "folioTxn")
    public GenericResponse getTransactionsDetailOperation(HttpServletRequest request, @RequestBody TransactionsDetailOperationDto transactionsDetailOperationDto);


    @ApiOperation(value = "Método para obtener los movimientos de transacciones", notes = "")
    public GenericResponse getMovements(HttpServletRequest request, @RequestBody /*MovementsDto*/ MovementsParamsDto movementsDto);


    @ApiOperation(value = "Método para dar de alta nuevo Vendor", notes = "En los campos provider, cha racteristic y userByRegister se deben de mandar solo el id correspondiente de la base de datos")
    public GenericResponse getMovementsDetail(HttpServletRequest request, @RequestBody TransactionsDetailOperationDto transactionsDetailOperationDto);


    @ApiOperation(value = "Método para obtener datos del usuario ")
    public GenericResponse getUserDetail(HttpServletRequest request);

    @ApiOperation(value = "Método que obtiene la lista de transaccion por fecha")
    public GenericResponse getTransactionsConciliationByDateAt(HttpServletRequest req, @RequestBody TransactionsDetailDto dto);

    @ApiOperation(value = "Método para obtener transacciones conciliadas por fecha y afiliacion agrupados por clientes", notes = "En el campo de action se debe colocar CC - conciliado, PC - procesado")
    GenericResponse getReportTransactionsConciliationHeader(HttpServletRequest req, @RequestBody ParamsTransactionsConciliationDto dto);

    @ApiOperation(value = "Método para obtener transacciones conciliadas por fecha y afiliacion ", notes = "En el campo de action se debe colocar CC - conciliado, PC - procesado")
    GenericResponse getReportTransactionsConciliationDetail(HttpServletRequest req, @RequestBody ParamsTransactionsConciliationDto dto);

    @ApiOperation(value = "Método para obtener transacciones pagadas por smart y el banco")
    GenericResponse getReportTransactionsConciliedMacth(HttpServletRequest req);

    @ApiOperation(value = "Método para obtener transacciones no pagadas por smart y el banco")
    GenericResponse getReportTransactionsConciliedNotMacth(HttpServletRequest req);

    @ApiOperation(value = "Método para obtener totales de transacciones pagadas por smart y el banco")
    GenericResponse getReportTransactionsConciliedMacthHeader(HttpServletRequest req);

    @ApiOperation(value = "Método para obtener totales de transacciones pagadas por smart y el banco")
    GenericResponse getReportTransactionsConciliedNotMacthHeader(HttpServletRequest req);

    @ApiOperation(value = "Método para obtener lista de clientes activos para filtro de reporte detalle de transacción")
    GenericResponse getClient(HttpServletRequest req);

    @ApiOperation(value = "Método para obtener el estado de cuenta", notes = "")
    public GenericResponse getStatementAccount(HttpServletRequest request, @RequestBody ParamsTransactionsConciliationDto dto);

    @ApiOperation(value = "Método para obtener el estado de cuenta detallado", notes = "")
    public GenericResponse getStatementAccountDetail(HttpServletRequest request, @RequestBody ParamsTransactionsConciliationDto dto);

    @ApiOperation(value = "Método para obtener la bitacora del estado de cuenta", notes = "")
    public GenericResponse getBitacoraAccountStatus(HttpServletRequest request, @RequestBody RequestBitacoraAccountStatus dto);

    @ApiOperation(value = "Método para obtener los cargos por transacciones por tipo de tarjetas", notes = "")
    public GenericResponse getTransactionFeesByCardType(HttpServletRequest request, @RequestBody ParamsTransactionsConciliationDto dto);

}
