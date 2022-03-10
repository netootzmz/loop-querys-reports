package com.smart.ecommerce.queries.service.impl;

import static com.smart.ecommerce.queries.util.Constants.*;
import static com.smart.ecommerce.queries.util.ConvertDates.convertDateToStr;
import static com.smart.ecommerce.queries.util.GetValuesJsonUtils.getJsonBoolean;
import static com.smart.ecommerce.queries.util.GetValuesJsonUtils.getJsonDouble;
import static com.smart.ecommerce.queries.util.GetValuesJsonUtils.getJsonInt;
import static com.smart.ecommerce.queries.util.GetValuesJsonUtils.getJsonObject;
import static com.smart.ecommerce.queries.util.GetValuesJsonUtils.getJsonString;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;

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
import com.smart.ecommerce.entity.core.CoreErrorCode;
import com.smart.ecommerce.queries.exception.SmartServicePlatformMyException;
import com.smart.ecommerce.queries.model.dto.ClientAffiliationResponseDto;
import com.smart.ecommerce.queries.model.dto.DetailDto;
import com.smart.ecommerce.queries.model.dto.IdentifiersSqlToDynamo;
import com.smart.ecommerce.queries.model.dto.InfoTokenDto;
import com.smart.ecommerce.queries.model.dto.PaymentBreakdownDto;
import com.smart.ecommerce.queries.model.dto.StatusDto;
import com.smart.ecommerce.queries.model.dto.TransactionsConciliationDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailOperationDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsAmountAndCommisionsServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsCardServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsClientServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsInformationServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsPromissoryNoteServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsServPtalDto;
import com.smart.ecommerce.queries.repository.ErrorCodeRepository;
import com.smart.ecommerce.queries.repository.TransactionsServPtalRepository;
import com.smart.ecommerce.queries.service.TransactionsServPtalService;
import com.smart.ecommerce.queries.util.ErrorCode;
import com.smart.ecommerce.queries.util.GenericResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <code>TransactionsServPtalServiceImpl</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */
@Slf4j
@Service("TransactionsServPtalService")
public class TransactionsServPtalServiceImpl implements TransactionsServPtalService {
  
  @Resource private TransactionsServPtalRepository transactionsDetailRepository;

  @Resource private ErrorCodeRepository codeDao;

  @Autowired private DynamoDB dynamoDB;
  
  private static final String TABLE_ONE = "transactions_conciliation";
  private static final String PATTERN = "yyyy-MM-dd";
  private static final String PATTERN_LONG = "yyyy-MM-dd HH:mm:ss";
  private static final String FIELD_STATUS = "status";

  private static final String PARAM_MERCHANT_NUMBER = "#p_merchant_number";
  private static final String FIELD_MERCHANT_NUMBER = "merchant_number";
  private static final String VALUE_MERCHANT_NUMBER = ":v_merchant_number";

  private static final String VALUE_FOLIO_TXN = ":v_folio_txn";
  private static final String PARAM_FOLIO_TXN = "#p_folio_txn";
  private static final String FIELD_FOLIO_TXN = "folio_txn";
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

  public  static final String PARAM_TRANSACTION_DATE = "#p_transaction_date";
  private static final String FIELD_TRANSACTION_DATE = "transaction_date";
  public  static final String VALUE_TRANSACTION_DATE_START = ":v_transaction_date_start";
  public  static final String VALUE_TRANSACTION_DATE_END = ":v_transaction_date_end";
  
  public  static final String PARAM_REFERENCE_NUMBER = "#p_reference_number";
  public  static final String VALUE_REFERENCE_NUMBER = ":v_reference_number";

  public  static final String PARAM_CONCILIED = "#p_concilied";
  public  static final String PARAM_PROCESSED = "#p_processed";
  private static final String FIELD_CREATE_AT = "created_at";
  public  static final String PARAM_CREATE_AT = "#p_create_at";
  public  static final String VALUE_CREATE_AT_START = ":v_create_at_start";
  public  static final String VALUE_CREATE_AT_END = ":v_create_at_end";
  public  static final String VALUE_CONCILIED = ":v_concilied";
  public  static final String VALUE_PROCESSED = ":v_processed";

  private static final String FIELD_BANK_COMMISSION = "bank_commission";
  private static final String FIELD_BANK_IVA_COMMISSION = "bank_iva_commission";
  private static final String FIELD_SMART_COMMISSION = "amount_rate_smart";
  private static final String FIELD_SMART_IVA_COMMISSION = "amount_iva_smart";
  private static final String FIELD_COMMISSION_SMART = "commission_smart";
  private static final String FIELD_TRANSACTION_AMOUNT = "transaction_amount";
  private static final String FIELD_TOTAL_AMOUNT_SMART = "total_amount_smart";
  private static final String FIELD_PAYMENT_BREAKDOWN = "payment_breakdown";
  private static final String FIELD_REFERENCE_NUMBER = "reference_number";
  private static final String KEY_RESULTS = "results";
  private static final String LOG_ERROR_TRACE = "Error: ";

  @Override
  public GenericResponse getTransactionsServPtal(String idOperation, TransactionsServPtalDto dto, InfoTokenDto infoTokenDto) {
    GenericResponse response = new GenericResponse();
    List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
    try {

      List<ClientAffiliationResponseDto> results = resultsListDto(dto);

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
      StringBuilder errorStack = new StringBuilder();
      for (StackTraceElement er : e.getStackTrace()) {
        errorStack.append(LOG_ERROR_TRACE);
        errorStack.append(er.getClassName() + SPACE);
        errorStack.append(er.getFileName() + SPACE);
        errorStack.append(er.getLineNumber() + SPACE);
        errorStack.append(er.getMethodName() + SPACE);
        errorStack.append("");
      }
      String errorTrace = errorStack.toString();
      log.info("Error Transaction ServPtal: {}", errorTrace);
      response.setCodeStatus("03");
      //response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()))
      response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), errorTrace));
      e.printStackTrace();
      return response;

    }
  }

  public List<ClientAffiliationResponseDto> resultsListDto(TransactionsServPtalDto dto) throws SmartServicePlatformMyException {
    try {

      List<ClientAffiliationResponseDto> results;
      List<ClientAffiliationResponseDto> lst;

      List<ClientAffiliationResponseDto> lstClient = transactionsDetailRepository.getTransactionsServPtalWithSP(dto);
      //List<ClientAffiliationResponseDto> lstClient = transactionsDetailRepository.getTransactionsServPtalRepository(dto)
      List<IdentifiersSqlToDynamo> mns = getMerchantsAndFoliosByDetail(lstClient);
      List<TransactionsConciliationDto> lstTc = getTransactionsConciliationsByMechantAndFolio(mns /*, dto */);

      log.info("size clients: {}", lstClient.size());
      log.info("size transactions: {}", lstTc.size());
      if (!lstClient.isEmpty() && !lstTc.isEmpty()) {
        lst = mergeJoinSqlDynamoTransactions(lstClient, lstTc);
        //lst.forEach(p-> log.info("Transaction Date FUll: {}", p.getTransactionDate()))
        results = getListFiltered(lst, dto);
        
        //results = mergeJoinSqlDynamoTransactions(lstClient, lstTc)
        //results = mergeJoinListClientTransactions(lstClient, lstTc)
      }else if (!lstClient.isEmpty() && lstTc.isEmpty()) {
        lst = lstClient;
        /*
        lst.forEach(p-> '{'
          log.info("Transaction Date FUll: {}", p.getTransactionDate())
          log.info("Transaction Date Length: {}", p.getTransactionDate().length())
        })
        */
        results = getListFiltered(lst, dto);
        
        //results = lstClient
      } else {
        results = Collections.emptyList();      
      }

      return results;
    } catch (Exception e) {
      StringBuilder errorStack = new StringBuilder();
      for (StackTraceElement er : e.getStackTrace()) {
        errorStack.append(LOG_ERROR_TRACE);
        errorStack.append(er.getClassName() + SPACE);
        errorStack.append(er.getFileName() + SPACE);
        errorStack.append(er.getLineNumber() + SPACE);
        errorStack.append(er.getMethodName() + SPACE);
        errorStack.append("");
      }
      String errorTrace = errorStack.toString();
      log.error("Errro: {}", errorTrace);
      throw new SmartServicePlatformMyException("error get list mariaDB and dynamoDB: " + errorTrace);
    }
  }
  
  public List<ClientAffiliationResponseDto> getListFiltered(List<ClientAffiliationResponseDto> lst, TransactionsServPtalDto dto){
    List<ClientAffiliationResponseDto> lstfiltering = lst;
    List<ClientAffiliationResponseDto> lstfiltered;
    
    lstfiltering = getListFilterReference(lstfiltering, dto.getPaymentReference());
    lstfiltering = getListFilterEntryMode(lstfiltering, dto.getPosEntryModeId());
    /** Verificar el filtrado de fecha **/
    //lstfiltering = getListFilterTransactionDate(lstfiltering, dto.getInitDate(), dto.getEndDate())
    
    lstfiltered= lstfiltering;
    
    return lstfiltered;
  }
  
  public List<ClientAffiliationResponseDto> getListFilterReference(List<ClientAffiliationResponseDto> lst, String param){
    List<ClientAffiliationResponseDto> lstFiltered;
    if (null != param && StringUtils.isNotEmpty(param)) {
      lstFiltered = lst.stream()
        //.filter(c -> filteredListStrParam(param, c.getReferenceNumber()))
        .filter(c -> filteredListStrParam(param, c.getRefSpNumber()))
        .collect(Collectors.toList());
    }else {
      lstFiltered = lst;
    }
    return lstFiltered;
  }
  
  public List<ClientAffiliationResponseDto> getListFilterEntryMode(List<ClientAffiliationResponseDto> lst, Integer param){
    List<ClientAffiliationResponseDto> lstFiltered;
    if (null != param ) {
      lstFiltered = lst.stream()
        .filter(c -> filteredListIntParam(param, c.getPosEntryModeId()))
        .collect(Collectors.toList());
    }else {
      lstFiltered = lst;
    }
    return lstFiltered;
  }
  
  public List<ClientAffiliationResponseDto> getListFilterTransactionDate(List<ClientAffiliationResponseDto> lst, String start, String end){
    List<ClientAffiliationResponseDto> lstFiltered;
    if (StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {
      LocalDate startDate = parseStringLocalDate(start + START_HOURS, PATTERN_LONG).plusDays(-1);
      LocalDate endDate = parseStringLocalDate(end + END_HOURS, PATTERN_LONG).plusDays(1);
      lstFiltered = lst.stream()
        .filter(c -> parseStringDatePattern(c.getTransactionDate()).isAfter(startDate) && parseStringDatePattern(c.getTransactionDate()).isBefore(endDate))
        //.filter(c -> c.getTransactionDateFull().isAfter(startDate) && c.getTransactionDateFull().isBefore(endDate))
        .collect(Collectors.toList());
    }else {
      lstFiltered = lst;
    }
    return lstFiltered;
  }

  public Boolean filteredListStrParam(String param, String value) {
    return param.equalsIgnoreCase(value);
  }

  public Boolean filteredListIntParam(Integer param, Integer value) {
    return param.equals(value);
  }
  
  public LocalDate parseStringLocalDate(String date, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    //LocalDate localDate = LocalDate.parse(date, formatter)
    //log.info("String To LocalDate: {}", localDate)
    return LocalDate.parse(date, formatter);
  }
  
  public LocalDate parseStringDatePattern(String date) {
    if (date.length() > 10) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_LONG);
      return LocalDate.parse(date, formatter);
    }else {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);
      return LocalDate.parse(date, formatter);
    }
    
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

  public List<TransactionsConciliationDto> getTransactionsConciliationsByMechantAndFolio(List<IdentifiersSqlToDynamo> mns /* , TransactionsServPtalDto dto */) {
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
      
      //String starDate = StringUtils.isNotEmpty(dto.getTransactionStartDate()) ? dto.getTransactionStartDate() + START_HOURS  : ""
      //String endDate = StringUtils.isNotEmpty(dto.getTransactionEndDate()) ? dto.getTransactionEndDate() + END_HOURS : ""

      Map<String, String> expressionMapNames = new HashMap<>();
      expressionMapNames.putAll(new NameMap().with(PARAM_MERCHANT_NUMBER, FIELD_MERCHANT_NUMBER));
      expressionMapNames.putAll(new NameMap().with(PARAM_FOLIO_TXN, FIELD_FOLIO_TXN));
      //validateDateParamStrExpMapName(expressionMapNames, PARAM_TRANSACTION_DATE, FIELD_TRANSACTION_DATE, dto)
      //validateParamStrExpMapName(expressionMapNames, PARAM_REFERENCE_NUMBER, FIELD_REFERENCE_NUMBER, dto.getReferenceNumber())


      List<String> merchants = getMerchants(mns);

      Map<String, Object> expressionMapValues = new HashMap<>();
      //validateParamStrExpMapValue(expressionMapValues, VALUE_TRANSACTION_DATE_START, starDate)
      //validateParamStrExpMapValue(expressionMapValues, VALUE_TRANSACTION_DATE_END, endDate)
      //validateParamStrExpMapValue(expressionMapValues, VALUE_REFERENCE_NUMBER, dto.getReferenceNumber())

      String valueReferenceInQuery = getQueryInByListMerchants(merchants, VALUE_MERCHANT_NUMBER);

      expressionMapValues.putAll(new ValueMap().with(VALUE_FOLIO_TXN_START, folioMin));
      expressionMapValues.putAll(new ValueMap().with(VALUE_FOLIO_TXN_END, folioMax));
      fillPutMapValueExpression(merchants, VALUE_MERCHANT_NUMBER, expressionMapValues);

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
      /*
      query.append(
      validateQuery(
        OPR_AND + SPACE
        + PARAM_TRANSACTION_DATE + SPACE
        + OPR_EQUAL_MAJOR + SPACE
        + VALUE_TRANSACTION_DATE_START
        , dto.getTransactionStartDate())
      )
      query.append(
        validateQuery(
          OPR_AND + SPACE
          + PARAM_TRANSACTION_DATE + SPACE
          + OPR_EQUAL_MINOR + SPACE
          + VALUE_TRANSACTION_DATE_END
          , dto.getTransactionStartDate())
        )
      
      query.append(
        validateQuery(
          OPR_AND + SPACE
          + PARAM_REFERENCE_NUMBER + SPACE
          + OPR_EQUAL + SPACE
          + VALUE_REFERENCE_NUMBER
          , dto.getReferenceNumber())
        )
      */
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
  
  public void validateDateParamStrExpMapName( Map<String, String> expressionMapNames, String key, String name, TransactionsServPtalDto dto) {
    if (StringUtils.isNotEmpty(dto.getTransactionStartDate())
      && StringUtils.isNotEmpty(dto.getTransactionEndDate())
      ) {
      expressionMapNames.putAll(new NameMap().with(key, name));
    }
  }
  
  public void validateParamStrExpMapName( Map<String, String> expressionMapNames, String key, String name, String param) {
    if (StringUtils.isNotEmpty(param) ) {
      expressionMapNames.putAll(new NameMap().with(key, name));
    }
  }
  
  public void validateParamStrExpMapValue( Map<String, Object> expressionMapValues, String key, String param) {
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

  public List<ClientAffiliationResponseDto> mergeJoinSqlDynamoTransactions( List<ClientAffiliationResponseDto> clients,
    List<TransactionsConciliationDto> conciliations) {
    List<ClientAffiliationResponseDto> lst = new ArrayList<>();
    for (ClientAffiliationResponseDto c : clients) {

      ClientAffiliationResponseDto dto = new ClientAffiliationResponseDto();
      dto.setFolioTxn(c.getFolioTxn());
      dto.setOperationTypeId(c.getOperationTypeId());
      dto.setOperationType(c.getOperationType());
      dto.setAmountStr(c.getAmountStr());
      dto.setProductId(c.getProductId());
      dto.setProductDescription(c.getProductDescription());
      dto.setPaymentStatus(c.getPaymentStatus());
      dto.setAuthorizerReplyMessage(c.getAuthorizerReplyMessage());
      dto.setReturnOperation(c.getReturnOperation());
      dto.setMerchantNumber(c.getMerchantNumber());
      dto.setHierarchyId(c.getHierarchyId());
      dto.setHierarchyName(c.getHierarchyName());
      dto.setCardNumber(c.getCardNumber());
      dto.setCardBrand(c.getCardBrand());
      if(!c.getTransmitter().equals("null")) {
        dto.setTransmitter(c.getTransmitter());
      }else{
        dto.setTransmitter("NA");
      }

      dto.setRefSpNumber(c.getRefSpNumber());
      validAddListMerchantsAndFolio(conciliations, dto, c.getMerchantNumber(), c.getFolioTxn());
      String dateStr = (dto.getTransactionDate() != null) ? dto.getTransactionDate(): c.getTransactionDate() + SPACE + c.getTransactionHour();
      dto.setTransactionDate(dateStr);
      String cardTypeId = dto.getCardTypeId() != null ? dto.getCardTypeId() : c.getCardTypeId();
      //dto.setReferenceNumber(c.getRefSpNumber())
      dto.setCardTypeId(cardTypeId);
      lst.add(dto);

    }
    return lst;
  }
  
  public List<ClientAffiliationResponseDto> mergeJoinListClientTransactions( List<ClientAffiliationResponseDto> clients,
    List<TransactionsConciliationDto> conciliations) {

    List<ClientAffiliationResponseDto> lst = new ArrayList<>();

    if (!clients.isEmpty() && conciliations.isEmpty()) {

      for (ClientAffiliationResponseDto c : clients) {

        ClientAffiliationResponseDto dto = new ClientAffiliationResponseDto();
        dto.setFolioTxn(c.getFolioTxn());
        dto.setOperationTypeId(c.getOperationTypeId());
        dto.setOperationType(c.getOperationType());
        dto.setAmountStr(c.getAmountStr());
        dto.setProductId(c.getProductId());
        dto.setProductDescription(c.getProductDescription());
        dto.setPaymentStatus(c.getPaymentStatus());
        dto.setAuthorizerReplyMessage(c.getAuthorizerReplyMessage());
        dto.setReturnOperation(c.getReturnOperation());
        dto.setMerchantNumber(c.getMerchantNumber());
        dto.setHierarchyId(c.getHierarchyId());
        dto.setHierarchyName(c.getHierarchyName());
        dto.setCardNumber(c.getCardNumber());
        if(!c.getTransmitter().equals("null")) {
          dto.setTransmitter(c.getTransmitter());
        }else{
          dto.setTransmitter("NA");
        }

        dto.setRefSpNumber(c.getRefSpNumber());
        validAddListMerchantsAndFolio(conciliations, dto, c.getMerchantNumber(), c.getFolioTxn());
        String dateStr = (dto.getTransactionDate() != null) ? dto.getTransactionDate(): c.getTransactionDate() + SPACE + c.getTransactionHour();
        dto.setTransactionDate(dateStr);
        lst.add(dto);

      }

    } else if (!clients.isEmpty() && !conciliations.isEmpty()) {

      for (ClientAffiliationResponseDto c : clients) {
        ClientAffiliationResponseDto dto = new ClientAffiliationResponseDto();
        if (Boolean.TRUE.equals(validListMerchantsAndFolio(conciliations, c.getMerchantNumber(), c.getFolioTxn()))) {
          dto.setFolioTxn(c.getFolioTxn());
          dto.setOperationTypeId(c.getOperationTypeId());
          dto.setOperationType(c.getOperationType());
          dto.setAmountStr(c.getAmountStr());
          dto.setProductId(c.getProductId());
          dto.setProductDescription(c.getProductDescription());
          dto.setPaymentStatus(c.getPaymentStatus());
          dto.setAuthorizerReplyMessage(c.getAuthorizerReplyMessage());
          dto.setReturnOperation(c.getReturnOperation());
          dto.setMerchantNumber(c.getMerchantNumber());
          dto.setHierarchyId(c.getHierarchyId());
          dto.setHierarchyName(c.getHierarchyName());
          dto.setCardNumber(c.getCardNumber());
          if(!c.getTransmitter().equals("null")) {
            dto.setTransmitter(c.getTransmitter());
          }else{
            dto.setTransmitter("NA");
          }

          dto.setRefSpNumber(c.getRefSpNumber());
          validAddListMerchantsAndFolio(conciliations, dto, c.getMerchantNumber(), c.getFolioTxn());
          String dateStr = (dto.getTransactionDate() != null) ? dto.getTransactionDate(): c.getTransactionDate() + SPACE + c.getTransactionHour();
          dto.setTransactionDate(dateStr);
          lst.add(dto);

        } else {

          dto.setFolioTxn(c.getFolioTxn());
          dto.setOperationTypeId(c.getOperationTypeId());
          dto.setOperationType(c.getOperationType());
          dto.setAmountStr(c.getAmountStr());
          dto.setProductId(c.getProductId());
          dto.setProductDescription(c.getProductDescription());
          dto.setPaymentStatus(c.getPaymentStatus());
          dto.setAuthorizerReplyMessage(c.getAuthorizerReplyMessage());
          dto.setReturnOperation(c.getReturnOperation());
          dto.setMerchantNumber(c.getMerchantNumber());
          dto.setHierarchyId(c.getHierarchyId());
          dto.setHierarchyName(c.getHierarchyName());
          dto.setCardNumber(c.getCardNumber());
          if(!c.getTransmitter().equals("null")) {
            dto.setTransmitter(c.getTransmitter());
          }else{
            dto.setTransmitter("NA");
          }

          dto.setRefSpNumber(c.getRefSpNumber());
          validAddListMerchantsAndFolio(conciliations, dto, c.getMerchantNumber(), c.getFolioTxn());
          String dateStr = (dto.getTransactionDate() != null) ? dto.getTransactionDate(): c.getTransactionDate() + SPACE + c.getTransactionHour();
          dto.setTransactionDate(dateStr);
          lst.add(dto);
        }
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

  public void validAddListMerchantsAndFolio( List<TransactionsConciliationDto> lstTC, ClientAffiliationResponseDto dto, String merchants,
    String folio) {

    for (TransactionsConciliationDto tc : lstTC) {

      if (merchants.equalsIgnoreCase(tc.getMerchantNumber())
        && folio.equalsIgnoreCase(tc.getFolioTxn())) {
        dto.setCardTypeId(tc.getDetail().getCardType());
        dto.setReferenceNumber(tc.getReferenceNumber());
        dto.setRefSgNumber(tc.getDetail().getRefSgNumber());
        dto.setTransactionDate(tc.getTransactionDate());
        dto.setApprovalCode(tc.getDetail().getApprovalCode());
        dto.setAmountTip(tc.getDetail().getAmountTip());
        dto.setPosEntryMode(tc.getDetail().getPosEntryMode());
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

  public void fillPutMapValueExpression(List<String> mns, String nameValue, Map<String, Object> mapValues) {
    for (int i = 0; i < mns.size(); i++) {
      mapValues.putAll(new ValueMap().with(nameValue + UNDERSCORE + i, mns.get(i)));
    }
  }

  /** Metodo para obtener detalles de transacciones por solapas **/
  public GenericResponse getTransactionsDetailOperation(String idOperation, TransactionsDetailOperationDto dto, InfoTokenDto infoTokenDto) {
    GenericResponse response = new GenericResponse();
    List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
    try {
      TransactionsDetailsServPtalDto results = resultsListDetailDto(dto);
      if (results != null) {
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
  
  public TransactionsDetailsServPtalDto resultsListDetailDto(TransactionsDetailOperationDto dto) throws SmartServicePlatformMyException {
    try {
      TransactionsDetailsServPtalDto results;
      TransactionsDetailsServPtalDto sqlDetail = transactionsDetailRepository.getTransactionsDetailServPtalWithSP(dto);
      //TransactionsDetailsServPtalDto sqlDetail = transactionsDetailRepository.getTransactionsDetailServPtalRepository(dto)
      TransactionsConciliationDto dynamoDetail = getTransactionsConciliationByTransactionsDetail( dto ); /**Aca va el metodo de extraccion de datos de  dynamo**/
      //dynamoDetail = getTransactionsConciliationByTransactionsDetail( dto )
      if (sqlDetail != null && dynamoDetail != null) {
        results = mergeJoinListTransactionsDetail(sqlDetail, dynamoDetail);
        results.setDetail(Boolean.TRUE);
      }else if (sqlDetail != null && Boolean.FALSE.equals(dataDynamoIsEmpty(dynamoDetail))) {
        results = sqlDetail;
        results.setDetail(Boolean.TRUE);
      }else {
        results = null;
      }
      return results;
    } catch (Exception e) {
      StringBuilder errorStack = new StringBuilder();
      for (StackTraceElement er : e.getStackTrace()) {
        errorStack.append(LOG_ERROR_TRACE);
        errorStack.append(er.getClassName() + SPACE);
        errorStack.append(er.getFileName() + SPACE);
        errorStack.append(er.getLineNumber() + SPACE);
        errorStack.append(er.getMethodName() + SPACE);
        errorStack.append("");
      }
      String errorTrace = errorStack.toString();
      log.error("Errro: {}", errorTrace);
      throw new SmartServicePlatformMyException("error get list mariaDB and dynamoDB: " + errorTrace);
    }
  }
  
  public Boolean dataDynamoIsEmpty(TransactionsConciliationDto dynamoDetail) {
    if (dynamoDetail != null) {
      return Boolean.TRUE;
    }else {
      return Boolean.FALSE;
    }
  }
  
  public TransactionsDetailsServPtalDto mergeJoinListTransactionsDetail( TransactionsDetailsServPtalDto sqlDetail,
    TransactionsConciliationDto dynamoDetail) {
    TransactionsDetailsServPtalDto tds = new TransactionsDetailsServPtalDto();
    TransactionsDetailsClientServPtalDto tdc = sqlDetail.getClientServPtalDto();
    TransactionsDetailsPromissoryNoteServPtalDto tdp = sqlDetail.getPromissoryNoteServPtalDto();
    TransactionsDetailsCardServPtalDto tdcd = sqlDetail.getCardServPtalDto();
    TransactionsDetailsInformationServPtalDto tr = sqlDetail.getInformationServPtalDto();
    //String amountReal = validateData(tr.getAmountCharged())
    /**Inicia Setear valores de dynamo al objecto principal**/
    TransactionsDetailsAmountAndCommisionsServPtalDto tdac = new TransactionsDetailsAmountAndCommisionsServPtalDto();
    Double comission = dynamoDetail.getPaymentBreakdown().getSmartCommission() != null ? dynamoDetail.getPaymentBreakdown().getSmartCommission() : 0D;
    Double totalcomission = dynamoDetail.getPaymentBreakdown().getCommissionSmart() != null ? dynamoDetail.getPaymentBreakdown().getCommissionSmart() : 0D;
    //Double comission = dynamoDetail.getPaymentBreakdown().getCommissionSmart() != null ? dynamoDetail.getPaymentBreakdown().getCommissionSmart() : 0D
    tdac.setBaseRateCommission(comission.toString());
    tdac.setTotalCommission(totalcomission.toString());
    tdp.setReferenceNumber(dynamoDetail.getReferenceNumber());
    Double iva = dynamoDetail.getPaymentBreakdown().getSmartIvaCommission() != null ? dynamoDetail.getPaymentBreakdown().getSmartIvaCommission() : 0D;
    tdac.setTax( iva.toString() );
    tdac.setTransactionAmount( dynamoDetail.getTransactionAmount().toString() );
    String comissionOnRate = transactionsDetailRepository.getSaleRate( sqlDetail.getInformationServPtalDto().getClientId(), sqlDetail.getPromissoryNoteServPtalDto().getCardTypeId());
    tdac.setCommissionOnRate( comissionOnRate );
    tr.setReference(dynamoDetail.getDetail().getRefSpNumber());
    //tr.setReference(dynamoDetail.getReferenceNumber())
    tdp.setTypeCurrency(dynamoDetail.getDetail().getCurrencyCode());
    //tdp.setTippingAmount(dynamoDetail.getDetail().getAmountTip().toString())
    //tdp.setSaleAmount(dynamoDetail.getTransactionAmount().toString())
    //Double totalAmount = dynamoDetail.getTransactionAmount() + dynamoDetail.getDetail().getAmountTip()
    //tdp.setTotalAmount(totalAmount.toString())
    tdp.setReferenceNumber(dynamoDetail.getDetail().getRefSpNumber());
    //tdp.setReferenceNumber(dynamoDetail.getReferenceNumber())
    //tr.setResponse(dynamoDetail.getDetail().getRespMessage())
    //tr.setAmountCharged(amountReal)
    //tr.setPaymentMethod(dynamoDetail.getDetail().getPosEntryMode())
    //tdc.setAmount(Double.parseDouble(amountReal))
    tdp.setPayDate(dynamoDetail.getTransactionDate());
    
    /**Termina Setear valores de dynamo al objecto principal**/
    
    tds.setClientServPtalDto(tdc);
    tds.setPromissoryNoteServPtalDto(tdp);
    tds.setCardServPtalDto(tdcd);
    tds.setInformationServPtalDto(tr);
    tds.setAmountAndCommisionsServPtalDto(tdac);
    
    return tds;
  }
  
  public String validateData(String value) {
    if (value != null) {
      Double valDouble = Double.parseDouble(value);
      BigDecimal valNumber = BigDecimal.valueOf(valDouble).divide(BigDecimal.valueOf(100));
      return valNumber.toString();
    }else {
      return "0.0";
    }
  }
  
  /*Metodo donde se mapea el objecto principal de los atributos o nodos del json*/
  private List<TransactionsConciliationDto> getListTransactionsByJson(JSONArray jsonArray) {
    List<TransactionsConciliationDto> lst = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      TransactionsConciliationDto tc = new TransactionsConciliationDto();
      JSONObject obj = jsonArray.getJSONObject(i);
      tc.setMerchantNumber(getJsonString(obj, FIELD_MERCHANT_NUMBER));
      tc.setReferenceNumber(getJsonString(obj, FIELD_REFERENCE_NUMBER));
      tc.setCreateAT(convertStrDate(PATTERN, obj.getString(FIELD_CREATE_AT)));
      tc.setCreateATStr(convertDateToStr("dd/MM/yyyy", tc.getCreateAT()));
      tc.setDetail(getDetailTransactionByJson(getJsonObject(obj, FIELD_DETAIL)));
      tc.setAuthorizationNumber(getJsonString(obj, FIELD_AUTHORIZATION_NUMBER));
      //tc.setAuthorizationNumber(getJsonInt(obj, FIELD_AUTHORIZATION_NUMBER))
      tc.setCardNumber(getJsonString(obj, "card_number"));
      tc.setMerchantName(getJsonString(obj, "merchant_name"));
      //tc.setPaymentBreakdown(obj.getJSONObject("payment_breakdown").toString())
      tc.setPaymentBreakdown(getPaymentBreakdownByJson(getJsonObject(obj, FIELD_PAYMENT_BREAKDOWN)));
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
      dt.setAmountTip(getJsonDouble(obj, "amountTip"));
      dt.setCurrencyCode(getJsonString(obj, "currencyCode"));
      dt.setRespMessage(getJsonString(obj, "respMessage"));

    }
    return dt;
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

  public TransactionsConciliationDto getTransactionsConciliationByTransactionsDetail(TransactionsDetailOperationDto dto) {
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
      return lst.get(0);
    } else {
      //lst = Collections.emptyList()
      return null;
    }
  }

}
