package com.smart.ecommerce.queries.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.smart.ecommerce.entity.admin.Client;
import com.smart.ecommerce.queries.repository.BitacoraAccountStatusRepository;
import com.smart.ecommerce.queries.repository.ClientRepository;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.smart.ecommerce.entity.core.CoreErrorCode;
import com.smart.ecommerce.queries.exception.SmartServicePlatformMyException;
import com.smart.ecommerce.queries.model.dto.*;
import com.smart.ecommerce.queries.repository.ErrorCodeRepository;
import com.smart.ecommerce.queries.repository.TransactionsDetailRepository;
import com.smart.ecommerce.queries.service.TransactionsDetailService;
import com.smart.ecommerce.queries.util.EnumActionTransaction;
import com.smart.ecommerce.queries.util.ErrorCode;
import com.smart.ecommerce.queries.util.GenericResponse;

import static com.smart.ecommerce.queries.util.Constants.*;
import static com.smart.ecommerce.queries.util.GetValuesJsonUtils.*;
import static com.smart.ecommerce.queries.util.ConvertDates.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("transactionsDetailService")
public class TransactionsDetailServiceImpl implements TransactionsDetailService {


    @Resource
    private TransactionsDetailRepository transactionsDetailRepository;

    @Resource
    private ErrorCodeRepository codeDao;

    @Autowired
    private DynamoDB dynamoDB;

    @Resource
    private ClientRepository clientRepository;

    @Autowired
    private BitacoraAccountStatusRepository bitacoraRepository;

    private static final String TABLE_ONE = "transactions_conciliation";
    private static final String TABLE_SETTLED_TRANSACTIONS = "settled_transactions";
    private static final String PATTERN = "yyyy-MM-dd";
    private static final String PATTERN_LARGE = "yyyy-MM-dd HH:mm:ss"; /* 2021-01-01 00:00:00 */
    private static final String FIELD_STATUS = "status";

    private static final String PARAM_MERCHANT_NUMBER = "#p_merchant_number";
    private static final String FIELD_MERCHANT_NUMBER = "merchant_number";
    private static final String VALUE_MERCHANT_NUMBER = ":v_merchant_number";

    private static final String PARAM_FOLIO_TXN = "#p_folio_txn";
    private static final String FIELD_FOLIO_TXN = "folio_txn";
    private static final String VALUE_FOLIO_TXN = ":v_folio_txn";
    private static final String VALUE_FOLIO_TXN_START = ":v_folio_txn_start";
    private static final String VALUE_FOLIO_TXN_END = ":v_folio_txn_end";

    public  static final String PARAM_CARD_TYPE = "#p_card_type";
    private static final String FIELD_CARD_TYPE = "card_type";
    public  static final String VALUE_CARD_TYPE = ":v_card_type";

    public  static final String PARAM_CARD_BRAND = "#p_cardBrand";
    private static final String FIELD_CARD_BRAND = "cardBrand";
    public  static final String VALUE_CARD_BRAND = ":v_cardBrand";

    public  static final String PARAM_DETAIL = "#p_detail";
    private static final String FIELD_DETAIL = "detail";

    public  static final String PARAM_REF_SP_NUMBER = "#p_refSpNumber";
    private static final String FIELD_REF_SP_NUMBER = "refSpNumber";
    public  static final String VALUE_REF_SP_NUMBER = ":v_refSpNumber";

    public  static final String PARAM_AUTHORIZATION_NUMBER = "#p_authorization_number";
    private static final String FIELD_AUTHORIZATION_NUMBER = "authorization_number";
    public  static final String VALUE_AUTHORIZATION_NUMBER = ":v_authorization_number";

    private static final String PARAM_TRANSACTION_DATE = "#p_transaction_date";
    private static final String FIELD_TRANSACTION_DATE = "transaction_date";
    private static final String VALUE_TRANSACTION_DATE_START = ":v_transaction_date_start";
    private static final String VALUE_TRANSACTION_DATE_END = ":v_transaction_date_end";

    private static final String PARAM_STATUS = "#p_status";
    public  static final String PARAM_CONCILIED = "#p_concilied";
    public  static final String PARAM_PROCESSED = "#p_processed";
    private static final String PARAM_DISPERSED = "#p_dispersed";
    private static final String FIELD_CREATE_AT = "created_at";
    public  static final String PARAM_CREATE_AT = "#p_create_at";
    public  static final String VALUE_CREATE_AT_START = ":v_create_at_start";
    public  static final String VALUE_CREATE_AT_END = ":v_create_at_end";
    public  static final String VALUE_CONCILIED = ":v_concilied";
    public  static final String VALUE_PROCESSED = ":v_processed";
    private static final String VALUE_DISPERSED = ":v_dispersed";
    private static final String KEY_RESULTS = "results";

    private static final String FIELD_BANK_COMMISSION = "bank_commission";
    private static final String FIELD_BANK_IVA_COMMISSION = "bank_iva_commission";
    private static final String FIELD_SMART_COMMISSION = "amount_rate_smart";
    private static final String FIELD_SMART_IVA_COMMISSION = "amount_iva_smart";
    private static final String FIELD_COMMISSION_SMART = "commission_smart";
    private static final String FIELD_TRANSACTION_AMOUNT = "transaction_amount";
    private static final String FIELD_TOTAL_AMOUNT_SMART = "total_amount_smart";
    private static final String FIELD_PAYMENT_BREAKDOWN = "payment_breakdown";
    private static final String FIELD_CREATED_AT = "created_at";
    private static final String FIELD_REFERENCE_NUMBER = "reference_number";
    private static final String FIELD_AMOUNT_DEPOSITED_SMART = "amount_deposited_smart";
    private static final String FIELD_AMOUNT_DEPOSITED = "amount_deposited";
    private static final String FIELD_AMOUNT_TREASURY = "amount_treasury";

    private static final String DISCOUNT_RATE = "discountRate";
    private static final String IVA_DISCOUNT_RATE = "ivaDiscountRate";
    private static final String TOTAL_SALE = "totalSale";
    private static final String PAYMENT_SALE = "paymentSale";

    private static final String SALE = "sale";
    private static final String REFUND = "refund";
    private static final String CANCELLATIONS = "cancellations";

    @Override
    public GenericResponse getTransactionsDetailDto(String idOperation, TransactionsDto transactionsDto, InfoTokenDto infoTokenDto) {
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
        try {

            List<ClientAffiliationResponseDto> results = resultsListDto(transactionsDto);

            if (!results.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {

            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            e.printStackTrace();
            return response;

        }
    }

    public List<ClientAffiliationResponseDto> resultsListDto(TransactionsDto transactionsDto) throws SmartServicePlatformMyException {
      try {
        List<ClientAffiliationResponseDto> results;

        List<ClientAffiliationResponseDto> lstClient = transactionsDetailRepository.getTransactionsDetailRepository(transactionsDto);
        //List<String> mns = getMerchantNumbersByDetail(lstClient)
        List<IdentifiersSqlToDynamo> mns = getMerchantsAndFoliosByDetail(lstClient);
        //List<TransactionsConciliationDto> lstTc = getTransactionsConciliationsByMechantNumber(mns, transactionsDto)
        List<TransactionsConciliationDto> lstTc = getTransactionsConciliationsByMechantAndFolio(mns, transactionsDto);

        //            if (!lstClient.isEmpty() && !lstTc.isEmpty()) {
        log.info("size clients: {}", lstClient.size());
        log.info("size transactions: {}", lstTc.size());
        //results = mergeListClientTransactions(lstClient, lstTc)
        results = mergeJoinListClientTransactions(lstClient, lstTc);
        //            } else {
        //                results = Collections.emptyList();
        //            }

        return results;
      } catch (Exception e) {
        e.printStackTrace();
        throw new SmartServicePlatformMyException("error get list mariaDB and dynamoDB: " + e.getMessage());
      }
    }
    
    /**Inicia Transacciones por codigo de respuesta**/
    public GenericResponse getTransactionsDetailByRespondeCodeDto(String idOperation, TransactionsByCodeDto transactionsDto, InfoTokenDto infoTokenDto) {
      GenericResponse response = new GenericResponse();
      List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
      try {

        List<ClientAffiliationResponseDto> results = resultsListByRespondeCodeto(transactionsDto);

        if (!results.isEmpty()) {
            response.setCodeStatus("00");
            response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
            Map<String, Object> information = new HashMap<>();
            information.put(KEY_RESULTS, results);
            response.setInformation(information);
        } else {
            response.setCodeStatus("01");
            response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
        }
        return response;
    } catch (Exception e) {

        response.setCodeStatus("03");
        response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
        e.printStackTrace();
        return response;

    }
    }
    
    public List<ClientAffiliationResponseDto> resultsListByRespondeCodeto(TransactionsByCodeDto transactionsDto) throws SmartServicePlatformMyException {
      try {
        List<ClientAffiliationResponseDto> results;
        List<ClientAffiliationResponseDto> sqlTransactions = transactionsDetailRepository.getTransactionsDetailRepositoryByResponseCode(transactionsDto);
        List<String> folios = getFolioByDetail(sqlTransactions);
        List<TransactionsConciliationDto> dynamoTransactions = getTransactionsConciliationsByFolios(folios, transactionsDto);
        
        log.info("sqlTransactions: {}", sqlTransactions.size());
        log.info("dynamoTransactions: {}", dynamoTransactions.size());
        
        if (!sqlTransactions.isEmpty() && !dynamoTransactions.isEmpty()) {
            results = mergeJoinListClientTransactionsByResponseCode(sqlTransactions, dynamoTransactions);
        } else {
            results = Collections.emptyList();
        }

        return results;
        
      } catch (Exception e) {
        e.printStackTrace();
        throw new SmartServicePlatformMyException("error get list mariaDB and dynamoDB:  " + e.getMessage());
      }
      
    }
    
    /**Termina Transacciones por codigo de respuesta**/

    @Override
    public GenericResponse getTransactionsDetailOperation(String idOperation, TransactionsDetailOperationDto transactionsDetailOperationDto, InfoTokenDto infoTokenDto) {
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
        try {

            List<ClientAffiliationResponseDetailDto> results = resultsListDetailDto(transactionsDetailOperationDto);

            //List<ClientAffiliationResponseDetailDto> results = transactionsDetailRepository.getTransactionsDetailOperation(transactionsDetailOperationDto)

            //List<ClientAffiliationResponseDto> results = transactionsDetailRepository.getTransactionsDetailOperation(transactionsDetailOperationDto)
            //List<TransactionsConciliationDto> lst = getTransactionsConciliationByTransactionsDetail(transactionsDetailOperationDto)
            //log.info("List Transactions Conciliation: {}", lst)

            if (!results.isEmpty()) {
                //'if' (results.size() > 0) '{'
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {

            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            e.printStackTrace();
            return response;

        }
    }

    public List<ClientAffiliationResponseDetailDto> resultsListDetailDto(TransactionsDetailOperationDto dto) throws SmartServicePlatformMyException {
        try {
            List<ClientAffiliationResponseDetailDto> results;
            List<ClientAffiliationResponseDetailDto> clients = transactionsDetailRepository.getTransactionsDetailOperation(dto);
//            List<TransactionsConciliationDto> lst = getTransactionsConciliationByTransactionsDetail(dto);
            List<TransactionsConciliationDto> lst = new ArrayList<>();

//            if (!clients.isEmpty() && !lst.isEmpty()) {
            log.info("size clients: {}", clients.size());
            log.info("size transactions: {}", lst.size());
            results = mergeJoinListTransactionsDetail(clients, lst);
//            } else {
//                getLogEmptyListClientsDetail(clients);
//                getLogEmptyListTransaction(lst);
//                results = Collections.emptyList();
//            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            throw new SmartServicePlatformMyException("error get list mariaDB and dynamoDB: " + e.getMessage());
        }

    }

    public void getLogEmptyListClientsDetail(List<ClientAffiliationResponseDetailDto> lst) {
        if (lst.isEmpty()) {
            log.error("Error list clients empty: {}", lst);
        }
    }

    public void getLogEmptyListTransaction(List<TransactionsConciliationDto> lst) {
        if (lst.isEmpty()) {
            log.error("Error list Transaction conciliation empty: {}", lst);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public GenericResponse getMovements(String idOperation, MovementsDto movementsDto, InfoTokenDto infoTokenDto) {
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
        try {


            Date today = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(today);
            cal.add(Calendar.MONTH, -5);

            Date months = new Date();
            months.setTime(movementsDto.getMonth().getTime());


            movementsDto.setMonthId(months.getMonth() + 1);

            Calendar cal2 = new GregorianCalendar();
            cal2.setTime(today);


            if (months.after(cal.getTime()) && months.before(cal2.getTime())) {

                List<ClientAffiliationResponseDto> results = transactionsDetailRepository.getMovements(movementsDto);
                if (!results.isEmpty()) {
                    response.setCodeStatus("00");
                    response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                    Map<String, Object> information = new HashMap<>();
                    information.put(KEY_RESULTS, results);
                    response.setInformation(information);
                } else {
                    response.setCodeStatus("01");
                    response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
                }
                return response;
            } else {

                response.setCodeStatus("63");
                response.setMessage(String.format(ErrorCode.getError(listCodes, "63").getMessage(), ""));
                return response;

            }


        } catch (Exception e) {

            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            e.printStackTrace();
            return response;

        }
    }

    @Override
    public GenericResponse getMovementsTransactions(String idOperation, /*MovementsDto dto */ MovementsParamsDto dto, InfoTokenDto infoTokenDto) {
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
        try {
            validateParamsEmptyOrNull(dto);
            /*
            Map<String, Map<String, Map<String, Object>>> resultsMap = getMovementsConcilied(dto)
            List<MovementsResponseDto> results = getListMovements(resultsMap)
            */
            List<MovementsTransactionsDto> results = getMovementsAllRate(dto);
            //log.info("Test List Map Movements: {}", getMovementsAllRate(dto))

            if (!results.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;

        } catch (Exception e) {
            if (e instanceof SmartServicePlatformMyException) {
                response.setCodeStatus("03");
                String msgError = String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage());
                response.setMessage(msgError.replace("No existe el ", "").replace("en DB", ""));
                e.printStackTrace();
                return response;
            } else {
                response.setCodeStatus("03");
                response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
                e.printStackTrace();
                return response;
            }
        }
    }

    public List<MovementsTransactionsDto> getMovementsAllRate(/*MovementsDto dto*/ MovementsParamsDto dto) {
        List<MovementsTransactionsDto> results = new ArrayList<>();
        List<MovementsTransactionsDto> depurateResults = new ArrayList<>();
        List<TransactionsConciliationDto> lst = getTransactionsConciliationByJsonArrayMovements(dto);
        String dateStr = dto.getStartDate();
        if (!lst.isEmpty()) {
            /**
             *Inicia donde se agregan los metodos que traeran los mapas de cada tasas que se agregaran a las lista de movimientos
             **/
            getSalesRefundMovements(dateStr, lst, results);
            getRateDebitCredit(dateStr, dto.getRfc(), lst, results);

            /**
             *Termina donde se agregan los metodos que traeran los mapas de cada tasas que se agregaran a las lista de movimientos
             **/

            depurateResults = depurateListMovements(results);
        }

        //return results
        //return depurateListMovements(results)
        return depurateResults;
    }

    public List<MovementsTransactionsDto> depurateListMovements(List<MovementsTransactionsDto> lst) {
        List<MovementsTransactionsDto> results = new ArrayList<>();
        if (!lst.isEmpty()) {
            Map<String, List<List<MovementsDetailsDto>>> mapResults = lst.stream()
                    .collect(Collectors.groupingBy(
                            MovementsTransactionsDto::getTypeTransaction,
                            Collectors.mapping(MovementsTransactionsDto::getDetail, Collectors.toList())
                    ));

            log.info("mapResults: {}", mapResults);

            lst.forEach(p -> {
                try {
                    MovementsTransactionsDto mt = new MovementsTransactionsDto();
                    mt.setMovementDate(p.getMovementDate());
                    mt.setTypeTransaction(p.getTypeTransaction());
                    mt.setDetail(p.getDetail());
                    if (mapResults.containsKey(p.getTypeTransaction())) {
                        List<MovementsDetailsDto> list = mapResults.get(p.getTypeTransaction()).get(0);
                        mt.setTotalTransaction(getTotalTransactionMovenment(list));
                    }
                    results.add(mt);

                } catch (Exception e) {
                    log.error("Error depurateListMovements: {}", e.getMessage());
                }
            });

        }
        return results;
    }

    public BigDecimal getTotalTransactionMovenment(List<MovementsDetailsDto> list) {
        BigDecimal total = BigDecimal.ZERO;
        if (!list.isEmpty()) {
            //list.forEach(p-> total.add(p.getTotal()))
            List<BigDecimal> values = getValueTotalTransactions(list);
            log.info("List values: {}", values);
            total = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return total;
    }

    public List<BigDecimal> getValueTotalTransactions(List<MovementsDetailsDto> list) {
        List<BigDecimal> values = new ArrayList<>();
        if (!list.isEmpty()) {
            list.forEach(p -> values.add(p.getTotal()));
        }
        return values;

    }

    public void getSalesRefundMovements(String dateStr, List<TransactionsConciliationDto> lst, List<MovementsTransactionsDto> results) {
        log.info(":: Fill Results By Sales And refund - GetSalesRefundMovements ::");
        Map<String, Map<String, Object>> mapSaleRefund = getTransactionsMovementsSalesRefund(lst);
        MovementsTransactionsDto mt = new MovementsTransactionsDto();
        mt.setMovementDate(dateStr);
        mt.setTypeTransaction("Transacciones");
        List<MovementsDetailsDto> list = new ArrayList<>();
        for (Entry<String, Map<String, Object>> mapObj : mapSaleRefund.entrySet()) {
            Map<String, Object> sales = mapObj.getValue();
            for (Entry<String, Object> sr : sales.entrySet()) {
                MovementsDetailsDto md = new MovementsDetailsDto();
                if (SALE.equalsIgnoreCase(sr.getKey())) {
                    md.setName("Ventas");
                    md.setTotal(BigDecimal.valueOf(Double.valueOf(sr.getValue().toString())));
                } else if (REFUND.equalsIgnoreCase(sr.getKey())) {
                    md.setName("Devoluciones");
                    md.setTotal(BigDecimal.valueOf(Double.valueOf(sr.getValue().toString())));
                } else if (CANCELLATIONS.equalsIgnoreCase(sr.getKey())) {
                    md.setName("Cancelaciones");
                    md.setTotal(BigDecimal.valueOf(Double.valueOf(sr.getValue().toString())));
                }
                list.add(md);
            }
        }
        mt.setDetail(list);
        results.add(mt);
    }

    public void getRateDebitCredit(String dateStr, String rfc, List<TransactionsConciliationDto> lst, List<MovementsTransactionsDto> results) {

        log.info(":: Fill Results By Rate debit And Credit - GetRateDebitCredit ::");
        Map<String, MovementsCommissionsMapingSumCount> mapRateDebitCredit = getRateMovementsDebitCredit(lst);

        MovementsTransactionsDto mt = new MovementsTransactionsDto();
        mt.setMovementDate(dateStr);
        mt.setTypeTransaction("Comisiones");
        List<MovementsDetailsDto> list = new ArrayList<>();
        /*Valida si el mapa trae registros verificar con la condicion del la lista principal de lst que se valida anteriormente*/
        if (!mapRateDebitCredit.isEmpty()) {
            for (Entry<String, MovementsCommissionsMapingSumCount> rate : mapRateDebitCredit.entrySet()) {
                MovementsCommissionsMapingSumCount sc = rate.getValue();
                MovementsDetailsDto md = new MovementsDetailsDto();
                if ("1".equalsIgnoreCase(rate.getKey())) {
                    md.setName("Tasa Debito");
                    //md.setName("Tasa credito")
                    md.setTotal(sc.getRate());
                } else if ("2".equalsIgnoreCase(rate.getKey())) {
                    md.setName("Tasa Credito");
                    //md.setName("Tasa debito")
                    md.setTotal(sc.getRate());
                }
                list.add(md);
            }
            /*para agregar mas nodo a la lista*/
            getRateFeeMonthlyMembership(rfc, list);
            getRateFeeEquipmentRent(rfc, list);
        }

        mt.setDetail(list);
        results.add(mt);
    }

    public void getRateFeeMonthlyMembership(String rfc, List<MovementsDetailsDto> list) {
        BigDecimal rateFeeMonthly = transactionsDetailRepository.getFeeCommissions(rfc, 1, 1);
        MovementsDetailsDto md = new MovementsDetailsDto();
        md.setName("Cuota mensual afiliacion");
        md.setTotal(rateFeeMonthly);
        list.add(md);
    }

    public void getRateFeeEquipmentRent(String rfc, List<MovementsDetailsDto> list) {
        BigDecimal rateEquipmentRent = transactionsDetailRepository.getFeeCommissions(rfc, 4, 2);
        MovementsDetailsDto md = new MovementsDetailsDto();
        md.setName("Renta equipo");
        md.setTotal(rateEquipmentRent);
        list.add(md);
    }

    @Override
    public GenericResponse getMovementsDetail(String idOperation, TransactionsDetailOperationDto transactionsDetailOperationDto, InfoTokenDto infoTokenDto) {
        return null;
    }

    @Override
    public GenericResponse getUserDetail(InfoTokenDto infoTokenDto) {
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
        try {


            List<UserDetailDto> results = transactionsDetailRepository.getUserDetail(infoTokenDto.getUser_by_register());
            if (results.size() > 0) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {

            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            e.printStackTrace();
            return response;

        }

    }

    @Override
    public GenericResponse getTransactionsConciliationByDateAt(TransactionsDetailDto dto, InfoTokenDto infoTokenDto) {
        Table table = dynamoDB.getTable(TABLE_ONE);
        List<TransactionsConciliationDto> results;

        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());


        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);

        String iniDate = null;
        String endDate = null;

        try {
            iniDate = sdf.format(dto.getInitDate());
            endDate = sdf.format(dto.getEndDate());
        } catch (Exception e) {
            log.info("Error parser date --> {}", e.getMessage());
        }

        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put(":created_at_ini", iniDate);
        mapParams.put(":created_at_end", endDate);

        ItemCollection<ScanOutcome> items = table
                .scan("created_at >= :created_at_ini AND created_at <= :created_at_end ", null, mapParams);
        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        log.info("json --> {}", jsonArray);

        results = getListTransactionsByJson(jsonArray);

        log.info("json --> {}", results);

        try {
            if (!results.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            log.info("Error response   --> {}", e.getMessage());
            return response;
        }

    }

    @Override
    public GenericResponse getReportTransactionsConciliationHeader(ParamsTransactionsConciliationDto dto, InfoTokenDto infoTokenDto) {
        log.info("::: GetReportTransactionsConciliationHeader :::");
        Table table = dynamoDB.getTable(TABLE_ONE);
        List<TransactionsConciliationDto> results;

        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());


        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);

        String iniDate = null;
        String endDate = null;

        try {
            iniDate = sdf.format(dto.getStartDate());
            endDate = sdf.format(dto.getEndDate());
        } catch (Exception e) {
            log.info("Error --> {}", e.getMessage());
        }

        Map<String, String> mapNames = new HashMap<>();
        mapNames.put("#statuss", FIELD_STATUS);
        mapNames.put("#merchantNumber", FIELD_MERCHANT_NUMBER);
        fillMapNames(mapNames, dto.getAction());

        Map<String, Object> mapValues = new HashMap<>();
        mapValues.put(":startDate", iniDate);
        mapValues.put(":endDate", endDate);
        mapValues.put(":merchantNumber", dto.getMerchantNumber());
        mapValues.put(":action", dto.getAction().equalsIgnoreCase("CC") ? Boolean.TRUE : Boolean.TRUE.toString());

        StringBuilder query = new StringBuilder();
        query.append("#statuss.#startDate >= :startDate  ");
        query.append("AND #statuss.#endDate <= :endDate  ");
        query.append("AND #merchantNumber = :merchantNumber  ");
        query.append("AND #statuss.#" + dto.getAction().toLowerCase() + " = :action  ");

        log.info(QUERY_LOG, query);

        ItemCollection<ScanOutcome> items = table.scan(query.toString(), mapNames, mapValues);

        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        log.info("jsonArray --> {}", jsonArray);

        results = getListTransactionsByJson(jsonArray);

        log.info("results --> {}", results);

        Map<String, Double> sumObj = results.stream().collect(
                Collectors.groupingBy(TransactionsConciliationDto::getMerchantNumber, Collectors.summingDouble(TransactionsConciliationDto::getTransactionAmount)));

        Map<String, Long> counting = results.stream().collect(
                Collectors.groupingBy(TransactionsConciliationDto::getMerchantNumber, Collectors.counting()));

        log.info("sum amount by group createdAt --> {}", sumObj);

        log.info("sum amount by counting merchantNumber --> {}", counting);

        TransactionsConciliationHeaderDto tch = new TransactionsConciliationHeaderDto();

        String merchantNumbre = dto.getMerchantNumber();
        Integer totalEntry = Integer.parseInt(counting.get(merchantNumbre).toString());
        Integer totalAmount = Integer.parseInt(sumObj.get(merchantNumbre).toString());

        tch.setMerchantNumber(merchantNumbre);
        tch.setTotalEntry(totalEntry);
        tch.setTotalAmount(totalAmount);

        log.info("Transactions Conciliation Header --> {}", tch);

        try {
            if (tch.getTotalEntry() != null && tch.getTotalAmount() != null) {
                //'if' '(''!sumObj'.isEmpty()')' '{'
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                //information.put(KEY_RESULTS, sumObj)
                information.put(KEY_RESULTS, tch);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            log.info("Error response --> {}", e.getMessage());
            return response;
        }
    }

    @Override
    public GenericResponse getReportTransactionsConciliationDetail(ParamsTransactionsConciliationDto dto, InfoTokenDto infoTokenDto) {
        log.info("::: GetReportTransactionsConciliationDetail :::");
        Table table = dynamoDB.getTable(TABLE_ONE);
        List<TransactionsConciliationDto> results;
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());


        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);

        String iniDate = null;
        String endDate = null;

        try {
            iniDate = sdf.format(dto.getStartDate());
            endDate = sdf.format(dto.getEndDate());
        } catch (Exception e) {
            log.info("Error --> {}", e.getMessage());
        }

        Map<String, String> mapNames = new HashMap<>();
        mapNames.put("#statuss", FIELD_STATUS);
        mapNames.put("#merchantNumber", FIELD_MERCHANT_NUMBER);
        fillMapNames(mapNames, dto.getAction());

        Map<String, Object> mapValues = new HashMap<>();
        mapValues.put(":startDate", iniDate);
        mapValues.put(":endDate", endDate);
        mapValues.put(":merchantNumber", dto.getMerchantNumber());
        mapValues.put(":action", dto.getAction().equalsIgnoreCase("CC") ? Boolean.TRUE : Boolean.TRUE.toString());

        StringBuilder query = new StringBuilder();
        query.append("#statuss.#startDate >= :startDate  ");
        query.append("AND #statuss.#endDate <= :endDate  ");
        query.append("AND #merchantNumber = :merchantNumber  ");
        query.append("AND #statuss.#" + dto.getAction().toLowerCase() + " = :action  ");

        log.info(QUERY_LOG, query);

        ItemCollection<ScanOutcome> items = table.scan(query.toString(), mapNames, mapValues);

        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        log.info("jsonArray --> {}", jsonArray);

        results = getListTransactionsByJson(jsonArray);

        log.info("results --> {}", results);


        try {
            if (!results.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            log.info("Error response --> {}", e.getMessage());
            return response;
        }
    }

    @Override
    public GenericResponse getReportTransactionsConciliedMacth(ParamsTransactionsConciliationDto dto, InfoTokenDto infoTokenDto) {
        Table table = dynamoDB.getTable(TABLE_ONE);
        List<TransactionsConciliationDto> results;
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());

        Map<String, String> mapNames = new HashMap<>();
        mapNames.put("#statuss", FIELD_STATUS);
        mapNames.put("#cc", EnumActionTransaction.CC.getValue());
        mapNames.put("#pc", EnumActionTransaction.PC.getValue());

        Map<String, Object> mapValues = new HashMap<>();
        mapValues.put(":actionCC", Boolean.TRUE);
        mapValues.put(":actionPC", Boolean.TRUE.toString());

        StringBuilder query = new StringBuilder();
        query.append("#statuss.#cc = :actionCC   ");
        query.append("AND #statuss.#pc = :actionPC   ");

        ItemCollection<ScanOutcome> items = table.scan(query.toString(), mapNames, mapValues);

        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        results = getListTransactionsByJson(jsonArray);

        log.info("results getReportTransactionsConciliedMacth --> {}", results);

        log.info("size getReportTransactionsConciliedMacth --> {}", results.size());


        try {
            if (!results.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            log.info("Error response --> {}", e.getMessage());
            return response;
        }
    }

    @Override
    public GenericResponse getReportTransactionsConciliedNotMacth(ParamsTransactionsConciliationDto dto, InfoTokenDto infoTokenDto) {
        Table table = dynamoDB.getTable(TABLE_ONE);
        List<TransactionsConciliationDto> results;
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());

        Map<String, String> mapNames = new HashMap<>();
        mapNames.put("#statuss", FIELD_STATUS);
        mapNames.put("#cc", EnumActionTransaction.CC.getValue());
        mapNames.put("#pc", EnumActionTransaction.PC.getValue());

        Map<String, Object> mapValues = new HashMap<>();
        mapValues.put(":actionCCO", Boolean.TRUE);
        mapValues.put(":actionCCT", Boolean.FALSE);
        mapValues.put(":actionPC", Boolean.FALSE.toString());

        StringBuilder query = new StringBuilder();
        query.append("( #statuss.#cc = :actionCCO  ");
        query.append("OR #statuss.#cc = :actionCCT )  ");
        query.append("AND #statuss.#pc = :actionPC  ");

        ItemCollection<ScanOutcome> items = table.scan(query.toString(), mapNames, mapValues);

        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        results = getListTransactionsByJson(jsonArray);

        log.info("results getReportTransactionsConciliedNotMacth --> {}", results);

        log.info("size getReportTransactionsConciliedNotMacth --> {}", results.size());


        try {
            if (!results.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            log.info("Error response --> {}", e.getMessage());
            return response;
        }
    }

    @Override
    public GenericResponse getReportTransactionsConciliedMacthHeader(ParamsTransactionsConciliationDto dto,
                                                                     InfoTokenDto infoTokenDto) {
        Table table = dynamoDB.getTable(TABLE_ONE);
        List<TransactionsConciliationDto> results;
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());

        Map<String, String> mapNames = new HashMap<>();
        mapNames.put("#statuss", FIELD_STATUS);
        mapNames.put("#cc", EnumActionTransaction.CC.getValue());
        mapNames.put("#pc", EnumActionTransaction.PC.getValue());

        Map<String, Object> mapValues = new HashMap<>();
        mapValues.put(":actionCC", Boolean.TRUE);
        mapValues.put(":actionPC", Boolean.TRUE.toString());

        StringBuilder query = new StringBuilder();
        query.append("#statuss.#cc = :actionCC  ");
        query.append("AND #statuss.#pc = :actionPC  ");

        ItemCollection<ScanOutcome> items = table.scan(query.toString(), mapNames, mapValues);

        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        results = getListTransactionsByJson(jsonArray);

        log.info("results getReportTransactionsConciliedMacthHeader --> {}", results);

        log.info("size getReportTransactionsConciliedMacthHeader --> {}", results.size());

        Map<String, Double> sumByClient = results.stream().collect(
                Collectors.groupingBy(TransactionsConciliationDto::getMerchantNumber, Collectors.summingDouble(TransactionsConciliationDto::getTransactionAmount)));

        Map<String, Long> counting = results.stream().collect(
                Collectors.groupingBy(TransactionsConciliationDto::getMerchantNumber, Collectors.counting()));

        List<String> lstUnique = new ArrayList<>();

        for (Entry<String, Double> entry : sumByClient.entrySet()) {
            lstUnique.add(entry.getKey());
        }

        List<TransactionsConciliationHeaderDto> lstTch = getTotalsByMerchant(lstUnique, sumByClient, counting);

        try {
            if (!lstTch.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, lstTch);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            log.info("Error response --> {}", e.getMessage());
            return response;
        }
    }

    @Override
    public GenericResponse getReportTransactionsConciliedNotMacthHeader(ParamsTransactionsConciliationDto dto,
                                                                        InfoTokenDto infoTokenDto) {
        Table table = dynamoDB.getTable(TABLE_ONE);
        List<TransactionsConciliationDto> results;
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());

        Map<String, String> mapNames = new HashMap<>();
        mapNames.put("#statuss", FIELD_STATUS);
        mapNames.put("#cc", EnumActionTransaction.CC.getValue());
        mapNames.put("#pc", EnumActionTransaction.PC.getValue());

        Map<String, Object> mapValues = new HashMap<>();
        mapValues.put(":actionCCO", Boolean.TRUE);
        mapValues.put(":actionCCT", Boolean.FALSE);
        mapValues.put(":actionPC", Boolean.FALSE.toString());

        StringBuilder query = new StringBuilder();
        query.append("( #statuss.#cc = :actionCCO  ");
        query.append("OR #statuss.#cc = :actionCCT )  ");
        query.append("AND #statuss.#pc = :actionPC  ");

        ItemCollection<ScanOutcome> items = table.scan(query.toString(), mapNames, mapValues);

        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        results = getListTransactionsByJson(jsonArray);

        log.info("results getReportTransactionsConciliedNotMacthHeader --> {}", results);

        log.info("size getReportTransactionsConciliedNotMacthHeader --> {}", results.size());

        Map<String, Double> sumByClient = results.stream().collect(
                Collectors.groupingBy(TransactionsConciliationDto::getMerchantNumber, Collectors.summingDouble(TransactionsConciliationDto::getTransactionAmount)));

        Map<String, Long> counting = results.stream().collect(
                Collectors.groupingBy(TransactionsConciliationDto::getMerchantNumber, Collectors.counting()));

        List<String> lstUnique = new ArrayList<>();

        for (Entry<String, Double> entry : sumByClient.entrySet()) {
            lstUnique.add(entry.getKey());
        }

        List<TransactionsConciliationHeaderDto> lstTch = getTotalsByMerchant(lstUnique, sumByClient, counting);

        try {
            if (!lstTch.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, lstTch);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            log.info("Error response --> {}", e.getMessage());
            return response;
        }
    }

    @Override
    public GenericResponse getStatementAccountDetail(ParamsTransactionsConciliationDto dto, InfoTokenDto infoTokenDto) {
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
        try {
            List<StatementAccountDto> results = getStattementAccountDetail(dto);
            if (!results.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;

        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            e.printStackTrace();
            return response;
        }
    }

    @Override
    public GenericResponse getStatementAccount(ParamsTransactionsConciliationDto dto, InfoTokenDto infoTokenDto) {
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
        try {
            List<StatementAccountDto> results = getStattementAccount(dto);
            if (!results.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;

        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            e.printStackTrace();
            return response;
        }
    }

    @Override
    public GenericResponse getBitacoraAccountStatus(RequestBitacoraAccountStatus dto, InfoTokenDto infoTokenDto) {
        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
        try {
            BitacoraAccountStatusDto bitacora = bitacoraRepository.getBitacoraByIdMembership(dto.getIdMembership(), dto.getInitDate(), dto.getEndDate());
            Map<String, Object> information = new HashMap<>();

            information.put(KEY_RESULTS, bitacora);
            response.setCodeStatus("00");
            response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
            response.setInformation(information);
            return response;
        } catch (Exception e) {
            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            e.printStackTrace();
            return response;
        }
    }

    public GenericResponse getTransactionFeesByCardType(
            ParamsTransactionsConciliationDto dto, InfoTokenDto infoTokenDto) {

        GenericResponse response = new GenericResponse();
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
        try {
            validateParamsTransactionEmptyOrNull(dto);
            Map<String, Map<String, MapingSumCountTransactionFees>> resultsMap = getMapTransactionProductBrand(dto);
            //Map<String, Map<String, Map<String, Object>>> resultsMap = getMapTransactionProductBrand(dto)
            List<CardTypeTransactionFeesDto> results = getListTransactionFees(resultsMap);
            if (!results.isEmpty()) {
                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);
            } else {
                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());
            }
            return response;
        } catch (Exception e) {
            if (e instanceof SmartServicePlatformMyException) {
                response.setCodeStatus("03");
                String msgError = String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage());
                response.setMessage(msgError.replace("No existe el ", "").replace("en DB", ""));
                e.printStackTrace();
                return response;
            } else {
                response.setCodeStatus("03");
                response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
                e.printStackTrace();
                return response;
            }
        }

    }

    public List<CardTypeTransactionFeesDto> getListTransactionFees(Map<String, Map<String, MapingSumCountTransactionFees>> resultMap) {
        List<CardTypeTransactionFeesDto> lst = new ArrayList<>();
        if (!resultMap.isEmpty()) {
            for (Entry<String, Map<String, MapingSumCountTransactionFees>> firtsMap : resultMap.entrySet()) {
                Map<String, MapingSumCountTransactionFees> valSecondMap = firtsMap.getValue();
                for (Entry<String, MapingSumCountTransactionFees> secondMap : valSecondMap.entrySet()) {
                    MapingSumCountTransactionFees thirdMap = secondMap.getValue();
                    CardTypeTransactionFeesDto cttf = new CardTypeTransactionFeesDto();
                    ProductBrandDto pb = new ProductBrandDto();
                    ProductBrandTransactionDto pbt = new ProductBrandTransactionDto();
                    cttf.setCardType(firtsMap.getKey());
                    pb.setCardBrand(secondMap.getKey());
                    pbt.setNumberDulyAuthorizedPayments(BigDecimal.valueOf(thirdMap.getNoTxn().longValue()));
                    pbt.setDiscountRate(BigDecimal.ZERO);
                    pbt.setExchangeFee(BigDecimal.ZERO);
                    pbt.setAmountChargedDiscountRate(thirdMap.getChargedDiscountRate());
                    pbt.setInvoicedAmount(thirdMap.getInvoicedAmount());
                    pbt.setTotalAmountCardPaymentReceptionService(thirdMap.getCardPaymentService());
                    pbt.setPenalties(BigDecimal.ZERO);
                    pb.setProductBrandTransaction(pbt);
                    cttf.setProductBrand(pb);
                    lst.add(cttf);
                }
            }
        }
        return lst;
    }

    public Map<String, Map<String, MapingSumCountTransactionFees>>
    getMapTransactionProductBrand(ParamsTransactionsConciliationDto dto) {

        log.info(":: TransactionsDetailServiceImpl - GetMapTransactionProductBrand ::");
        Map<String, Map<String, MapingSumCountTransactionFees>> resultMap = new HashMap<>();
        List<TransactionsConciliationDto> lst = getTransactionsConciliationByJson(dto);
        if (!lst.isEmpty()) {
            log.info("size lst - MapTransactionProductBrand: {}", lst.size());
            resultMap = lst.stream()
                    .collect(Collectors.groupingBy(tc -> tc.getDetail().getCardType(),
                            Collectors.groupingBy(tc -> tc.getDetail().getCardBrand(),
                                    Collector.of(MapingSumCountTransactionFees::new, MapingSumCountTransactionFees::add, MapingSumCountTransactionFees::merge)
                            )));

            log.info("Map Example mapSumCountFees:  {}", resultMap);

        }
        return resultMap;
    }

    public List<TransactionsConciliationDto> getTransactionsConciliationByJson(ParamsTransactionsConciliationDto dto) {

        log.info(":: TransactionsDetailServiceImpl - GetTransactionsConciliation ::");
        List<TransactionsConciliationDto> results;

        JSONArray jsonArray = getJsonTransactionsConciliationByCardType(dto);
        log.info(JSON_ARRAY_LOG, jsonArray);
        results = getListTransactionsByJson(jsonArray);

        if (!results.isEmpty()) {
            log.info("size json:  {}", jsonArray.length());
            log.info("lst getTransactionsConciliations --> {}", results);
            return results;
        } else {
            log.warn(DATA_EMPTY_DYNAMO);
            return Collections.emptyList();
        }
    }

    public JSONArray getJsonTransactionsConciliationByCardType(ParamsTransactionsConciliationDto dto) {
        log.info(":: TransactionsDetailServiceImpl - GetJsonTransactionsConciliationByCardType ::");
        Table table = dynamoDB.getTable(TABLE_ONE);
        String iniDate = null;
        String endDate = null;

        try {
            iniDate = convertDateToStr(PATTERN_LARGE, dto.getStartDate());
            endDate = convertDateToStr(PATTERN_LARGE, dto.getEndDate());
        } catch (Exception e) {
            log.info("Error -->  {}", e.getMessage());
        }

        Map<String, String> mapNames = new HashMap<>();
        mapNames.put(PARAM_MERCHANT_NUMBER, FIELD_MERCHANT_NUMBER);
        mapNames.put(PARAM_TRANSACTION_DATE, FIELD_TRANSACTION_DATE);
        mapNames.put(PARAM_STATUS, FIELD_STATUS);
        /*Para testear con estatus de concilied descomentar el param_conclied y comentar el param_dispersed*/
        mapNames.put(PARAM_DISPERSED, EnumActionTransaction.DP.getValue());
        //mapNames.put(PARAM_CONCILIED, EnumActionTransaction.CC.getValue())

        Map<String, Object> mapValues = new HashMap<>();
        mapValues.put(VALUE_MERCHANT_NUMBER, dto.getMerchantNumber());
        mapValues.put(VALUE_TRANSACTION_DATE_START, iniDate);
        mapValues.put(VALUE_TRANSACTION_DATE_END, endDate);
        /*Para testear con estatus de concilied descomentar el value_conclied y comentar el value_dispersed*/
        mapValues.put(VALUE_DISPERSED, Boolean.TRUE);
        //mapValues.put(VALUE_CONCILIED, Boolean.TRUE.toString())

        StringBuilder query = new StringBuilder();
        query.append(PARAM_MERCHANT_NUMBER);
        query.append(OPR_EQUAL);
        query.append(VALUE_MERCHANT_NUMBER);
        query.append(OPR_AND);
        query.append(PARAM_TRANSACTION_DATE);
        query.append(OPR_EQUAL_MAJOR);
        query.append(VALUE_TRANSACTION_DATE_START);
        query.append(OPR_AND);
        query.append(PARAM_TRANSACTION_DATE);
        query.append(OPR_EQUAL_MINOR);
        query.append(VALUE_TRANSACTION_DATE_END);
        query.append(OPR_AND);
        /*Para testear con estatus de concilied descomentar el param_conclied y comentar el param_dispersed*/
        query.append(PARAM_STATUS + POINT + PARAM_DISPERSED);
        //query.append(PARAM_STATUS + POINT + PARAM_CONCILIED)
        query.append(OPR_EQUAL);
        /*Para testear con estatus de concilied descomentar el value_conclied y comentar el value_dispersed*/
        query.append(VALUE_DISPERSED);
        //query.append(VALUE_CONCILIED)

        query.append("");
        query.append("");

        log.info(QUERY_LOG, query);

        ItemCollection<ScanOutcome> items =
                table.scan(query.toString(), mapNames, mapValues);

        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        if (StringUtils.isNotBlank(rsp) && StringUtils.isNotEmpty(rsp)) {
            jsonArray = new JSONArray("[" + rsp.toString() + "]");
            log.info(JSON_ARRAY_LOG, jsonArray);
            return jsonArray;
        } else {
            log.warn("json array of conciliations is empty...");
            return new JSONArray();
        }
    }

    public List<TransactionsConciliationDto> getTransactionsConciliationsByMechantNumber(List<String> mns, TransactionsDto tDto) {
        log.info("DTO:  {}", tDto);
        log.info("MNS:  {}", mns);
        List<TransactionsConciliationDto> lst;

        if (!mns.isEmpty()) {

            Table table = dynamoDB.getTable(TABLE_ONE);

            Map<String, String> mapNames = new HashMap<>();
            mapNames.put(PARAM_MERCHANT_NUMBER, FIELD_MERCHANT_NUMBER);

            Map<String, Object> mapValues = new HashMap<>();
            fillPutMapValueQuery(mns, VALUE_MERCHANT_NUMBER, mapValues);

            ImmutableMap<String, String> immutableMapName = ImmutableMap.copyOf(mapNames);
            ImmutableMap<String, Object> immutableMapVal = ImmutableMap.copyOf(mapValues);

            String valueInQuery = getQueryInByListMerchants(mns, VALUE_MERCHANT_NUMBER);
            StringBuilder query = new StringBuilder();
            query.append(PARAM_MERCHANT_NUMBER);
            query.append(OPR_IN);
            query.append(PARENTHESIS_OPEN);
            query.append(valueInQuery);
            query.append(PARENTHESIS_CLOSE);
            query.append("");

            log.info(QUERY_LOG, query);

            ScanSpec scanSpec = new ScanSpec()
                    .withFilterExpression(query.toString())
                    .withNameMap(immutableMapName)
                    .withValueMap(immutableMapVal);

            ItemCollection<ScanOutcome> rows = table.scan(scanSpec);
            Iterator<Item> record = rows.iterator();

            StringBuilder rsp = new StringBuilder();

            JSONArray jsonArray;

            while (record.hasNext()) {
                rsp.append(record.next().toJSONPretty());
                rsp.append(",");
            }

            jsonArray = new JSONArray("[" + rsp.toString() + "]");

            log.info(JSON_ARRAY_LOG, jsonArray);

            lst = getListTransactionsByJson(jsonArray);
        } else {
            lst = Collections.emptyList();
        }

        return lst;
    }

    public List<TransactionsConciliationDto> getTransactionsConciliationsByMechantAndFolio(List<IdentifiersSqlToDynamo> mns, TransactionsDto tDto) {
      log.info(":: TransactionsDetailServiceImpl - GetTransactionsConciliationsByMechantAndFolio ::");
      List<TransactionsConciliationDto> lst;

      JSONArray jsonArray;
      StringBuilder rsp = new StringBuilder();

      if (!mns.isEmpty()) {
        Table table = dynamoDB.getTable(TABLE_ONE);
        ScanSpec spec;
        
        /*Inicia para obtener minimo y maximo de folio*/
        List<Integer> folios = getFolios(mns);
        List<String> foliosStr = getFoliosStr(mns);
        Collections.sort(foliosStr);
        String minFolio = getMinFolio(foliosStr);
        String maxFolio = getMaxFolio(foliosStr);
        log.info("Valor Minimo Sort: {} ", minFolio);
        log.info("Valor Maximo Sort: {} ", maxFolio);
        Integer max = Collections.max(folios);
        Integer min = Collections.min(folios);
        String folioMin =  "FOL_" + setCifrasNumber(min, "00000");
        String folioMax =  "FOL_" + setCifrasNumber(max, "00000");
        log.info("Valor Minimo: {} ", folioMin);
        log.info("Valor Maximo: {} ", folioMax);
        /*Termina para obtener minimo y maximo de folio*/

        Map<String, String> expressionMapNames = new HashMap<>();
        expressionMapNames.putAll(new NameMap().with(PARAM_MERCHANT_NUMBER, FIELD_MERCHANT_NUMBER));
        expressionMapNames.putAll(new NameMap().with(PARAM_FOLIO_TXN, FIELD_FOLIO_TXN));
//        validateDetailParamStrExpMapName(expressionMapNames, PARAM_DETAIL, FIELD_DETAIL, tDto);
//        validateParamStrExpMapName(expressionMapNames, PARAM_CARD_TYPE, FIELD_CARD_TYPE, tDto.getCardType());
//        validateParamStrExpMapName(expressionMapNames, PARAM_CARD_BRAND, FIELD_CARD_BRAND, tDto.getCardBrand());
//        validateParamStrExpMapName(expressionMapNames, PARAM_AUTHORIZATION_NUMBER, FIELD_AUTHORIZATION_NUMBER, tDto.getApproval());
        /*aca sigue*/


        List<String> merchants = getMerchants(mns);

        Map<String, Object> expressionMapValues = new HashMap<>();
//        validateParamStrExpMapValue(expressionMapValues, VALUE_CARD_TYPE, tDto.getCardType());
//        validateParamStrExpMapValue(expressionMapValues, VALUE_CARD_BRAND, tDto.getCardBrand());
//        validateParamStrExpMapValue(expressionMapValues, VALUE_AUTHORIZATION_NUMBER, tDto.getApproval());

        String valueReferenceInQuery = getQueryInByListMerchants(merchants, VALUE_MERCHANT_NUMBER);
        //String valueReferenceInQuery = getQueryInByListMerchants(s.getFolioTxn(), VALUE_FOLIO_TXN)

        //expressionMapValues.putAll(new ValueMap().with(VALUE_MERCHANT_NUMBER, s.getMerchantNumber()))
        expressionMapValues.putAll(new ValueMap().with(VALUE_FOLIO_TXN_START, folioMin));
        expressionMapValues.putAll(new ValueMap().with(VALUE_FOLIO_TXN_END, folioMax));
        fillPutMapValueExpression(merchants, VALUE_MERCHANT_NUMBER, expressionMapValues);
        //fillPutMapValueExpression(s.getFolioTxn(), VALUE_FOLIO_TXN, expressionMapValues)

        StringBuilder query = new StringBuilder();
        query.append(PARAM_MERCHANT_NUMBER);
        query.append(OPR_IN);
        query.append(PARENTHESIS_OPEN);
        query.append(valueReferenceInQuery);
        query.append(PARENTHESIS_CLOSE);

        query.append(OPR_AND);
        query.append(PARAM_FOLIO_TXN);
        query.append(OPR_BETWEEN);
        query.append(VALUE_FOLIO_TXN_START);
        query.append(OPR_AND);
        query.append(VALUE_FOLIO_TXN_END);
//        query.append(
//          validateQuery(
//            OPR_AND + SPACE
//            + PARAM_DETAIL + POINT + PARAM_CARD_TYPE + SPACE
//            + OPR_EQUAL + SPACE
//            + VALUE_CARD_TYPE
//            , tDto.getCardType())
//          )
//        ;
//        query.append(
//          validateQuery(
//            OPR_AND + SPACE
//            + PARAM_DETAIL + POINT + PARAM_CARD_BRAND + SPACE
//            + OPR_EQUAL + SPACE
//            + VALUE_CARD_BRAND
//            , tDto.getCardBrand())
//          )
//        ;
//        query.append(
//          validateQuery(
//            OPR_AND + SPACE
//            + PARAM_AUTHORIZATION_NUMBER + SPACE
//            + OPR_EQUAL + SPACE
//            + VALUE_AUTHORIZATION_NUMBER
//            , tDto.getApproval())
//          )
//        ;
        query.append("");

        log.info(QUERY_LOG, query);

        spec = new ScanSpec()
          .withFilterExpression(query.toString())
          .withNameMap(expressionMapNames)
          .withValueMap(expressionMapValues);

        ItemCollection<ScanOutcome> items = table.scan(spec);
        Iterator<Item> record = items.iterator();

        while (record.hasNext()) {
          String jsonPretty = record.next().toJSONPretty();
          rsp.append(jsonPretty);
          rsp.append(",");

        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        log.info(JSON_ARRAY_LOG, jsonArray);

        lst = getListTransactionsByJson(jsonArray);

      } else {
        lst = Collections.emptyList();
      }

      return lst;
    }
    
    public List<TransactionsConciliationDto> getTransactionsConciliationsByFolios(List<String> folios, TransactionsByCodeDto tDto) {
      log.info(":: TransactionsDetailServiceImpl - GetTransactionsConciliationsByFolios ::");
      ObjectMapper mapper = new ObjectMapper();
      List<TransactionsConciliationDto> results = new ArrayList<>();;
      
      Table table = dynamoDB.getTable(TABLE_ONE);
      
      String valueInQuery = getQueryInByListMerchants(folios, VALUE_FOLIO_TXN);
      
      Map<String, String> mapNames = new HashMap<>();
      mapNames.put(PARAM_FOLIO_TXN, FIELD_FOLIO_TXN);
      
      Map<String, Object> mapValues = new HashMap<>();
      fillPutMapValueQuery(folios, VALUE_FOLIO_TXN, mapValues);
      
      StringBuilder query = new StringBuilder();
      query.append(PARAM_FOLIO_TXN);
      query.append(OPR_IN);
      query.append(PARENTHESIS_OPEN);
      query.append(valueInQuery);
      query.append(PARENTHESIS_CLOSE);
      query.append("");
      query.append("");

      log.info(QUERY_LOG, query);
      
      ItemCollection<ScanOutcome> items =
        table.scan(query.toString(), mapNames, mapValues);

      Iterator<Item> iterator = items.iterator();
      
      while (iterator.hasNext()) {
        try {
          JsonNode jsonNode = mapper.readTree(iterator.next().toJSON());
          TransactionsConciliationDto tc = setTransactionsConciliationByJsonNode(jsonNode);
          results.add(tc);
        } catch (Exception e) {
          log.error(ERROR_QUERY_DYNAMO_LOG, e.getMessage());
        }
      }
      log.info("Size - GetTransactionsConciliationsByFolios: {}", results.size());
      return results;
    }

    public List<TransactionsConciliationDto> getTransactionsConciliationByTransactionsDetail(TransactionsDetailOperationDto dto) {
        List<TransactionsConciliationDto> lst;
        Table table = dynamoDB.getTable(TABLE_ONE);
        ScanSpec spec;

        JSONArray jsonArray;
        StringBuilder rsp = new StringBuilder();

        Map<String, String> expressionMapNames = new HashMap<>();
        expressionMapNames.putAll(new NameMap().with(PARAM_FOLIO_TXN, FIELD_FOLIO_TXN));

        Map<String, Object> expressionMapValues = new HashMap<>();
        expressionMapValues.putAll(new ValueMap().with(VALUE_FOLIO_TXN, dto.getFolioTxn()));

        StringBuilder query = new StringBuilder();
        query.append(PARAM_FOLIO_TXN);
        query.append(OPR_EQUAL);
        query.append(VALUE_FOLIO_TXN);
        query.append("");

        log.info(QUERY_LOG, query);

        spec = new ScanSpec()
                .withFilterExpression(query.toString())
                .withNameMap(expressionMapNames)
                .withValueMap(expressionMapValues);

        ItemCollection<ScanOutcome> items = table.scan(spec);
        Iterator<Item> record = items.iterator();

        while (record.hasNext()) {
            String jsonPretty = record.next().toJSONPretty();
            rsp.append(jsonPretty);
            rsp.append(",");

        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        log.info(JSON_ARRAY_LOG, jsonArray);

        if (!jsonArray.isEmpty()) {
            lst = getListTransactionsByJson(jsonArray);
        } else {
            lst = Collections.emptyList();
        }


        return lst;
    }

    public void validateDetailParamStrExpMapName(Map<String, String> expressionMapNames,
                                                 String key, String name, TransactionsDto dto) {

        if (
                StringUtils.isNotEmpty(dto.getCardType()) || StringUtils.isNotBlank(dto.getCardType())
                        ||
                        StringUtils.isNotEmpty(dto.getCardBrand()) || StringUtils.isNotBlank(dto.getCardBrand())
                        ||
                        StringUtils.isNotEmpty(dto.getPaymentReference()) || StringUtils.isNotBlank(dto.getPaymentReference())
        ) {
            expressionMapNames.putAll(new NameMap().with(key, name));
        }
    }

    public void validateParamStrExpMapName(Map<String, String> expressionMapNames,
                                           String key, String name, String param) {

        if (StringUtils.isNotEmpty(param) || StringUtils.isNotBlank(param)) {
            expressionMapNames.putAll(new NameMap().with(key, name));
        }
    }

    public void validateParamStrExpMapValue(Map<String, Object> expressionMapValues,
                                            String key, String param) {

        if (StringUtils.isNotEmpty(param) || StringUtils.isNotBlank(param)) {
            expressionMapValues.putAll(new ValueMap().with(key, param));
        }
    }

    public String validateQuery(String query, String param) {
        if (StringUtils.isNotBlank(param) && StringUtils.isNotEmpty(param)) {
            return query;
        } else {
            return "";
        }

    }

    public void fillPutMapValueExpression(List<String> mns, String nameValue, Map<String, Object> mapValues) {
        for (int i = 0; i < mns.size(); i++) {
            mapValues.putAll(new ValueMap().with(nameValue + UNDERSCORE + i, mns.get(i)));
        }
    }


    public Map<String, Map<String, Map<String, Object>>> getMovementsConcilied(/*MovementsDto*/ MovementsParamsDto dto) {

        List<TransactionsConciliationDto> lstTransactionsConciliation;
        Map<String, Map<String, Map<String, Object>>> movementsGroupCreateAt = new HashMap<>();

        /*
        Table table = dynamoDB.getTable(TABLE_ONE)
        String iniDate = null
        String endDate = null
        String merchantNumber = null

        'try' '{'
            List<ParamsTransactionsConciliationDto> lstMerchants = transactionsDetailRepository.getMerchantNumberByRfc(dto)
            'if' (!lstMerchants.isEmpty()) '{'
                merchantNumber = lstMerchants.get(0).getMerchantNumber()
            '}' 'else' '{'
                merchantNumber = "0"
            '}'

            iniDate = convertDateToStr(PATTERN, dto.getStartDate())
            endDate = convertDateToStr(PATTERN, dto.getEndDate())
        '}' 'catch' (Exception e) '{'
            log.info("Error  --> {}", e.getMessage())
        '}'
        Map<String, String> mapNames = new HashMap<>()
        mapNames.put(PARAM_MERCHANT_NUMBER, FIELD_MERCHANT_NUMBER)
        mapNames.put(PARAM_CREATE_AT, FIELD_CREATE_AT)
        mapNames.put(PARAM_STATUS, FIELD_STATUS)
        mapNames.put(PARAM_DISPERSED, EnumActionTransaction.DP.getValue())
        //mapNames.put(PARAM_CONCILIED, EnumActionTransaction.CC.getValue())
        //mapNames.put(PARAM_PROCESSED, EnumActionTransaction.PC.getValue())

        Map<String, Object> mapValues = new HashMap<>()
        mapValues.put(VALUE_MERCHANT_NUMBER, merchantNumber)
        mapValues.put(VALUE_CREATE_AT_START, iniDate)
        mapValues.put(VALUE_CREATE_AT_END, endDate)
        mapValues.put(VALUE_DISPERSED, Boolean.TRUE)
        //mapValues.put(VALUE_CONCILIED, Boolean.TRUE.toString())
        //mapValues.put(VALUE_PROCESSED, Boolean.TRUE.toString())

        StringBuilder query = new StringBuilder()
        query.append(PARAM_MERCHANT_NUMBER)
        query.append(OPR_EQUAL)
        query.append(VALUE_MERCHANT_NUMBER)
        query.append(OPR_AND)
        query.append(PARAM_CREATE_AT)
        query.append(OPR_EQUAL_MAJOR)
        query.append(VALUE_CREATE_AT_START)
        query.append(OPR_AND)
        query.append(PARAM_CREATE_AT)
        query.append(OPR_EQUAL_MINOR)
        query.append(VALUE_CREATE_AT_END)
        query.append(OPR_AND)
        query.append(PARAM_STATUS + POINT + PARAM_DISPERSED)
        //query.append(PARAM_STATUS + POINT + PARAM_CONCILIED
        query.append(OPR_EQUAL)
        query.append(VALUE_DISPERSED)
        //query.append(VALUE_CONCILIED)

        //query.append(OPR_AND)
        //query.append(PARAM_STATUS + POINT + PARAM_PROCESSED)
        //query.append(OPR_EQUAL)
        //query.append(VALUE_PROCESSED)


        log.info(QUERY_LOG, query)

        ItemCollection<ScanOutcome> items = table.scan(query.toString(), mapNames, mapValues)
        Iterator<Item> iterator = items.iterator()

        StringBuilder rsp = new StringBuilder()

        JSONArray jsonArray

        'while' (iterator.hasNext()) '{'
            rsp.append(iterator.next().toJSONPretty())
            rsp.append(",")
        '}'

        jsonArray = new JSONArray("[" + rsp.toString() + "]")
        */

        JSONArray jsonArray = getJsonArrayMovements(dto);

        log.info(JSON_ARRAY_LOG, jsonArray);

        lstTransactionsConciliation = getListTransactionsByJson(jsonArray);

        if (!lstTransactionsConciliation.isEmpty()) {

            log.info("lst --> {}", lstTransactionsConciliation);
            log.info("size lst --> {}", lstTransactionsConciliation.size());

            movementsGroupCreateAt = lstTransactionsConciliation.stream()
                    .collect(Collectors.groupingBy(TransactionsConciliationDto::getCreateATStr,
                            Collectors.groupingBy(TransactionsConciliationDto::getReferenceNumber,
                                    Collector.of(HashMap::new, (m, p) -> {
                                        m.merge(TOTAL_SALE, p.getTransactionAmount(), (a, b) -> ((Integer) a) + ((Integer) b));
                                        m.merge(DISCOUNT_RATE, (p.getPaymentBreakdown().getBankCommission() + p.getPaymentBreakdown().getSmartCommission()), (a, b) -> ((Double) a) + ((Double) b));
                                        m.merge(IVA_DISCOUNT_RATE, (p.getPaymentBreakdown().getBankIvaCommission() + p.getPaymentBreakdown().getSmartIvaCommission()), (a, b) -> ((Double) a) + ((Double) b));
                                        m.merge(PAYMENT_SALE, p.getPaymentBreakdown().getTotalAmountSmart(), (a, b) -> ((Double) a) + ((Double) b));
                                    }, (m1, m2) -> {
                                        m1.merge(TOTAL_SALE, m2.get(TOTAL_SALE), (a, b) -> ((Integer) a) + ((Integer) b));
                                        m1.merge(DISCOUNT_RATE, m2.get(DISCOUNT_RATE), (a, b) -> ((Double) a) + ((Double) b));
                                        m1.merge(IVA_DISCOUNT_RATE, m2.get(IVA_DISCOUNT_RATE), (a, b) -> ((Double) a) + ((Double) b));
                                        m1.merge(PAYMENT_SALE, m2.get(PAYMENT_SALE), (a, b) -> ((Double) a) + ((Double) b));
                                        return m1;
                                    }))));

            log.info("movementsGroupCreateAt map :  {}", movementsGroupCreateAt);
            return movementsGroupCreateAt;

        } else {
            log.info("lista vacia de lstTransactionsConciliation");
            return movementsGroupCreateAt;
        }
    }

    public Map<String, MovementsCommissionsMapingSumCount> getRateMovementsDebitCredit
            (List<TransactionsConciliationDto> lst) {
        log.info(":: TransactionsDetailServiceImpl - GetRateMovementsDebitCredit ::");
        Map<String, MovementsCommissionsMapingSumCount> resultMap = new HashMap<>();
        if (!lst.isEmpty()) {
            log.info("size list:  {}", lst.size());
            resultMap = lst.stream()
                    .collect(Collectors.groupingBy(tc -> tc.getDetail().getCardType(),
                            Collector.of(MovementsCommissionsMapingSumCount::new, MovementsCommissionsMapingSumCount::add, MovementsCommissionsMapingSumCount::merge)
                    ));

            log.info("Map Example resultMap RateMovementsDebitCredit:  {}", resultMap);
        }
        return resultMap;
    }

    public Map<String, Map<String, Object>> getTransactionsMovementsSalesRefund(List<TransactionsConciliationDto> lst) {
        log.info(":: TransactionsDetailServiceImpl - GetTransactionsMovementsSalesRefund ::");
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        if (!lst.isEmpty()) {
            log.info("size list:  {}", lst.size());
            resultMap = lst.stream()
                    .collect(Collectors.groupingBy(TransactionsConciliationDto::getTransactionDateShort,
                            Collector.of(HashMap::new, (m, p) -> {
                                m.merge(SALE, (p.getTransactionAmount()), (a, b) -> ((Double) a) + ((Double) b));
                                m.merge(REFUND, (p.getRefundAmount()), (a, b) -> ((Double) a) + ((Double) b));
                                m.merge(CANCELLATIONS, (p.getRefundAmount()), (a, b) -> ((Double) a) + ((Double) b));

                            }, (m1, m2) -> {
                                m1.merge(SALE, m2.get(SALE), (a, b) -> ((Double) a) + ((Double) b));
                                m1.merge(REFUND, m2.get(REFUND), (a, b) -> ((Double) a) + ((Double) b));
                                m1.merge(CANCELLATIONS, m2.get(CANCELLATIONS), (a, b) -> ((Double) a) + ((Double) b));
                                return m1;
                            })));
            log.info("Map Example resultMap TransactionsMovementsSalesRefund:  {}", resultMap);
        }
        return resultMap;
    }

    public List<TransactionsConciliationDto> getTransactionsConciliationByJsonArrayMovements
            (/*MovementsDto*/ MovementsParamsDto dto) {

        log.info(":: TransactionsDetailServiceImpl - GetTransactionsConciliationByJsonArrayMovements ::");
        List<TransactionsConciliationDto> results;

        JSONArray jsonArray = getJsonArrayMovements(dto);
        log.info(JSON_ARRAY_LOG, jsonArray);
        results = getListSettledTransactionsByJson(jsonArray);
        //results = getListTransactionsByJson(jsonArray)

        if (!results.isEmpty()) {
            log.info("size json - GetTransactionsConciliationByJsonArrayMovements:  {}", jsonArray.length());
            log.info(ARRAY_LIST_LOG, results);
            return results;
        } else {
            log.warn(DATA_EMPTY_DYNAMO);
            return Collections.emptyList();
        }
    }


    public JSONArray getJsonArrayMovements(/*MovementsDto*/ MovementsParamsDto dto) {
        Table table = dynamoDB.getTable(TABLE_SETTLED_TRANSACTIONS);
        //Table table = dynamoDB.getTable(TABLE_ONE)

        String iniDate = null;
        String endDate = null;
        String merchantNumber = null;

        try {
            List<ParamsTransactionsConciliationDto> lstMerchants = transactionsDetailRepository.getMerchantNumberByRfc(dto);
            if (!lstMerchants.isEmpty()) {
                merchantNumber = lstMerchants.get(0).getMerchantNumber();
            } else {
                merchantNumber = "0";
            }

            iniDate = dto.getStartDate() + " 00:00:00";
            endDate = dto.getStartDate() + " 23:59:59";

        } catch (Exception e) {
            log.info("Error  --> {}", e.getMessage());
        }
        Map<String, String> mapNames = new HashMap<>();
        mapNames.put(PARAM_MERCHANT_NUMBER, FIELD_MERCHANT_NUMBER);
        mapNames.put(PARAM_TRANSACTION_DATE, FIELD_TRANSACTION_DATE);
        //mapNames.put(PARAM_STATUS, FIELD_STATUS)
        /*Para testear descomentar concilied y comentar dispersed*/
        //mapNames.put(PARAM_CONCILIED, EnumActionTransaction.CC.getValue())
        mapNames.put(PARAM_DISPERSED, EnumActionTransaction.DP.getValue());

        Map<String, Object> mapValues = new HashMap<>();
        mapValues.put(VALUE_MERCHANT_NUMBER, merchantNumber);
        mapValues.put(VALUE_TRANSACTION_DATE_START, iniDate);
        mapValues.put(VALUE_TRANSACTION_DATE_END, endDate);
        /*Para testear descomentar concilied y comentar dispersed*/
        //mapValues.put(VALUE_CONCILIED, Boolean.TRUE.toString())
        mapValues.put(VALUE_DISPERSED, 1);
        //mapValues.put(VALUE_DISPERSED, Boolean.TRUE)

        StringBuilder query = new StringBuilder();
        query.append(PARAM_MERCHANT_NUMBER);
        query.append(OPR_EQUAL);
        query.append(VALUE_MERCHANT_NUMBER);
        query.append(OPR_AND);
        query.append(PARAM_TRANSACTION_DATE);
        query.append(OPR_EQUAL_MAJOR);
        query.append(VALUE_TRANSACTION_DATE_START);
        query.append(OPR_AND);
        query.append(PARAM_TRANSACTION_DATE);
        query.append(OPR_EQUAL_MINOR);
        query.append(VALUE_TRANSACTION_DATE_END);
        query.append(OPR_AND);
        /*Para testear descomentar concilied y comentar dispersed*/
        //query.append(PARAM_STATUS + POINT + PARAM_CONCILIED)
        query.append(PARAM_DISPERSED);
        query.append(OPR_EQUAL);
        /*Para testear descomentar concilied y comentar dispersed*/
        //query.append(VALUE_CONCILIED)
        query.append(VALUE_DISPERSED);

        log.info(QUERY_LOG, query);

        ItemCollection<ScanOutcome> items = table.scan(query.toString(), mapNames, mapValues);
        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        if (StringUtils.isNotBlank(rsp) && StringUtils.isNotEmpty(rsp)) {
            jsonArray = new JSONArray("[" + rsp.toString() + "]");
            log.info(JSON_ARRAY_LOG, jsonArray);
            return jsonArray;
        } else {
            log.warn("json array of conciliations is empty...");
            return new JSONArray();
        }

    }


    private void validateParamsEmptyOrNull(/*MovementsDto dto*/ MovementsParamsDto dto)
            throws SmartServicePlatformMyException {
        if (dto != null) {
            validAttributeStrEntry(dto.getRfc(), "RFC");
            validAttributeStrEntry(dto.getStartDate(), "StartDate");
        } else {
            throw new SmartServicePlatformMyException("Empty data request");
        }
    }

    private void validateParamsTransactionEmptyOrNull(ParamsTransactionsConciliationDto dto)
            throws SmartServicePlatformMyException {
        if (dto != null) {
            validAttributeStrEntry(dto.getMerchantNumber(), "Merchant Number");
            validAttributeDateEntry(dto.getStartDate(), "StartDate");
            validAttributeDateEntry(dto.getEndDate(), "EndDate");
        } else {
            throw new SmartServicePlatformMyException("Empty data request");
        }
    }

    private void validAttributeStrEntry(String attr, String name)
            throws SmartServicePlatformMyException {

        if (StringUtils.isEmpty(attr) || StringUtils.isBlank(attr)) {
            throw new SmartServicePlatformMyException(
                    "attribute null or empty: " + name);
        }
    }

    private void validAttributeDateEntry(Date attr, String name)
            throws SmartServicePlatformMyException {

        if (attr == null) {
            throw new SmartServicePlatformMyException(
                    "attribute null or empty: " + name);
        }
    }

    public List<MovementsResponseDto> getListMovements(Map<String, Map<String, Map<String, Object>>> movementsGroupCreateAt) {
        List<MovementsResponseDto> lstM = new ArrayList<>();
        if (!movementsGroupCreateAt.isEmpty()) {
            for (Entry<String, Map<String, Map<String, Object>>> firtsMap : movementsGroupCreateAt.entrySet()) {
                Map<String, Map<String, Object>> valSecondMap = firtsMap.getValue();
                for (Entry<String, Map<String, Object>> secondMap : valSecondMap.entrySet()) {
                    Map<String, Object> thirdMap = secondMap.getValue();
                    MovementsResponseDto mrd = new MovementsResponseDto();
                    DetailTransactionDto dtd = new DetailTransactionDto();
                    DetailSaleDto dsd = new DetailSaleDto();
                    mrd.setDateTransaction(firtsMap.getKey());
                    dtd.setReferenceNumber(secondMap.getKey());
                    dsd.setDiscountRate(BigDecimal.valueOf(Double.valueOf(thirdMap.get(DISCOUNT_RATE).toString())));
                    dsd.setIvaDiscountRate(BigDecimal.valueOf(Double.valueOf(thirdMap.get(IVA_DISCOUNT_RATE).toString())));
                    dsd.setTotalSale(BigDecimal.valueOf(Double.valueOf(thirdMap.get(TOTAL_SALE).toString())));
                    dsd.setPaymentSale(BigDecimal.valueOf(Double.valueOf(thirdMap.get(PAYMENT_SALE).toString())));
                    dtd.setDetailSale(dsd);
                    mrd.setDetailTransaction(dtd);
                    lstM.add(mrd);
                }
            }
        }
        return lstM;
    }


    public List<StatementAccountDto> getStattementAccountDetail(ParamsTransactionsConciliationDto dto) {
        List<StatementAccountDto> lst;
        List<TransactionsConciliationDto> lstTC;
        Map<String, Map<String, Object>> mapAccount;
        lstTC = getTransactionsConciliationForAccount(dto);
        mapAccount = getGroupStattementAccountDetail(lstTC);
        lst = getStattementAccountDetailByMap(mapAccount);
        if (!lst.isEmpty()) {
            lst.stream().forEach(p -> p.setMerchantNumber(dto.getMerchantNumber()));
            log.info("getStattementAccountDetail --> {}", lst);
            return lst;
        } else {
            log.info("list vacia --> {}", lst);
            return Collections.emptyList();
        }

    }


    public List<StatementAccountDto> getStattementAccount(ParamsTransactionsConciliationDto dto) {
        List<StatementAccountDto> lst;
        List<TransactionsConciliationDto> lstTC;
        Map<String, Map<String, Object>> mapAccount;
        lstTC = getTransactionsConciliationForAccount(dto);
        mapAccount = getGroupStattementAccount(lstTC);
        lst = getStattementAccountByMap(mapAccount);
        if (!lst.isEmpty()) {
            log.info("getStattementAccount --> {}", lst);
            return lst;
        } else {
            log.info("list vacia --> {}", lst);
            return Collections.emptyList();
        }

    }

    /*Metodo con el query de dynamodb que obtiene los datos del estado de cuenta */
    public List<TransactionsConciliationDto> getTransactionsConciliationForAccount(ParamsTransactionsConciliationDto dto) {
        List<TransactionsConciliationDto> lst;
        Table table = dynamoDB.getTable(TABLE_SETTLED_TRANSACTIONS);
        //Table table = dynamoDB.getTable(TABLE_ONE)

        String iniDate = null;
        String endDate = null;

        try {
            iniDate = convertDateToStr(PATTERN_LARGE, dto.getStartDate());
            endDate = convertDateToStr(PATTERN_LARGE, dto.getEndDate());
        } catch (Exception e) {
            log.info("Error --> {}", e.getMessage());
        }

        Map<String, String> mapNames = new HashMap<>();
        mapNames.put(PARAM_MERCHANT_NUMBER, FIELD_MERCHANT_NUMBER);
        mapNames.put(PARAM_TRANSACTION_DATE, FIELD_TRANSACTION_DATE);
        //mapNames.put(PARAM_CREATE_AT, FIELD_CREATE_AT)
        //mapNames.put(PARAM_STATUS, FIELD_STATUS)
        mapNames.put(PARAM_DISPERSED, EnumActionTransaction.DP.getValue());
        /*
        mapNames.put(PARAM_CONCILIED, EnumActionTransaction.CC.getValue())
        mapNames.put(PARAM_PROCESSED, EnumActionTransaction.PC.getValue())
        */


        Map<String, Object> mapValues = new HashMap<>();
        mapValues.put(VALUE_MERCHANT_NUMBER, dto.getMerchantNumber());
        mapValues.put(VALUE_TRANSACTION_DATE_START, iniDate);
        mapValues.put(VALUE_TRANSACTION_DATE_END, endDate);
        /*
        mapValues.put(VALUE_CREATE_AT_START, iniDate)
        mapValues.put(VALUE_CREATE_AT_END, endDate)
        */
        mapValues.put(VALUE_DISPERSED, 1);
        //mapValues.put(VALUE_DISPERSED, Boolean.TRUE)
        /*
        mapValues.put(VALUE_CONCILIED, Boolean.TRUE)
        mapValues.put(VALUE_PROCESSED, Boolean.TRUE.toString())
        */

        StringBuilder query = new StringBuilder();
        query.append(PARAM_MERCHANT_NUMBER);
        query.append(OPR_EQUAL);
        query.append(VALUE_MERCHANT_NUMBER);
        query.append(OPR_AND);
        query.append(PARAM_TRANSACTION_DATE);
        //query.append(PARAM_CREATE_AT)
        query.append(OPR_EQUAL_MAJOR);
        query.append(VALUE_TRANSACTION_DATE_START);
        //query.append(VALUE_CREATE_AT_START)
        query.append(OPR_AND);
        query.append(PARAM_TRANSACTION_DATE);
        //query.append(PARAM_CREATE_AT)
        query.append(OPR_EQUAL_MINOR);
        query.append(VALUE_TRANSACTION_DATE_END);
        //query.append(VALUE_CREATE_AT_END)
        query.append(OPR_AND);
        query.append(PARAM_DISPERSED);
        //query.append(PARAM_STATUS + POINT + PARAM_CONCILIED)
        query.append(OPR_EQUAL);
        query.append(VALUE_DISPERSED);
        /*
        query.append(VALUE_CONCILIED)
        query.append(OPR_AND)
        query.append(PARAM_STATUS + POINT + PARAM_PROCESSED)
        query.append(OPR_EQUAL)
        query.append(VALUE_PROCESSED)
        */
        query.append("");
        query.append("");

        log.info(QUERY_LOG, query);

        ItemCollection<ScanOutcome> items =
                table.scan(query.toString(), mapNames, mapValues);

        Iterator<Item> iterator = items.iterator();

        StringBuilder rsp = new StringBuilder();

        JSONArray jsonArray;

        while (iterator.hasNext()) {
            rsp.append(iterator.next().toJSONPretty());
            rsp.append(",");
        }

        jsonArray = new JSONArray("[" + rsp.toString() + "]");

        if (!jsonArray.isEmpty()) {
            log.info(JSON_ARRAY_LOG, jsonArray);
            lst = getListSettledTransactionsByJson(jsonArray);
            return lst;
        } else {
            return Collections.emptyList();
        }

    }


    private Map<String, Map<String, Object>> getGroupStattementAccountDetail(List<TransactionsConciliationDto> lst) {
        Map<String, Map<String, Object>> groupStattementAccountDetail = new HashMap<>();
        if (!lst.isEmpty()) {
            groupStattementAccountDetail = lst.stream()
                    .collect(Collectors.groupingBy(TransactionsConciliationDto::getCreateATStr,
                            Collector.of(HashMap::new, (m, p) -> {
                                m.merge(TOTAL_SALE, p.getTransactionAmount(), (a, b) -> ((Double) a) + ((Double) b));
                                m.merge(DISCOUNT_RATE, (p.getPaymentBreakdown().getBankCommission() + p.getPaymentBreakdown().getSmartCommission()), (a, b) -> ((Double) a) + ((Double) b));
                                m.merge(IVA_DISCOUNT_RATE, (p.getPaymentBreakdown().getBankIvaCommission() + p.getPaymentBreakdown().getSmartIvaCommission()), (a, b) -> ((Double) a) + ((Double) b));
                                m.merge(PAYMENT_SALE, (
                                                p.getTransactionAmount()
                                                        - (
                                                        (p.getPaymentBreakdown().getBankCommission() + p.getPaymentBreakdown().getSmartCommission())
                                                                + (p.getPaymentBreakdown().getBankIvaCommission() + p.getPaymentBreakdown().getSmartIvaCommission())
                                                )
                                        )
                                        , (a, b) -> ((Double) a) + ((Double) b));
                            }, (m1, m2) -> {
                                m1.merge(TOTAL_SALE, m2.get(TOTAL_SALE), (a, b) -> ((Double) a) + ((Double) b));
                                m1.merge(DISCOUNT_RATE, m2.get(DISCOUNT_RATE), (a, b) -> ((Double) a) + ((Double) b));
                                m1.merge(IVA_DISCOUNT_RATE, m2.get(IVA_DISCOUNT_RATE), (a, b) -> ((Double) a) + ((Double) b));
                                m1.merge(PAYMENT_SALE, m2.get(PAYMENT_SALE), (a, b) -> ((Double) a) + ((Double) b));
                                return m1;
                            })));
            log.info("GroupStattementAccountDetail map :  {}", groupStattementAccountDetail);
            return groupStattementAccountDetail;

        } else {
            log.info("lista vacia de lst groupStattementAccountDetail");
            return groupStattementAccountDetail;
        }

    }


    private List<StatementAccountDto> getStattementAccountDetailByMap(Map<String, Map<String, Object>> mapAccount) {
        List<StatementAccountDto> lst = new ArrayList<>();
        if (!mapAccount.isEmpty()) {
            for (Entry<String, Map<String, Object>> mapDetail : mapAccount.entrySet()) {
                Map<String, Object> map = mapDetail.getValue();
                StatementAccountDto sa = new StatementAccountDto();
                DetailSaleDto dsd = new DetailSaleDto();
                sa.setDateTransaction(mapDetail.getKey());
                dsd.setDiscountRate(BigDecimal.valueOf(Double.valueOf(map.get(DISCOUNT_RATE).toString())));
                dsd.setIvaDiscountRate(BigDecimal.valueOf(Double.valueOf(map.get(IVA_DISCOUNT_RATE).toString())));
                dsd.setTotalSale(BigDecimal.valueOf(Double.valueOf(map.get(TOTAL_SALE).toString())));
                dsd.setPaymentSale(BigDecimal.valueOf(Double.valueOf(map.get(PAYMENT_SALE).toString())));
                sa.setDetailSale(dsd);
                lst.add(sa);
            }
        }
        return lst;
    }


    private Map<String, Map<String, Object>> getGroupStattementAccount(List<TransactionsConciliationDto> lst) {
        Map<String, Map<String, Object>> groupStattementAccountDetail = new HashMap<>();
        if (!lst.isEmpty()) {
            groupStattementAccountDetail = lst.stream()
                    .collect(Collectors.groupingBy(TransactionsConciliationDto::getMerchantNumber,
                            Collector.of(HashMap::new, (m, p) -> {
                                m.merge(TOTAL_SALE, p.getTransactionAmount(), (a, b) -> ((Double) a) + ((Double) b));
                                m.merge(DISCOUNT_RATE, (p.getPaymentBreakdown().getBankCommission() + p.getPaymentBreakdown().getSmartCommission()), (a, b) -> ((Double) a) + ((Double) b));
                                m.merge(IVA_DISCOUNT_RATE, (p.getPaymentBreakdown().getBankIvaCommission() + p.getPaymentBreakdown().getSmartIvaCommission()), (a, b) -> ((Double) a) + ((Double) b));
                                m.merge(PAYMENT_SALE, (
                                                p.getTransactionAmount()
                                                        - (
                                                        (p.getPaymentBreakdown().getBankCommission() + p.getPaymentBreakdown().getSmartCommission())
                                                                + (p.getPaymentBreakdown().getBankIvaCommission() + p.getPaymentBreakdown().getSmartIvaCommission())
                                                )
                                        )
                                        , (a, b) -> ((Double) a) + ((Double) b));
                            }, (m1, m2) -> {
                                m1.merge(TOTAL_SALE, m2.get(TOTAL_SALE), (a, b) -> ((Double) a) + ((Double) b));
                                m1.merge(DISCOUNT_RATE, m2.get(DISCOUNT_RATE), (a, b) -> ((Double) a) + ((Double) b));
                                m1.merge(IVA_DISCOUNT_RATE, m2.get(IVA_DISCOUNT_RATE), (a, b) -> ((Double) a) + ((Double) b));
                                m1.merge(PAYMENT_SALE, m2.get(PAYMENT_SALE), (a, b) -> ((Double) a) + ((Double) b));
                                return m1;
                            })));
            log.info("GroupStattementAccountDetail map :  {}", groupStattementAccountDetail);
            return groupStattementAccountDetail;

        } else {
            log.info("lista vacia de lst groupStattementAccountDetail");
            return groupStattementAccountDetail;
        }

    }


    private List<StatementAccountDto> getStattementAccountByMap(Map<String, Map<String, Object>> mapAccount) {
        List<StatementAccountDto> lst = new ArrayList<>();
        if (!mapAccount.isEmpty()) {
            for (Entry<String, Map<String, Object>> mapDetail : mapAccount.entrySet()) {
                Map<String, Object> map = mapDetail.getValue();
                StatementAccountDto sa = new StatementAccountDto();
                DetailSaleDto dsd = new DetailSaleDto();
                sa.setMerchantNumber(mapDetail.getKey());
                dsd.setDiscountRate(BigDecimal.valueOf(Double.valueOf(map.get(DISCOUNT_RATE).toString())));
                dsd.setIvaDiscountRate(BigDecimal.valueOf(Double.valueOf(map.get(IVA_DISCOUNT_RATE).toString())));
                dsd.setTotalSale(BigDecimal.valueOf(Double.valueOf(map.get(TOTAL_SALE).toString())));
                dsd.setPaymentSale(BigDecimal.valueOf(Double.valueOf(map.get(PAYMENT_SALE).toString())));
                sa.setDetailSale(dsd);
                lst.add(sa);
            }
        }
        return lst;
    }


    private Date convertStrDate(String ptt, String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(ptt);
        Date dateConverted = null;
        try {
            dateConverted = sdf.parse(dateStr);
        } catch (Exception e) {
            log.info("Error converted date --> {}", e.getMessage());
        }
        return dateConverted;
    }


    private void fillMapNames(Map<String, String> mapNames, String action) {
        String sufixDate = "_at";
        String prefixMapName = "#";
        String nameDate = "";
        String nameAction = "";
        if (action.equalsIgnoreCase(EnumActionTransaction.CC.getCode())) {
            nameDate = EnumActionTransaction.CC.getValue() + sufixDate;
            nameAction = EnumActionTransaction.CC.getValue();
        } else if (action.equalsIgnoreCase(EnumActionTransaction.PC.getCode())) {
            nameDate = EnumActionTransaction.PC.getValue() + sufixDate;
            nameAction = EnumActionTransaction.PC.getValue();
        }
        mapNames.put(prefixMapName + "startDate", nameDate);
        mapNames.put(prefixMapName + "endDate", nameDate);
        mapNames.put(prefixMapName + action.toLowerCase(), nameAction);
    }

    private List<TransactionsConciliationDto> getListSettledTransactionsByJson(JSONArray jsonArray) {
        List<TransactionsConciliationDto> lst = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            TransactionsConciliationDto tc = new TransactionsConciliationDto();
            PaymentBreakdownDto pb = new PaymentBreakdownDto();
            DetailDto d = new DetailDto();
            JSONObject obj = jsonArray.getJSONObject(i);
            tc.setMerchantNumber(getJsonString(obj, FIELD_MERCHANT_NUMBER));
            tc.setReferenceNumber(getJsonString(obj, "reference_number"));
            tc.setCreateAT(convertStrDate(PATTERN, obj.getString(FIELD_CREATE_AT)));
            tc.setCreateATStr(convertDateToStr("dd/MM/yyyy", tc.getCreateAT()));

            /*Seteo de Detail*/
            d.setCardType(getJsonString(obj, FIELD_CARD_TYPE));
            d.setCardBrand(getJsonString(obj, "card_brand"));
            tc.setDetail(d);

            tc.setAuthorizationNumber(getJsonString(obj, FIELD_AUTHORIZATION_NUMBER));

            /*Seteo de PaymentBreakdown*/
            pb.setBankCommission(getJsonDouble(obj, FIELD_BANK_COMMISSION));
            pb.setBankIvaCommission(getJsonDouble(obj, FIELD_BANK_IVA_COMMISSION));
            pb.setSmartCommission(getJsonDouble(obj, "transaction_fee"));
            pb.setCommissionSmart(getJsonDouble(obj, "smart_commission"));
            pb.setSmartIvaCommission(getJsonDouble(obj, "iva"));
            pb.setTotalAmountSmart(getJsonDouble(obj, "amount_to_settled"));
            tc.setPaymentBreakdown(pb);
            tc.setTransactionAmount(getJsonDouble(obj, FIELD_TRANSACTION_AMOUNT));
            tc.setRefundAmount(getJsonDouble(obj, "refund_amount"));
            tc.setTransactionDate(getJsonString(obj, FIELD_TRANSACTION_DATE));
            tc.setTransactionDateShort(convertDateToStr(PATTERN, convertStrDate(PATTERN_LARGE, obj.getString(FIELD_TRANSACTION_DATE))));
            lst.add(tc);
        }
        return lst;
    }

    /*Metodo donde se mapea el objecto principal de los atributos o nodos del json*/
    private List<TransactionsConciliationDto> getListTransactionsByJson(JSONArray jsonArray) {
        List<TransactionsConciliationDto> lst = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            TransactionsConciliationDto tc = new TransactionsConciliationDto();
            JSONObject obj = jsonArray.getJSONObject(i);
            tc.setMerchantNumber(getJsonString(obj, FIELD_MERCHANT_NUMBER));
            tc.setReferenceNumber(getJsonString(obj, "reference_number"));
            tc.setCreateAT(convertStrDate(PATTERN, obj.getString(FIELD_CREATE_AT)));
            tc.setCreateATStr(convertDateToStr("dd/MM/yyyy", tc.getCreateAT()));
            tc.setDetail(getDetailTransactionByJson(getJsonObject(obj, FIELD_DETAIL)));
            tc.setAuthorizationNumber(getJsonString(obj, FIELD_AUTHORIZATION_NUMBER));
            //tc.setAuthorizationNumber(getJsonInt(obj, FIELD_AUTHORIZATION_NUMBER))
            tc.setCardNumber(getJsonString(obj, "card_number"));
            tc.setMerchantName(getJsonString(obj, "merchant_name"));
            //tc.setPaymentBreakdown(obj.getJSONObject("payment_breakdown").toString())
            tc.setPaymentBreakdown(getPaymentBreakdownByJson(getJsonObject(obj, "payment_breakdown")));
            //tc.setStatus(obj.getJSONObject(ST).toString())
            tc.setStatus(getStatusTransactionByJson(getJsonObject(obj, FIELD_STATUS)));
            tc.setTransactionAmount(getJsonDouble(obj, FIELD_TRANSACTION_AMOUNT));
            //tc.setTransactionAmount(getJsonInt(obj, FIELD_TRANSACTION_AMOUNT))
            tc.setRefundAmount(getJsonDouble(obj, "refund_amount"));
            tc.setTransactionDate(getJsonString(obj, FIELD_TRANSACTION_DATE));
            tc.setTransactionDateShort(convertDateToStr(PATTERN, convertStrDate(PATTERN, obj.getString(FIELD_TRANSACTION_DATE))));
            tc.setFolioTxn(getJsonString(obj, FIELD_FOLIO_TXN));
            lst.add(tc);
        }
        return lst;
    }

    private PaymentBreakdownDto getPaymentBreakdownByJson(JSONObject obj) {
        PaymentBreakdownDto pb = new PaymentBreakdownDto();
        if (obj != null) {
            pb.setBankCommission(getJsonDouble(obj, FIELD_BANK_COMMISSION));
            pb.setBankIvaCommission(getJsonDouble(obj, FIELD_BANK_IVA_COMMISSION));
            pb.setSmartCommission(getJsonDouble(obj, FIELD_SMART_COMMISSION));
            pb.setCommissionSmart(getJsonDouble(obj, FIELD_COMMISSION_SMART));
            pb.setSmartIvaCommission(getJsonDouble(obj, FIELD_SMART_IVA_COMMISSION));
            pb.setTotalAmountSmart(getJsonDouble(obj, FIELD_TOTAL_AMOUNT_SMART));
            //pb.setTotal(getJsonDouble(obj, "total"))
        }
        return pb;
    }

    private StatusDto getStatusTransactionByJson(JSONObject obj) {
        StatusDto st = new StatusDto();
        if (obj != null) {
            st.setConcilied(getJsonBoolean(obj, "concilied"));
            st.setConciliedAt(getJsonString(obj, "concilied_at"));
            st.setProcessed(getJsonString(obj, "processed"));
            st.setProcessedAt(getJsonString(obj, "processed_at"));
        }
        return st;
    }

    private DetailDto getDetailTransactionByJson(JSONObject obj) {
        DetailDto dt = new DetailDto();
        if (obj != null) {
            dt.setAccessTypeIdentifier(getJsonString(obj, "access_type_identifier"));
            dt.setAdditionalAmount(getJsonString(obj, "additional_amount"));
            dt.setAttendedByAcquirerIndicator(getJsonInt(obj, "attended_by_acquirer_indicator"));
            dt.setAuthenticationMethod(getJsonString(obj, "authentication_method"));
            dt.setBranch(getJsonString(obj, "branch"));
            dt.setCardName(getJsonString(obj, "card_name"));
            dt.setCardPresenceIndicator(getJsonInt(obj, "card_presence_indicator"));
            dt.setCardType(getJsonString(obj, FIELD_CARD_TYPE));
            dt.setCardBrand(getJsonString(obj, FIELD_CARD_BRAND));
            dt.setCardholderPresenceIndicator(getJsonInt(obj, "cardholder_presence_indicator"));
            dt.setDeferredPaymentsDeferral(getJsonString(obj, "deferred_payments_deferral"));
            dt.setDeferredPaymentsNumber(getJsonString(obj, "deferred_payments_number"));
            dt.setDeferredpaymentsPlan(getJsonString(obj, "deferred_payments_plan"));
            dt.setEcommerceIndicator(getJsonInt(obj, "ecommerce_indicator"));
            dt.setFiid(getJsonString(obj, "fiid"));
            dt.setLayoutVersion(getJsonString(obj, "layout_version"));
            dt.setLogicalNetwork(getJsonString(obj, "logical_network"));
            dt.setMerchantIdentifier(getJsonString(obj, "merchant_identifier"));
            dt.setOperationKey(getJsonString(obj, "operation_key"));
            dt.setPosEntryMode(getJsonString(obj, "pos_entry_mode"));
            dt.setRegistryType(getJsonString(obj, "registry_type"));
            dt.setRejectReason(getJsonString(obj, "reject_reason"));
            dt.setRoutingIndicator(getJsonInt(obj, "routing_indicator"));
            dt.setSecurityLevelAcquirer(getJsonString(obj, "security_level_acquirer"));
            dt.setServiceCodeFlag(getJsonString(obj, "service_code_flag"));
            dt.setSpeiIndicator(getJsonInt(obj, "spei_indicator"));
            dt.setStatus(getJsonString(obj, FIELD_STATUS));
            dt.setStatusIndicator(getJsonInt(obj, "status_indicator"));
            dt.setTerminalActivationByCardholder(getJsonInt(obj, "terminal_activation_by_cardholder"));
            dt.setTerminalIdentifier(getJsonString(obj, "terminal_identifier"));
            dt.setTransactionTime(getJsonString(obj, "transaction_time"));
            dt.setRefSgNumber(getJsonString(obj, "refSgNumber"));
            dt.setRefSpNumber(getJsonString(obj, FIELD_REF_SP_NUMBER));
            dt.setApprovalCode(getJsonString(obj, "approvalCode"));
        }
        return dt;
    }

    private List<TransactionsConciliationHeaderDto> getTotalsByMerchant(List<String> lstMerchant,
                                                                        Map<String, Double> totalAmount, Map<String, Long> totalEntry) {
        List<TransactionsConciliationHeaderDto> lstTch = new ArrayList<>();
        for (String m : lstMerchant) {
            TransactionsConciliationHeaderDto tch = new TransactionsConciliationHeaderDto();
            tch.setMerchantNumber(m);
            tch.setTotalAmount(totalAmount.get(m).intValue());
            tch.setTotalEntry(totalEntry.get(m).intValue());
            lstTch.add(tch);
        }
        return lstTch;
    }

    public List<String> getMerchantNumbersByDetail(List<ClientAffiliationResponseDto> lstCl) {
        List<String> lst = new ArrayList<>();

        Map<String, Double> sumByClient = lstCl.stream().collect(
                Collectors.groupingBy(ClientAffiliationResponseDto::getMerchantNumber, Collectors.summingDouble(ClientAffiliationResponseDto::getAmount)));
        for (Entry<String, Double> entry : sumByClient.entrySet()) {
            lst.add(entry.getKey());
        }
        return lst;
    }

    public List<IdentifiersSqlToDynamo> getMerchantsAndFoliosByDetail(List<ClientAffiliationResponseDto> lstCl) {
        List<IdentifiersSqlToDynamo> lst = new ArrayList<>();
        Map<String, List<String>> mapFolios =
                lstCl.stream()
                        .collect(Collectors.groupingBy(ClientAffiliationResponseDto::getMerchantNumber,
                                Collectors.mapping(ClientAffiliationResponseDto::getFolioTxn, Collectors.toList())
                        ));

        for (Entry<String, List<String>> entry : mapFolios.entrySet()) {
            IdentifiersSqlToDynamo identifier = new IdentifiersSqlToDynamo();
            identifier.setMerchantNumber(entry.getKey());
            identifier.setFolioTxn(entry.getValue());
            lst.add(identifier);
        }

        return lst;

    }
    
    public List<String> getFolioByDetail(List<ClientAffiliationResponseDto> list) {
      List<String> lst = new ArrayList<>();

      Map<String, Long> resultMap = list.stream().collect(
        Collectors.groupingBy(ClientAffiliationResponseDto::getFolioTxn, Collectors.counting()));

      for (Entry<String, Long> entry : resultMap.entrySet()) {
        lst.add(entry.getKey());
      }

      return lst;
    }

    public List<ClientAffiliationResponseDto> mergeListClientTransactions(List<ClientAffiliationResponseDto> clients, List<TransactionsConciliationDto> conciliations) {
        List<ClientAffiliationResponseDto> lst = new ArrayList<>();

        for (int i = 0; i < conciliations.size(); i++) {
            TransactionsConciliationDto ct = conciliations.get(i);
            ClientAffiliationResponseDto dto = new ClientAffiliationResponseDto();
            dto.setReferenceNumber(ct.getReferenceNumber());
            dto.setCardNumber(ct.getCardNumber());
            if (clients.size() >= conciliations.size()) {
                dto.setFolioTxn(clients.get(i).getFolioTxn());
                dto.setTransactionDate(clients.get(i).getTransactionDate());
                dto.setTransactionHour(clients.get(i).getTransactionHour());
                dto.setOperationTypeId(clients.get(i).getOperationTypeId());
                //dto.setAmount(clients.get(i).getAmount())
                dto.setProductId(clients.get(i).getProductId());
                dto.setProductDescription(clients.get(i).getProductDescription());
                dto.setPaymentStatus(clients.get(i).getPaymentStatus());
                dto.setAuthorizerReplyMessage(clients.get(i).getAuthorizerReplyMessage());
                dto.setReturnOperation(clients.get(i).getReturnOperation());
                dto.setMerchantNumber(clients.get(i).getMerchantNumber());
                dto.setTransmitter(clients.get(i).getTransmitter());
            } else {
                for (ClientAffiliationResponseDto cl : clients) {
                    dto.setFolioTxn(cl.getFolioTxn());
                    dto.setTransactionDate(cl.getTransactionDate());
                    dto.setTransactionHour(cl.getTransactionHour());
                    dto.setOperationTypeId(cl.getOperationTypeId());
                    //dto.setAmount(cl.getAmount())
                    dto.setProductId(cl.getProductId());
                    dto.setProductDescription(cl.getProductDescription());
                    dto.setPaymentStatus(cl.getPaymentStatus());
                    dto.setAuthorizerReplyMessage(cl.getAuthorizerReplyMessage());
                    dto.setReturnOperation(cl.getReturnOperation());
                    dto.setMerchantNumber(cl.getMerchantNumber());
                    dto.setTransmitter(cl.getTransmitter());
                }
            }

            lst.add(dto);
        }

        return lst;
    }

    public List<ClientAffiliationResponseDto> mergeJoinListClientTransactions(
            List<ClientAffiliationResponseDto> clients,
            List<TransactionsConciliationDto> conciliations) {

        List<ClientAffiliationResponseDto> lst = new ArrayList<>();

        if (!clients.isEmpty() && conciliations.isEmpty()) {

            for (ClientAffiliationResponseDto c : clients) {

                ClientAffiliationResponseDto dto = new ClientAffiliationResponseDto();
                dto.setFolioTxn(c.getFolioTxn());
//                dto.setTransactionDate(c.getTransactionDate());
//                dto.setTransactionHour(c.getTransactionHour());
                dto.setOperationTypeId(c.getOperationTypeId());
                dto.setOperationType(c.getOperationType());
                //dto.setAmount(c.getAmount())
                dto.setAmountStr(c.getAmountStr());
                dto.setProductId(c.getProductId());
                dto.setProductDescription(c.getProductDescription());
                dto.setPaymentStatus(c.getPaymentStatus());
                dto.setAuthorizerReplyMessage(c.getAuthorizerReplyMessage());
                dto.setReturnOperation(c.getReturnOperation());
                dto.setMerchantNumber(c.getMerchantNumber());
                if(!c.getTransmitter().equals("null")) {
                    dto.setTransmitter(c.getTransmitter());
                }else{
                    dto.setTransmitter("NA");
                }
                
                dto.setRefSpNumber(c.getRefSpNumber());
                validAddListMerchantsAndFolio(conciliations, dto, c.getMerchantNumber(), c.getFolioTxn());
                String dateStr = (dto.getTransactionDate() != null) ? dto.getTransactionDate(): c.getTransactionDate();
                dto.setTransactionDate(dateStr);
                lst.add(dto);

            }

        } else if (!clients.isEmpty() && !conciliations.isEmpty()) {

            for (ClientAffiliationResponseDto c : clients) {
                ClientAffiliationResponseDto dto = new ClientAffiliationResponseDto();
                if (Boolean.TRUE.equals(validListMerchantsAndFolio(conciliations, c.getMerchantNumber(), c.getFolioTxn()))) {

//                    ClientAffiliationResponseDto dto = new ClientAffiliationResponseDto();
                    dto.setFolioTxn(c.getFolioTxn());
//                    dto.setTransactionDate(c.getTransactionDate());
//                    dto.setTransactionHour(c.getTransactionHour());
                    dto.setOperationTypeId(c.getOperationTypeId());
                    dto.setOperationType(c.getOperationType());
                    //dto.setAmount(c.getAmount())
                    dto.setAmountStr(c.getAmountStr());
                    dto.setProductId(c.getProductId());
                    dto.setProductDescription(c.getProductDescription());
                    dto.setPaymentStatus(c.getPaymentStatus());
                    dto.setAuthorizerReplyMessage(c.getAuthorizerReplyMessage());
                    dto.setReturnOperation(c.getReturnOperation());
                    dto.setMerchantNumber(c.getMerchantNumber());
                    if(!c.getTransmitter().equals("null")) {
                        dto.setTransmitter(c.getTransmitter());
                    }else{
                        dto.setTransmitter("NA");
                    }

                    dto.setRefSpNumber(c.getRefSpNumber());
                    validAddListMerchantsAndFolio(conciliations, dto, c.getMerchantNumber(), c.getFolioTxn());
                    String dateStr = (dto.getTransactionDate() != null) ? dto.getTransactionDate(): c.getTransactionDate();
                    dto.setTransactionDate(dateStr);
                    lst.add(dto);

                } else {

                    dto.setFolioTxn(c.getFolioTxn());
//                    dto.setTransactionDate(c.getTransactionDate());
//                    dto.setTransactionHour(c.getTransactionHour());
                    dto.setOperationTypeId(c.getOperationTypeId());
                    dto.setOperationType(c.getOperationType());
                    //dto.setAmount(c.getAmount())
                    dto.setAmountStr(c.getAmountStr());
                    dto.setProductId(c.getProductId());
                    dto.setProductDescription(c.getProductDescription());
                    dto.setPaymentStatus(c.getPaymentStatus());
                    dto.setAuthorizerReplyMessage(c.getAuthorizerReplyMessage());
                    dto.setReturnOperation(c.getReturnOperation());
                    dto.setMerchantNumber(c.getMerchantNumber());
                    if(!c.getTransmitter().equals("null")) {
                        dto.setTransmitter(c.getTransmitter());
                    }else{
                        dto.setTransmitter("NA");
                    }

                    
                    dto.setRefSpNumber(c.getRefSpNumber());
                    validAddListMerchantsAndFolio(conciliations, dto, c.getMerchantNumber(), c.getFolioTxn());
                    String dateStr = (dto.getTransactionDate() != null) ? dto.getTransactionDate(): c.getTransactionDate();
                    dto.setTransactionDate(dateStr);
                    lst.add(dto);
                }
            }
        }

        return lst;
    }
    
    public List<ClientAffiliationResponseDto> mergeJoinListClientTransactionsByResponseCode(
      List<ClientAffiliationResponseDto> clients,
      List<TransactionsConciliationDto> conciliations) {

      List<ClientAffiliationResponseDto> lst = new ArrayList<>();

      for (ClientAffiliationResponseDto c : clients) {
        if (Boolean.TRUE.equals(validListByFolios(conciliations, c.getFolioTxn()))) {
          ClientAffiliationResponseDto dto = new ClientAffiliationResponseDto();
          dto.setFolioTxn(c.getFolioTxn());
          dto.setTransactionDate(c.getTransactionDate());
          dto.setTransactionHour(c.getTransactionHour());
          dto.setOperationTypeId(c.getOperationTypeId());
          dto.setOperationType(c.getOperationType());
          //dto.setAmount(c.getAmount())
          dto.setAmountStr(c.getAmountStr());
          dto.setProductId(c.getProductId());
          dto.setProductDescription(c.getProductDescription());
          dto.setPaymentStatus(c.getPaymentStatus());
          dto.setAuthorizerReplyMessage(c.getAuthorizerReplyMessage());
          dto.setReturnOperation(c.getReturnOperation());
          dto.setMerchantNumber(c.getMerchantNumber());
          dto.setTransmitter(c.getTransmitter());
          validAddListByFolios(conciliations, dto, c.getFolioTxn());
          lst.add(dto);
        }
      }

      return lst;
    }

    public Boolean validListMerchantsAndFolio(List<TransactionsConciliationDto> lst, String merchants, String folio) {
        Boolean answer = Boolean.FALSE;
        for (TransactionsConciliationDto tc : lst) {
            if (merchants.equalsIgnoreCase(tc.getMerchantNumber()) && folio.equalsIgnoreCase(tc.getFolioTxn())) {
                answer = Boolean.TRUE;
            }
        }
        return answer;
    }

    public void validAddListMerchantsAndFolio(List<TransactionsConciliationDto> lstTC, ClientAffiliationResponseDto dto, String merchants, String folio) {

        for (TransactionsConciliationDto tc : lstTC) {
        	
            if (merchants.equalsIgnoreCase(tc.getMerchantNumber()) && folio.equalsIgnoreCase(tc.getFolioTxn())) {
                dto.setReferenceNumber(tc.getReferenceNumber());
                dto.setRefSgNumber(tc.getDetail().getRefSgNumber());
                dto.setTransactionDate(tc.getTransactionDate());
                dto.setApprovalCode(tc.getDetail().getApprovalCode());
//                dto.setRefSpNumber(tc.getDetail().getRefSpNumber());
            }
        }

    }
    
    public Boolean validListByFolios(List<TransactionsConciliationDto> lst, String folio) {
      Boolean answer = Boolean.FALSE;
      for (TransactionsConciliationDto tc : lst) {
          if (folio.equalsIgnoreCase(tc.getFolioTxn())) {
              answer = Boolean.TRUE;
          }
      }
      return answer;
  }
    
    public void validAddListByFolios(List<TransactionsConciliationDto> lstTC, ClientAffiliationResponseDto dto, String folio) {

      for (TransactionsConciliationDto tc : lstTC) {
          if (folio.equalsIgnoreCase(tc.getFolioTxn())) {
              dto.setReferenceNumber(tc.getReferenceNumber());
              dto.setRefSgNumber(tc.getDetail().getRefSgNumber());
              dto.setRefSpNumber(tc.getDetail().getRefSpNumber());
          }
      }

  }

    public List<ClientAffiliationResponseDetailDto> mergeJoinListTransactionsDetail(
            List<ClientAffiliationResponseDetailDto> clients,
            List<TransactionsConciliationDto> conciliations) {
        List<ClientAffiliationResponseDetailDto> lst = new ArrayList<>();
        if (!clients.isEmpty() && conciliations.isEmpty()) {
            for (ClientAffiliationResponseDetailDto c : clients) {

                ClientAffiliationResponseDetailDto dto = c;
                validAddListTransactionsDetailByFolio(conciliations, dto, c.getFolioTxn());
                lst.add(dto);

            }
        } else if (!clients.isEmpty() && !conciliations.isEmpty()) {

            for (ClientAffiliationResponseDetailDto c : clients) {
                ClientAffiliationResponseDetailDto dto = c;

                if (Boolean.TRUE.equals(validListTransactionsDetailByFolio(conciliations, c.getFolioTxn()))) {

//                    ClientAffiliationResponseDetailDto dto = c;
                    validAddListTransactionsDetailByFolio(conciliations, dto, c.getFolioTxn());
                    lst.add(dto);

                }else{

//                    ClientAffiliationResponseDetailDto dto = c;
                    validAddListTransactionsDetailByFolio(conciliations, dto, c.getFolioTxn());
                    lst.add(dto);

                }
            }
        }
        log.info("list transaction detail: {}", lst.size());
        return lst;
    }

    public Boolean validListTransactionsDetailByFolio
            (List<TransactionsConciliationDto> lst, String folio) {

        Boolean answer = Boolean.FALSE;
        for (TransactionsConciliationDto tc : lst) {
            if (folio.equalsIgnoreCase(tc.getFolioTxn())) {
                answer = Boolean.TRUE;
            }
        }
        return answer;
    }

    public void validAddListTransactionsDetailByFolio
            (List<TransactionsConciliationDto> lstTC, ClientAffiliationResponseDetailDto dto, String folio) {

        for (TransactionsConciliationDto tc : lstTC) {
            if (folio.equalsIgnoreCase(tc.getFolioTxn())) {
                dto.setReferenceNumber(tc.getReferenceNumber());
                dto.setRefSgNumber(tc.getDetail().getRefSgNumber());
                dto.setRefSpNumber(tc.getDetail().getRefSpNumber());
//                dto.setCardType(tc.getDetail().getCardType());
                dto.setAuthorizationNumber(tc.getAuthorizationNumber());
            }
        }
    }

    private String getQueryInByListMerchants(List<String> mns, String nameValue) {
      StringBuilder query = new StringBuilder();
      if (!mns.isEmpty()) {
        for (int i = 0; i < mns.size(); i++) {
          query.append(nameValue + UNDERSCORE + i);
          query.append(",");
        }    
      }else {
        query.append(nameValue + UNDERSCORE + 0);
        query.append(",");
      }

      return query.substring(0, query.length()-1);
    }

    private void fillPutMapValueQuery(List<String> mns, String nameValue, Map<String, Object> mapValues) {
      if (!mns.isEmpty()) {
        for (int i = 0; i < mns.size(); i++) {
          mapValues.put(nameValue + UNDERSCORE + i, mns.get(i));
        }
      }else {
        mapValues.put(nameValue + UNDERSCORE + 0, 0);
      }
    }
    
    public TransactionsConciliationDto setTransactionsConciliationByJsonNode(JsonNode jsonNode) {
      TransactionsConciliationDto tc = new TransactionsConciliationDto();
      if (jsonNode != null) {
        JsonNode childNodePB = jsonNode.get(FIELD_PAYMENT_BREAKDOWN);
        JsonNode childNodeD = jsonNode.get(FIELD_DETAIL);

        PaymentBreakdownDto pb = new PaymentBreakdownDto();
        DetailDto d = new DetailDto();
        tc.setMerchantNumber(getJsonNodeString(jsonNode, FIELD_MERCHANT_NUMBER));
        tc.setReferenceNumber(getJsonNodeString(jsonNode, FIELD_REFERENCE_NUMBER));
        tc.setCreateATStr(getJsonNodeString(jsonNode, FIELD_CREATED_AT));
        tc.setTransactionAmount(getJsonNodeDouble(jsonNode, FIELD_TRANSACTION_AMOUNT));
        tc.setFolioTxn(getJsonNodeString(jsonNode, FIELD_FOLIO_TXN));
        String transactionDateStr = getJsonNodeString(jsonNode, FIELD_TRANSACTION_DATE);
        Date transactionDatDt = convertStrDate(PATTERN_LARGE, transactionDateStr); 
        tc.setTransactionDate(convertDateToStr(PATTERN, transactionDatDt));
        pb.setAmountDepositedSmart(getJsonNodeDouble(childNodePB, FIELD_AMOUNT_DEPOSITED_SMART));
        tc.setPaymentBreakdown(pb);
        tc.setAmountDeposited(getJsonNodeInt(jsonNode, FIELD_AMOUNT_DEPOSITED));
        tc.setAmountTreasury(getJsonNodeDouble(jsonNode, FIELD_AMOUNT_TREASURY));
        d.setRefSgNumber(getJsonNodeString(childNodeD, "refSgNumber"));
        d.setRefSpNumber(getJsonNodeString(childNodeD, FIELD_REF_SP_NUMBER));
        tc.setDetail(d);
      }
      return tc;
    }

    @Override
    public GenericResponse getClient(InfoTokenDto infoTokenDto) {
        GenericResponse response = new GenericResponse();
        Integer status = 1;
        List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());

        try {

            List<Client> results = clientRepository.findAllByStatusId(status);

            if (!results.isEmpty()) {
                //'if' (results.size() > 0) '{'

                response.setCodeStatus("00");
                response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                Map<String, Object> information = new HashMap<>();
                information.put(KEY_RESULTS, results);
                response.setInformation(information);

            } else {

                response.setCodeStatus("01");
                response.setMessage(ErrorCode.getError(listCodes, "01").getMessage());

            }

            return response;

        } catch (Exception e) {

            response.setCodeStatus("03");
            response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()));
            e.printStackTrace();
            return response;

        }
    }
    
    public List<Integer> getFolios(List<IdentifiersSqlToDynamo> lst) {
      List<Integer> rs = new ArrayList<>();
      for (IdentifiersSqlToDynamo cl : lst) {
        for (String s : cl.getFolioTxn()) {
          String[] folio = s.split("\\_");
          Integer numFolio = 0;
          if (folio.length > 1) {
           numFolio = Integer.parseInt(folio[1]);
          }
          rs.add(numFolio);
        }
        
      }
      return rs;
    }
    
    public List<String> getFoliosStr(List<IdentifiersSqlToDynamo> lst) {
      List<String> rs = new ArrayList<>();
      for (IdentifiersSqlToDynamo cl : lst) {
        for (String s : cl.getFolioTxn()) {
          rs.add(s);
        }
      }
      return rs;
    }
    
    public String setCifrasNumber(Integer num, String cifras) {    
      Integer len = num.toString().length();
      Integer lenCifras = cifras.length();
      String newCifras = "";
      if (lenCifras > len) {
        newCifras = cifras.substring(0, lenCifras - len);
      }
      return newCifras + num.toString();
    }
    
    public List<String> getMerchants(List<IdentifiersSqlToDynamo> mns){
      List<String> lst =  new ArrayList<>();
      for (IdentifiersSqlToDynamo s : mns) {
        lst.add(s.getMerchantNumber());
      }
      return lst;
    }
    
    public String getMinFolio(List<String> foliosStr) {
      String fl = "";
      if (!foliosStr.isEmpty()) {
        fl = foliosStr.get(0);
      }
      return fl;
    }
    
    public String getMaxFolio(List<String> foliosStr) {
      String fl = "";
      if (!foliosStr.isEmpty()) {
        Integer lstSize = foliosStr.size() - 1;
        fl = foliosStr.get(lstSize);
      }
      return fl;
    }

}