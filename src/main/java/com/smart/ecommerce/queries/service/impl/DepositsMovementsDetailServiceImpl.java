package com.smart.ecommerce.queries.service.impl;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.ecommerce.entity.core.CoreErrorCode;
import com.smart.ecommerce.queries.model.dto.DepositsMovementsDTO;
import com.smart.ecommerce.queries.model.dto.DepositsMovementsDetailDTO;
import com.smart.ecommerce.queries.model.dto.DepositsMovementsDetailParamDTO;
import com.smart.ecommerce.queries.model.dto.DepositsMovementsMappingSumCount;
import com.smart.ecommerce.queries.model.dto.InfoTokenDto;
import com.smart.ecommerce.queries.model.dto.PaymentBreakdownDto;
import com.smart.ecommerce.queries.model.dto.SettledTransactionDTO;
import com.smart.ecommerce.queries.model.dto.TransactionsConciliationDto;
import com.smart.ecommerce.queries.repository.ErrorCodeRepository;
import com.smart.ecommerce.queries.service.DepositsMovementsDetailService;
import com.smart.ecommerce.queries.util.ErrorCode;
import com.smart.ecommerce.queries.util.GenericResponse;

import lombok.extern.slf4j.Slf4j;

import static com.smart.ecommerce.queries.util.Constants.*;
import static com.smart.ecommerce.queries.util.GetValuesJsonUtils.*;

@Slf4j
@Service
public class DepositsMovementsDetailServiceImpl implements DepositsMovementsDetailService {
  
  @Autowired private DynamoDB dynamoDB;
  @Resource private ErrorCodeRepository codeDao;
  
  private static final String TABLE_SETTLED_TRANSACTIONS = "settled_transactions";
  private static final String TABLE_TRANSACTIONS_CONCILIATION = "transactions_conciliation";
  private static final String INDEX_TABLE_REFERENCE_NUMBER = "reference_number-index";
  
  private static final String PARAM_SETTLEMENT_ID = "#p_settlement_id";
  private static final String VALUE_SETTLEMENT_ID= ":v_settlement_id";
  private static final String FIELD_SETTLEMENT_ID = "settlement_id";
  private static final String PARAM_FOLIO = "#p_folio";
  private static final String VALUE_FOLIO= ":v_folio";
  private static final String FIELD_FOLIO = "folio";
  private static final String FIELD_MERCHANT_NUMBER = "merchant_number";
  private static final String PARAM_REFERENCE_NUMBER = "#p_reference_number";
  private static final String VALUE_REFERENCE_NUMBER = ":v_reference_number";
  private static final String FIELD_REFERENCE_NUMBER = "reference_number";
  private static final String FIELD_CREATED_AT = "created_at";
  private static final String FIELD_CARD_TYPE = "card_type";
  private static final String FIELD_ACQUIRER_COMMISSION = "acquirer_commission";
  private static final String FIELD_AMOUNT_TO_SETTLED = "amount_to_settled";
  private static final String FIELD_TRANSACTION_CONCEPT = "transaction_concept";
  private static final String FIELD_VERIFIED_TRANSACTION = "verified_transaction";
  private static final String FIELD_SMART_COMMISSION = "smart_commission";
  private static final String FIELD_IVA = "iva";
  private static final String FIELD_DISPERSED = "dispersed";
  private static final String FIELD_TRANSACTION_AMOUNT = "transaction_amount";
  private static final String FIELD_CARD_NUMBER = "card_number";
  private static final String FIELD_AUTHORIZATION_NUMBER = "authorization_number";
  private static final String FIELD_TRANSACTION_FEE = "transaction_fee";
  private static final String FIELD_TRANSACTION_TYPE = "transaction_type";
  
  private static final String FIELD_PAYMENT_BREAKDOWN = "payment_breakdown";
  private static final String FIELD_TRANSACTION_DATE = "transaction_date";
  private static final String FIELD_AMOUNT_DEPOSITED_SMART = "amount_deposited_smart";
  private static final String FIELD_AMOUNT_DEPOSITED = "amount_deposited";
  private static final String FIELD_AMOUNT_TREASURY = "amount_treasury";
  private static final String FIELD_AMOUNT_RATE_SMART = "amount_rate_smart";
  private static final String FIELD_AMOUNT_IVA_SMART = "amount_iva_smart";
  private static final String FIELD_TOTAL_AMOUNT_SMART = "total_amount_smart";
  private static final String FIELD_CLIENT_ID = "client_id";
  
  private static final String VALUE_CIFRAS = "00000000000";
  private static final String PATTERN_DATE = "yyyy-MM-dd";
  private static final String PATTERN_LARGE = "yyyy-MM-dd HH:mm:ss"; //200818
  
  private static final String KEY_RESULTS = "results";
  
  
  public GenericResponse getDepositsMovementsDto(String idOperation, DepositsMovementsDetailParamDTO dto, InfoTokenDto infoTokenDto) {
    GenericResponse response = new GenericResponse();
    List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
    try {

      DepositsMovementsDTO results = getDepositsMovements(dto);

      if (!results.getDetails().isEmpty()) {
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
  
  public DepositsMovementsDTO getDepositsMovements(DepositsMovementsDetailParamDTO dto) {
    log.info(":: DepositsMovementsDetailServiceImpl - GetDepositsMovements ::");
    DepositsMovementsDTO dm;
    List<SettledTransactionDTO> settledTransactionLst = getSettledTransaction(dto);
    List<String> reference = getReferenceByTransactions(settledTransactionLst);
    List<TransactionsConciliationDto> transactionConciliationsLst = getTransactionsConciliation(reference);
    Map<String,  DepositsMovementsMappingSumCount> mapResults = getMapDepositsMovements(transactionConciliationsLst);
    dm = fillDepositsMovements(settledTransactionLst, mapResults);
    return dm;
  }
  
  public DepositsMovementsDTO fillDepositsMovements(List<SettledTransactionDTO> lst, Map<String, DepositsMovementsMappingSumCount> map) {
    DepositsMovementsDTO dm = new DepositsMovementsDTO();
    
    if (!map.isEmpty()) {
      for (Entry<String, DepositsMovementsMappingSumCount> entry : map.entrySet()) {
        DepositsMovementsMappingSumCount sum = entry.getValue();
        dm.setDepositAmount(sum.getDepositAmount());
        dm.setCommissionsCharged(sum.getCommissionsCharged());
        dm.setIvacommissions(sum.getIvacommissions());
      }
    }
    
    List<DepositsMovementsDetailDTO> depositsMovementsDetailLst =  getDepositsMovementsDetail(lst);
    dm.setDetails(depositsMovementsDetailLst);
    return dm;
  }
  
  public List<DepositsMovementsDetailDTO> getDepositsMovementsDetail(List<SettledTransactionDTO> lst){
    List<DepositsMovementsDetailDTO> results = new ArrayList<>();
    if (!lst.isEmpty()) {
      for (SettledTransactionDTO st : lst) {
        DepositsMovementsDetailDTO dmd = new DepositsMovementsDetailDTO();
        dmd.setHours(st.getCreatedAtStr());
        dmd.setOperation(st.getOperation());
        dmd.setReferenceNumber(st.getReferenceNumber());
        dmd.setMerchantNumber(st.getMerchantNumber());
        dmd.setAmount(BigDecimal.valueOf(st.getAmountToSettled()));
        dmd.setCardType(st.getCardTypeName());
        dmd.setCommission(BigDecimal.valueOf(st.getAcquirerCommission()));
        dmd.setIvaCommission(BigDecimal.valueOf(st.getIva()));
        results.add(dmd);
      }
    }
    return results;
  }
  
  public Map<String,  DepositsMovementsMappingSumCount> getMapDepositsMovements(List<TransactionsConciliationDto> lst) {

      log.info(":: TransactionsDetailServiceImpl - GetMapDepositsMovements ::");
      Map<String,  DepositsMovementsMappingSumCount> resultMap = new HashMap<>();
      if (!lst.isEmpty()) {
          log.info("size lst - MapDepositsMovements: {}", lst.size());
          resultMap = lst.stream()
            .collect(Collectors.groupingBy(tc -> tc.getClientId(),
                    Collector.of(DepositsMovementsMappingSumCount::new, DepositsMovementsMappingSumCount::add, DepositsMovementsMappingSumCount::merge)
            ));

          log.info("Map Example MapDepositsMovements:  {}", resultMap);

      }
      return resultMap;
  }
  
  @Override
  public List<SettledTransactionDTO> getSettledTransaction(DepositsMovementsDetailParamDTO dto) {
    log.info(":: DepositsMovementsDetailServiceImpl - GetSettledTransaction ::");
    Table table = dynamoDB.getTable(TABLE_SETTLED_TRANSACTIONS);
    QuerySpec querySpec;
    List<SettledTransactionDTO> results = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    
    String settlementId = validSettlementId(dto.getClientId(), FIELD_SETTLEMENT_ID);
    
    Map<String, String> mapNames = new HashMap<>();
    mapNames.put(PARAM_SETTLEMENT_ID, FIELD_SETTLEMENT_ID);
    mapNames.put(PARAM_FOLIO, FIELD_FOLIO);
    
    Map<String, Object> mapValues = new HashMap<>();
    mapValues.put(VALUE_SETTLEMENT_ID, settlementId);
    mapValues.put(VALUE_FOLIO, dto.getPaymentCode());
    
    StringBuilder keyConditionQuery = new StringBuilder();
    keyConditionQuery.append(PARAM_SETTLEMENT_ID);
    keyConditionQuery.append(OPR_EQUAL);
    keyConditionQuery.append(VALUE_SETTLEMENT_ID);
    
    log.info(QUERY_KEY_CONDITION_LOG, keyConditionQuery);

    StringBuilder query = new StringBuilder();
    query.append(PARAM_FOLIO);
    query.append(OPR_EQUAL);
    query.append(VALUE_FOLIO);
    
    log.info(QUERY_LOG, query);
    
    querySpec = new QuerySpec()
      .withKeyConditionExpression(keyConditionQuery.toString())
      .withFilterExpression(query.toString())
      .withNameMap(mapNames)
      .withValueMap(mapValues);
    
    ItemCollection<QueryOutcome> rows = table.query(querySpec);
    Iterator<Item> iterator = rows.iterator();
    
    while (iterator.hasNext()) {
      try {
        JsonNode jsonNode = mapper.readTree(iterator.next().toJSON());
        SettledTransactionDTO st = setSettledTransactionByJsonNode(jsonNode);
        results.add(st);
      } catch (Exception e) {
        log.error(ERROR_QUERY_DYNAMO_LOG, e.getMessage());
      }
    }
    
    return results;
  }
  
  public List<TransactionsConciliationDto> getTransactionsConciliation(List<String> reference) {
    log.info(":: DepositsMovementsDetailServiceImpl - GetTransactionsConciliation ::");
    Table table = dynamoDB.getTable(TABLE_TRANSACTIONS_CONCILIATION);
    Index index = table.getIndex(INDEX_TABLE_REFERENCE_NUMBER);
    QuerySpec querySpec;
    
    List<TransactionsConciliationDto> results = new ArrayList<>();
    
    ObjectMapper mapper = new ObjectMapper();
    
    Map<String, String> mapNames = new HashMap<>();
    mapNames.put(PARAM_REFERENCE_NUMBER, FIELD_REFERENCE_NUMBER);
    
    Map<String, Object> mapValues = new HashMap<>();
    
    StringBuilder keyConditionQuery = new StringBuilder();
    keyConditionQuery.append(PARAM_REFERENCE_NUMBER);
    keyConditionQuery.append(OPR_EQUAL);
    keyConditionQuery.append(VALUE_REFERENCE_NUMBER);
    
    log.info(QUERY_KEY_CONDITION_LOG, keyConditionQuery);
    
    for (String r : reference) {
      mapValues.put(VALUE_REFERENCE_NUMBER, r);
      
      querySpec = new QuerySpec()
        .withKeyConditionExpression(keyConditionQuery.toString())
        //.withFilterExpression(query.toString())
        .withNameMap(mapNames)
        .withValueMap(mapValues);

      ItemCollection<QueryOutcome> items = index.query(querySpec);
      Iterator<Item> record = items.iterator();
      
      while (record.hasNext()) {
        try {
          JsonNode jsonNode = mapper.readTree(record.next().toJSON());
          TransactionsConciliationDto tc = setTransactionsConciliationByJsonNode(jsonNode);
          results.add(tc);
        } catch (Exception e) {
          log.error(ERROR_QUERY_DYNAMO_LOG, e.getMessage());
        }
      }
      
    }
    
    return results;
  }
  
  public List<String> getReferenceByTransactions(List<SettledTransactionDTO> lst){
    List<String> results = new ArrayList<>();
    Map<Object, List<SettledTransactionDTO>> mapFolios =
      getUniqueValuesMap(lst, SettledTransactionDTO::getReferenceNumber);

    if (!lst.isEmpty()) {
      for (Entry<Object, List<SettledTransactionDTO>> entry : mapFolios.entrySet()) {
        results.add(entry.getKey().toString());
      }
    }

    return results;
  }
  
  public Map<Object, List<SettledTransactionDTO>> getUniqueValuesMap
  (List<SettledTransactionDTO> results, Function<? super SettledTransactionDTO, ?> f){
    Map<Object, List<SettledTransactionDTO>> strValue;
    if (f != null) {
      strValue =   results.stream().collect(Collectors.groupingBy(f));
    }else {
      strValue =  Collections.emptyMap();
    }
    return strValue;
  }
  
  public String validSettlementId(String value, String name) {
    String data = VALUE_CIFRAS;
    if (value != null) {
      String regex = "[+-]?[0-9]+(\\.[0-9]+)?([Ee][+-]?[0-9]+)?";
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(value);
      if(m.find() && m.group().equals(value)) {
        data = setCifrasNumber(Integer.parseInt(value), data);
        //data = value
      }else {
        data = value;
      }

    } else {
      log.warn("El dato viene nulo: {}", name);
    }
    return data;
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
  
  public Date getDateToString(String dateStr, String pattern) {
    if (StringUtils.isNotEmpty(dateStr)) {
      try {
        return new SimpleDateFormat(pattern).parse(dateStr);
      } catch (ParseException e) {
        e.printStackTrace();
        return new Date();
      }
    }else {
      return new Date();
    }
  }
  
  public String getStringToDate(Date date, String pattern) {
    if (date != null) {
      return new SimpleDateFormat(pattern).format(date);
    }else {
      return new SimpleDateFormat(pattern).format(new Date());
    }
  }
  
  public TransactionsConciliationDto setTransactionsConciliationByJsonNode(JsonNode jsonNode) {
    TransactionsConciliationDto tc = new TransactionsConciliationDto();
    if (jsonNode != null) {
      JsonNode childNodePB = jsonNode.get(FIELD_PAYMENT_BREAKDOWN);
      
      PaymentBreakdownDto pb = new PaymentBreakdownDto();
      tc.setMerchantNumber(getJsonNodeString(jsonNode, FIELD_MERCHANT_NUMBER));
      tc.setReferenceNumber(getJsonNodeString(jsonNode, FIELD_REFERENCE_NUMBER));
      tc.setCreateATStr(getJsonNodeString(jsonNode, FIELD_CREATED_AT));
      tc.setTransactionAmount(getJsonNodeDouble(jsonNode, FIELD_TRANSACTION_AMOUNT));
      String transactionDateStr = getJsonNodeString(jsonNode, FIELD_TRANSACTION_DATE);
      Date transactionDatDt = getDateToString(transactionDateStr, PATTERN_LARGE); 
      tc.setTransactionDate(getStringToDate(transactionDatDt, PATTERN_DATE));
      pb.setAmountDepositedSmart(getJsonNodeDouble(childNodePB, FIELD_AMOUNT_DEPOSITED_SMART));
      pb.setSmartCommission(getJsonNodeDouble(childNodePB, FIELD_AMOUNT_RATE_SMART));
      pb.setSmartIvaCommission(getJsonNodeDouble(childNodePB, FIELD_AMOUNT_IVA_SMART));
      pb.setTotalAmountSmart(getJsonNodeDouble(childNodePB, FIELD_TOTAL_AMOUNT_SMART));
      tc.setPaymentBreakdown(pb);
      tc.setAmountDeposited(getJsonNodeInt(jsonNode, FIELD_AMOUNT_DEPOSITED));
      tc.setAmountTreasury(getJsonNodeDouble(jsonNode, FIELD_AMOUNT_TREASURY));
      tc.setClientId(getJsonNodeString(jsonNode, FIELD_CLIENT_ID));
    }
    return tc;
  }
  
  public SettledTransactionDTO setSettledTransactionByJsonNode(JsonNode jsonNode) {
    SettledTransactionDTO st = new SettledTransactionDTO();
    if (jsonNode != null) {
      st.setSettlementId(getJsonNodeString(jsonNode, FIELD_SETTLEMENT_ID));
      st.setMerchantNumber(getJsonNodeString(jsonNode, FIELD_MERCHANT_NUMBER));
      st.setReferenceNumber(getJsonNodeString(jsonNode, FIELD_REFERENCE_NUMBER));
      st.setCreatedAtStr(getJsonNodeString(jsonNode, FIELD_CREATED_AT));
      st.setCardType(getJsonNodeInt(jsonNode, FIELD_CARD_TYPE));
      st.setAcquirerCommission(getJsonNodeLong(jsonNode, FIELD_ACQUIRER_COMMISSION));
      st.setAmountToSettled(getJsonNodeDouble(jsonNode, FIELD_AMOUNT_TO_SETTLED));
      st.setFolio(getJsonNodeString(jsonNode, FIELD_FOLIO));
      st.setTransactionConcept(getJsonNodeInt(jsonNode, FIELD_TRANSACTION_CONCEPT));
      st.setVerifiedTransaction(getJsonNodeBoolean(jsonNode, FIELD_VERIFIED_TRANSACTION));
      st.setSmartCommission(getJsonNodeDouble(jsonNode, FIELD_SMART_COMMISSION));
      st.setIva(getJsonNodeDouble(jsonNode, FIELD_IVA));
      st.setDispersed(getJsonNodeDouble(jsonNode, FIELD_DISPERSED));
      st.setTransactionAmount(getJsonNodeDouble(jsonNode, FIELD_TRANSACTION_AMOUNT));
      st.setCardNumber(getJsonNodeString(jsonNode, FIELD_CARD_NUMBER));
      st.setAuthorizationNumber(getJsonNodeString(jsonNode, FIELD_AUTHORIZATION_NUMBER));
      st.setTransactionFee(getJsonNodeDouble(jsonNode, FIELD_TRANSACTION_FEE));
      st.setTransactionType(getJsonNodeLong(jsonNode, FIELD_TRANSACTION_TYPE));
    }
    return st;
  }

}
