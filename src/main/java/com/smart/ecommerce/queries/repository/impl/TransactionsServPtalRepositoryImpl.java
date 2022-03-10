package com.smart.ecommerce.queries.repository.impl;

import static com.smart.ecommerce.queries.util.Constants.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository;

import com.smart.ecommerce.queries.model.dto.ClientAffiliationResponseDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailOperationDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsCardServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsClientServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsInformationServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsPromissoryNoteServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsDetailsServPtalDto;
import com.smart.ecommerce.queries.model.dto.TransactionsServPtalDto;
import com.smart.ecommerce.queries.repository.TransactionsServPtalRepository;
import com.smart.ecommerce.queries.util.RepositoryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository("TransactionsServPtalRepository")
public class TransactionsServPtalRepositoryImpl implements TransactionsServPtalRepository {
  
  @PersistenceContext EntityManager em;
  @Autowired private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  /** dao util. */
  @Autowired protected RepositoryUtil daoUtil;
  @Autowired private JdbcTemplate jdbcTemplate;

  private static final String SQL_SELECT = "SELECT";
  private static final String SQL_FROM = "FROM";
  private static final String SQL_AND_BETWEEN = "' AND '";
  private static final String SQL_CASE = "     CASE";
  private static final String SQL_END = "     END";
  private static final String PATTERN_DATE = "yyyy-MM-dd";
  
  private static final String FIELD_AMOUNT = "amount";
  private static final String FIELD_CLIENT_NAME = "clientName";
  private static final String FIELD_AUTO_CODE = "autCode";
  private static final String FIELD_CARD_NUMBER = "cardNumber";
  private static final String FIELD_CARD_BRAND = "cardBrand";
  private static final String FIELD_CARD_TYPE = "cardType";
  private static final String FIELD_PRODUCT_DESCRIPTION = "productDescription";

  @Override
  public List<ClientAffiliationResponseDto> getTransactionsServPtalRepository(TransactionsServPtalDto transactionsDto) {
    List<ClientAffiliationResponseDto> list = new ArrayList<>();
    //String pattern = "yyyy-MM-dd"

    /* Toda la consulta detalle de transacción Grid principal datos */

    StringBuilder sql = new StringBuilder();


    sql.append(SQL_SELECT);
    sql.append("     DISTINCT ");
    sql.append("     cct.folio_tx                                                                                                                                       AS folioTxn, ");
    sql.append("     DATE_FORMAT(cct.created_at, '%Y-%m-%d')                                                                                                            AS transactionDate, ");
    sql.append("     DATE_FORMAT(cct.created_at, '%r')                                                                                                                  AS transactionHour, ");
    sql.append("     cct.type_transaction_id                                                                                                                            AS operationTypeId, ");
    sql.append("     IFNULL(UPPER(toc.name),'INDEFINIDO')                                                                                                               AS operationType, ");
    //sql.append("     FORMAT(IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.amount')) , 0),2)                            AS amountStr, ")
    sql.append("     FORMAT(IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.amount'))/ 100 , 0),2)                       AS amountStr, "); /** Ojo revisar que no truene esta linea **/
    sql.append("     lc.type_smartlink                                                                                                                                  AS productId, ");
    sql.append("     m.membership                                                                                                                                       AS merchantNumber, ");
    sql.append("     gj.group_id                                                                                                                                        AS hierarchyId, ");
    sql.append("     gj.name                                                                                                                                            AS hierarchyName, ");
    sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardEmisor')),'NA')                                    AS transmitter, ");
    sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardNumber')),'NA')                                    AS cardNumber, ");
    sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardBrand')),'NA')                                     AS cardBrand, ");
    sql.append("     IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(info_tx,'$.smartData'),'$.cardType')),json_unquote(json_extract(json_extract(info_tx,'$.smartData'),'$.cardType')),0) ,0)  AS cardTypeId, ");
    sql.append("     IF( \n" +
            "\tIF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardType')),'NA') = '1', 'DEBITO',  \n" +
            "    IF( IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardType')),'NA') = '2', 'CREDITO', 'NA') )                                   AS cardType, ");
    sql.append("     CASE WHEN lc.type_smartlink = 1 THEN 'CHECKOUT' WHEN lc.type_smartlink = 2 THEN 'LIGA DE PAGO' ELSE 'TARJETA PRESENTE' END                         AS productDescription, ");
    sql.append("     CASE WHEN (type_transaction_id = 1) THEN 'NO PAGADO' WHEN (type_transaction_id = 2) THEN 'PAGADO' WHEN (type_transaction_id IN (3,4,5)) THEN 'CANCELACION' ELSE  UPPER(toc.name) END  AS paymentStatus, ");
    //sql.append("     CASE WHEN (type_transaction_id = 1) THEN 'NO PAGADO' WHEN (type_transaction_id = 2) THEN 'PAGADO' ELSE  UPPER(toc.name) END                        AS paymentStatus, ")
    //sql.append("     IF(cct.type_transaction_id = 2,'PAGADO','NO PAGADO')                                                                                               AS paymentStatus, ")
    sql.append("     UPPER(IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(info_tx,'$.smartData'),'$.responseMessage')),json_unquote(json_extract(json_extract(info_tx,'$.smartData'),'$.responseMessage')),'N/A') ,'N/A')) AS authorizerReplyMessage, ");
    //sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.responseMessage')),'NA')                               AS authorizerReplyMessage, ")
    sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.autCode')) ,'NA')                                      AS returnOperation, ");
    sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.refSPNum')),'NA')                                    AS refSpNumber ");
    sql.append(SQL_FROM + SPACE);
    sql.append("     core_client_transaction cct   ");
    sql.append("     LEFT OUTER JOIN link_config lc ON cct.folio_tx = lc.folio_txn");
    sql.append("     LEFT OUTER JOIN type_operation_catalog toc ON cct.type_transaction_id = toc.type_operation_id");
    sql.append("     INNER JOIN `group` g ON cct.id_client = g.client_id AND REPLACE(JSON_EXTRACT(cct.info_tx,'$.groupId'),'\"','') = g.group_id" + SPACE);
    //sql.append("     INNER JOIN `group` gj ON cct.id_client = gj.client_id AND g.parent_group_id = gj.group_id")
    sql.append("     INNER JOIN `group` gj ON gj.client_id = cct.id_client  AND gj.group_id = CASE WHEN lc.type_smartlink = 1 THEN g.group_id WHEN lc.type_smartlink = 2 THEN g.group_id ELSE g.parent_group_id END" + SPACE);
    
    /**Inicia el Join de la validacion de que jerarquia del cliente realiza la transaccion**/
    /*
    sql.append(
      appendSqlNumber(transactionsDto.getHierarchyLevelId(), "INNER JOIN (SELECT gpj.* FROM `group` gpj WHERE gpj.group_level_id = ", "INNER JOIN (SELECT gpj.* FROM `group` gpj WHERE gpj.group_level_id = 5")
      )
    sql.append(SPACE + ") gj ON gj.client_id =g.client_id" + SPACE)
    sql.append(
      appendValidationPointSale(transactionsDto.getHierarchyLevelId(), 
        "AND gj.group_id = (SELECT DISTINCT gpjp.parent_group_id FROM `group` gpjp WHERE gpjp.client_id = cct.id_client  AND gpjp.parent_group_id = gj.group_id)", 
        "")       
      )
    */
    /**Termina el Join de la validacion de que jerarquia del cliente realiza la transaccion**/
    
    sql.append("     INNER JOIN  membership m ON" + SPACE);
    sql.append(SQL_CASE + SPACE);
    sql.append("     WHEN (IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.productTypeRealId')),'NA') = 'TP')  THEN g.parent_group_id" + SPACE);
    sql.append("     WHEN (IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.productTypeRealId')),'NA') = 'CHKO_LP')  THEN g.group_id" + SPACE);
    sql.append("     ELSE g.parent_group_id" + SPACE);
    sql.append(SQL_END + SPACE);
    sql.append("     =  m.group_id" + SPACE);
    //sql.append("     INNER JOIN membership m ON g.parent_group_id = m.group_id")
    sql.append("     LEFT OUTER JOIN VIEW_CLIENT_BY_ACQUIRER vca ON cct.id_client = vca.client_id_css   ");
    sql.append("WHERE 1 = 1 ");


    sql.append(
      appendSqlDateAndHours(
        transactionsDto.getInitDate(), transactionsDto.getEndDate(),
        "AND DATE(cct.created_at)  BETWEEN '", SQL_AND_BETWEEN, "'"
        )
      );
    
    sql.append(
      appendSqlDateAndHours(
        transactionsDto.getTransactionStartDate(), transactionsDto.getTransactionEndDate(),
        "AND DATE(cct.created_at)  BETWEEN '", SQL_AND_BETWEEN, "'"
        )
      );

    sql.append(
      appendSqlDateAndHours(transactionsDto.getInitHour(), transactionsDto.getEndHour(),
        "AND date_format(cct.created_at, '%T')  BETWEEN '", SQL_AND_BETWEEN, "'")
      );

    sql.append(
      appendSqlNumber(transactionsDto.getProductId(), "AND lc.type_smartlink =", "")
      );

    sql.append(
      appendSqlNumber(transactionsDto.getOperationTypeId(), "AND cct.type_transaction_id =", "AND   cct.type_transaction_id  IN (2,3,4,5)")
      //appendSqlNumber(transactionsDto.getOperationTypeId(), "AND cct.type_transaction_id =", "AND   cct.type_transaction_id  IN (SELECT toc.type_operation_id FROM type_operation_catalog toc )")
      );


    sql.append(
      appendSqlString(convertDoubleString(transactionsDto.getSaleAmount()), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(info_tx, '$.amount'), null) =", "")
      );


    sql.append(
      appendSqlString(validUpperValue(transactionsDto.getTransmitter()), "AND IF(JSON_VALID(info_tx),UPPER(json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardEmisor'))) ,'NA') =", "")
      );

    /** revisar por esta validacion no iria, si no desde java con el reference number **/
    /*
    sql.append(
      appendSqlString(transactionsDto.getPaymentReference(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.refSPNum'), null) =", "")
      )
    */

    sql.append(
      appendSqlString(validUpperValue(transactionsDto.getCardBrand()), "AND IF(JSON_VALID(info_tx),UPPER(json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardBrand'))) ,'NA') =", "")
      );

    sql.append(
      appendSqlString(transactionsDto.getApproval(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.autCode'), null) =", "")
      );

    sql.append(
      appendSqlString(transactionsDto.getCardType(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'), null) =", "")
      );
    
    sql.append(
      appendSqlString(transactionsDto.getResponseCode(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.respCode'), null) =", "")
      );

    sql.append(
      appendSqlString(transactionsDto.getAuthorizerStatus(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.responseMessage'), null) =", "")
      );

    sql.append(
            appendSqlNumber(transactionsDto.getPosEntryModeId(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.EntryMode'), null) =", "")
    );
    
    sql.append(
      appendSqlStringLikeEnd(transactionsDto.getCardNumberEnd(), "AND IF(JSON_VALID(info_tx), REPLACE(JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardNumber'),'\"','') , null) LIKE ", "")
      );
    
    sql.append(
      appendSqlString(transactionsDto.getCodeMsi(), "AND cct.cve_msi = ", "")
      );
    
    sql.append(
      appendSqlString(transactionsDto.getMerchant(), "AND m.membership = ", "")
      );
    
    

    log.info(SQL_LOG, sql);

    try {

      /*
        list = em.createNativeQuery(sql.toString()).unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.aliasToBean(ClientAffiliationResponseDto.class))
                .getResultList()
       */
        
        list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<ClientAffiliationResponseDto>(ClientAffiliationResponseDto.class));

        return list;
    } catch (Exception e) {

        e.printStackTrace();
        return Collections.emptyList();
    }
  }
  
  public List<ClientAffiliationResponseDto> getTransactionsServPtalWithSP(TransactionsServPtalDto dto) {
    log.info(":: TransactionsServPtalRepositoryImpl - getTransactionsServPtalWithSP ::");
    List<ClientAffiliationResponseDto> results;
    String todayStr = new SimpleDateFormat(PATTERN_DATE).format(new Date());
    if (dto.getInitDate().equalsIgnoreCase(todayStr) && dto.getEndDate().equalsIgnoreCase(todayStr)) {
      log.info(":: Consultando tabla de hecho hoy ::");
      results = getTransactionByExecuteView(dto, "VIEW_GET_TRANSACTION");
    }else {
      log.info(":: Consultando Historico ::");
      results = getTransactionByExecuteView(dto, "VIEW_GET_TRANSACTION_HISTORICAL");
    }
    return results;
  }
  
  public List<ClientAffiliationResponseDto> getTransactionByExecuteView(TransactionsServPtalDto dto, String nameView){
    List<ClientAffiliationResponseDto> lst = new ArrayList<>();

    /* Toda la consulta detalle de transacción Grid principal datos */

    StringBuilder sql = new StringBuilder();

    sql.append(SQL_SELECT + SPACE);
    sql.append("*" + SPACE);
    sql.append(SQL_FROM + SPACE);
    sql.append(nameView + SPACE);
    sql.append(addFilterStatement(dto));

    log.info(SQL_LOG, sql);

    try {

      lst = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<ClientAffiliationResponseDto>(ClientAffiliationResponseDto.class));

      return lst;
    } catch (Exception e) {

      e.printStackTrace();
      return Collections.emptyList();
    }
  }
  
  public String addFilterStatement( TransactionsServPtalDto dto) {
    StringBuilder sql = new StringBuilder();
    sql.append("WHERE 1 = 1" + SPACE);
    
    sql.append(
      appendSqlDateAndHours(
        dto.getInitDate(), dto.getEndDate(),
        "AND transactionDate  BETWEEN '", SQL_AND_BETWEEN, "'"
        )
      );
    
    sql.append(
      appendSqlDateAndHours(
        dto.getTransactionStartDate(), dto.getTransactionEndDate(),
        "AND transactionDate  BETWEEN '", SQL_AND_BETWEEN, "'"
        )
      );

    sql.append(
      appendSqlDateAndHours(dto.getInitHour(), dto.getEndHour(),
        "AND transactionHour  BETWEEN '", SQL_AND_BETWEEN, "'")
      );

    sql.append(
      appendSqlNumber(dto.getProductId(), "AND productId =", "")
      );
    
    sql.append(
      appendSqlString(convertDoubleString(dto.getSaleAmount()), "AND amount =", "")
      );


    sql.append(
      appendSqlString(validUpperValue(dto.getTransmitter()), "AND UPPER(transmitter) =", "")
      );


    sql.append(
      appendSqlString(validUpperValue(dto.getCardBrand()), "AND UPPER(cardBrand) =", "")
      );

    sql.append(
      appendSqlString(dto.getApproval(), "AND returnOperation =", "")
      );

    sql.append(
      appendSqlString(dto.getCardType(), "AND cardTypeId =", "")
      );
    
    sql.append(
      appendSqlString(dto.getResponseCode(), "AND respCode =", "")
      );

    sql.append(
      appendSqlString(dto.getAuthorizerStatus(), "AND UPPER(authorizerReplyMessage) =", "")
      );

    sql.append(
            appendSqlNumber(dto.getPosEntryModeId(), "AND UPPER(entryMode) =", "")
    );
    
    sql.append(
      appendSqlStringLikeEnd(dto.getCardNumberEnd(), "AND cardNumber LIKE ", "")
      );
    
    sql.append(
      appendSqlString(dto.getCodeMsi(), "AND cveMsi =", "")
      );
    
    sql.append(
      appendSqlString(dto.getMerchant(), "AND merchantNumber =", "")
      );
    
    return sql.toString();
  }
  
  public TransactionsDetailsServPtalDto getTransactionsDetailServPtalRepository(TransactionsDetailOperationDto dto) {
    log.info(":: TransactionsProcessedRepositoryImpl - HasAcquirer ::");
    SqlParameterSource param = new BeanPropertySqlParameterSource(dto);
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT " + SPACE);
    sql.append("  transactionDate" + SPACE);
    sql.append("  ,transactionHour" + SPACE);
    sql.append("  ,hierarchyId" + SPACE);
    sql.append("  ,hierarchyName" + SPACE);
    sql.append("  ,folioTxn" + SPACE);
    sql.append("  ,linkUser" + SPACE);
    sql.append("  ,linkEstatus" + SPACE);
    sql.append("  ,linkEstatusDescription" + SPACE);
    sql.append("  ,linkConcept" + SPACE);
    sql.append("  ,cancelConcept" + SPACE);
    sql.append("  ,operationTypeId" + SPACE);
    sql.append("  ,operationType" + SPACE);
    sql.append("  ,amount" + SPACE);
    sql.append("  ,monthsWithoutInterest" + SPACE);
    sql.append("  ,paymentStatus" + SPACE);
    sql.append("  ,authorizerReplyMessage" + SPACE);
    sql.append("  ,cardNumber" + SPACE);
    sql.append("  ,autCode" + SPACE);
    sql.append("  ,userName" + SPACE);
    sql.append("  ,lastName" + SPACE);
    sql.append("  ,tradeReference" + SPACE);
    sql.append("  ,cardBrand" + SPACE);
    sql.append("  ,cveMsi" + SPACE);
    sql.append("  ,cardType" + SPACE);
    sql.append("  ,subTotal" + SPACE);
    sql.append("  ,haveTip" + SPACE);
    sql.append("  ,amountTip" + SPACE);
    sql.append("  ,clientName" + SPACE);
    sql.append("  ,address" + SPACE);
    sql.append("  ,email " + SPACE);
    sql.append("  ,phone " + SPACE);
    sql.append("  ,acquirer " + SPACE);
    sql.append("  ,merchantNumber " + SPACE);
    sql.append("  ,productDescription " + SPACE);
    sql.append("  ,clientId" + SPACE);
    sql.append("  ,cardTypeId" + SPACE);
    sql.append("  ,saleType" + SPACE);
    sql.append("  ,txnType" + SPACE);
    sql.append("  ,entryMode" + SPACE);
    sql.append("  ,eci" + SPACE);
    sql.append("  ,cardEmisor" + SPACE);
    sql.append("FROM" + SPACE);
    sql.append("  (" + SPACE);
    sql.append("  SELECT" + SPACE);
    sql.append("  *" + SPACE);
    sql.append("  FROM " + SPACE);
    sql.append("  (" + SPACE);
    sql.append("  SELECT " + SPACE);
    sql.append("    DISTINCT " + SPACE);
    sql.append("       DATE_FORMAT(cct.created_at, '%Y-%m-%d')                                                                                                  AS transactionDate" + SPACE);
    sql.append("      ,date_format(cct.created_at, '%r')                                                                                                        AS transactionHour" + SPACE);
    sql.append("      ,gpj.group_id                                                                                                                             AS hierarchyId" + SPACE);
    sql.append("      ,gpj.name                                                                                                                                 AS hierarchyName" + SPACE);
    sql.append("      ,cct.folio_tx                                                                                                                             AS folioTxn" + SPACE);
    sql.append("      ,cct.user_by_register                                                                                                                     AS linkUser" + SPACE);
    sql.append("      ,0                                                                                                                                        AS linkEstatus" + SPACE);
    sql.append("      ,'N/A'                                                                                                                                    AS linkEstatusDescription" + SPACE);
    sql.append("      ,IF(ISNULL(json_unquote(json_extract(cct.info_tx, '$.reference'))) = '', (json_unquote(json_extract(cct.info_tx, '$.reference'))), 'NA')  AS linkConcept" + SPACE);
    sql.append("      ,'N/A'                                                                                                                                    AS cancelConcept" + SPACE);
    sql.append("      ,cct.type_transaction_id                                                                                                                  AS operationTypeId" + SPACE);
    sql.append("      ,IFNULL(UPPER(toc.name),'INDEFINIDO')                                                                                                     AS operationType" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.amount')) , 0)                            AS amount" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.amount')) , 0)                            AS monthsWithoutInterest" + SPACE);
    sql.append("      ,IF(cct.type_transaction_id = 2, 'PAGADO', 'NO PAGADO')                                                                                   AS paymentStatus" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.responseMessage')), 'NA')                 AS authorizerReplyMessage" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.cardNumber')), 'NA')                      AS cardNumber" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.autCode')), 'NA')                         AS autCode" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.name')), 'NA')                            AS userName" + SPACE);
    //sql.append("      ,user.name                                                                                                                                AS userName")
    sql.append("      ,CONCAT(IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.lastname')), 'NA'), ' '" + SPACE);
    sql.append("                ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.lastname2')), 'NA'))            AS lastName" + SPACE);
    //sql.append("      ,user.last_name1                                                                                                                          AS lastName" + SPACE)
    sql.append("      ,'N/A'                                                                                                                                    AS tradeReference" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.cardBrand')), 'NA')                       AS cardBrand" + SPACE);
    sql.append("      ,IF(ISNULL(cct.cve_msi) = 0, cct.cve_msi, 'NA')                                                                                           AS cveMsi" + SPACE);
    sql.append("      ,CASE WHEN IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'), null)  = '1' THEN 'Débito' " + SPACE);
    sql.append("               WHEN " + SPACE);
    sql.append("                       IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'), null) = '2' " + SPACE);
    sql.append("                   THEN 'Crédito' " + SPACE);
    sql.append("               ELSE 'NA' END                                                                                                                    AS cardType" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.subTotal')),'NA')                          AS subTotal" + SPACE);
    sql.append("      ,cct.have_tip                                                                                                                             AS haveTip" + SPACE);
    sql.append("      ,FORMAT(cct.tip_amount,2)                                                                                                                 AS amountTip " + SPACE);
    sql.append("      ,c.name                                                                                                                                   AS clientName" + SPACE);
    sql.append("      ,CONCAT(IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.address')),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.address')),'') ,'') , ' ' " + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.city')) ,json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.city')),'') ,'') , ' '" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.state')),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.state')),'') ,'') , ' '" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.zipCode')) ,json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.zipCode')),''),''))                       AS address" + SPACE);
    //sql.append("      ,CONCAT(ca.calle , ' ', ca.cve_asentamiento, ' ', ca.cve_municipio)                                                                       AS address" + SPACE)
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.email')), 'NA')                           AS email" + SPACE);
    //sql.append("      ,cc.email                                                                                                                                 AS email   " + SPACE)
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.phone')), 'NA')                           AS phone" + SPACE);
    //sql.append("      ,cc.phone                                                                                                                                 AS phone " + SPACE)
    sql.append("      ,m.acquirer                                                                                                                               AS acquirer" + SPACE);
    sql.append("      ,m.membership                                                                                                                             AS merchantNumber" + SPACE);
    sql.append("      ,'TARJETA PRESENTE'                                                                                                                       AS productDescription" + SPACE);
    sql.append("      ,c.client_id                                                                                                                              AS clientId" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), REPLACE(JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'),'\"',''), null)                         AS cardTypeId" + SPACE);
    sql.append("      ,cstc.name                                                                                                                                AS saleType" + SPACE); /*Tipo de venta**/
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.TxnType')), 'NA')                         AS txnType" + SPACE); /**Tipo de transaccion**/
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.EntryMode')), 'NA')                       AS entryMode" + SPACE); /**Tipo de Entrada**/
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.eci')),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.eci')),'N/A') ,'N/A') AS eci" + SPACE); /**ECI**/
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(info_tx,'$.smartData'),'$.cardEmisor')),json_unquote(json_extract(json_extract(info_tx,'$.smartData'),'$.cardEmisor')),'N/A') ,'N/A') AS cardEmisor" + SPACE); /**Emisor**/
    sql.append("  FROM core_client_transaction cct" + SPACE);
    sql.append("    INNER JOIN  " + SPACE);
    sql.append("        (  " + SPACE);
    sql.append("          SELECT  " + SPACE);
    sql.append("            cct2.folio_tx as maxFolio,  " + SPACE);
    sql.append("            MAX(cct2.created_at) as maxDate  " + SPACE);
    sql.append("          FROM core_client_transaction cct2  " + SPACE);
    sql.append("          WHERE cct2.folio_tx = :folioTxn  " + SPACE);
    sql.append("          GROUP BY cct2.folio_tx  " + SPACE);
    sql.append("        ) maxTxn ON maxTxn.maxFolio = cct.folio_tx AND maxTxn.maxDate = cct.created_at " + SPACE);
    sql.append("      LEFT OUTER JOIN type_operation_catalog toc ON cct.type_transaction_id = toc.type_operation_id " + SPACE);
    sql.append("      LEFT OUTER JOIN user user ON cct.user_by_register = user.user_id  " + SPACE);
    sql.append("      INNER JOIN `group` gp  ON cct.id_client = gp.client_id AND gp.group_id = REPLACE(JSON_EXTRACT(cct.info_tx,'$.groupId'),'\"','')" + SPACE);
    sql.append("      INNER JOIN `group` gpj ON cct.id_client = gpj.client_id AND gp.parent_group_id = gpj.group_id " + SPACE);
    sql.append("      INNER JOIN client c ON c.client_id = gp.client_id " + SPACE);
    sql.append("     INNER JOIN membership m ON" + SPACE);
    sql.append(SQL_CASE + SPACE);
    sql.append("     WHEN (IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.productTypeRealId')),'NA') = 'TP')  THEN gp.parent_group_id" + SPACE);
    sql.append("     WHEN (IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.productTypeRealId')),'NA') = 'CHKO_LP')  THEN gp.group_id" + SPACE);
    sql.append("     ELSE gp.parent_group_id" + SPACE);
    sql.append(SQL_END + SPACE);
    sql.append("     = m.group_id" + SPACE);
    //sql.append("      INNER JOIN membership m ON gp.parent_group_id = m.group_id" + SPACE)
    sql.append("      LEFT OUTER JOIN VIEW_CLIENT_BY_ACQUIRER vca ON cct.id_client = vca.client_id_css" + SPACE);
    sql.append("      LEFT OUTER JOIN client_address ca ON c.client_id = ca.client_id " + SPACE);
    sql.append("      LEFT OUTER JOIN client_contact cc ON c.client_id = cc.client_id" + SPACE);
    sql.append("      LEFT OUTER JOIN core_sale_type_catalog cstc ON cct.sale_type_id = cstc.idcore_sale_type_catalog" + SPACE);
    sql.append("  WHERE cct.folio_tx NOT IN (SELECT lc.folio_txn FROM link_config lc) AND cct.folio_tx = :folioTxn  " + SPACE);
    sql.append("  ORDER BY 1 DESC " + SPACE);
    sql.append("  ) TABLE_CCT" + SPACE);
    sql.append("  UNION" + SPACE);
    sql.append("  SELECT" + SPACE);
    sql.append("  * FROM" + SPACE);
    sql.append("  (" + SPACE);
    sql.append("  SELECT " + SPACE);
    sql.append("    DISTINCT " + SPACE);
    sql.append("      DATE_FORMAT(lclc.created_at, '%Y-%m-%d')                                                                                                      AS linkDate" + SPACE);
    sql.append("      ,DATE_FORMAT(lclc.created_at, '%r')                                                                                                           AS linkHour" + SPACE);
    sql.append("      ,gpj.group_id                                                                                                                                 AS hierarchyId" + SPACE);
    sql.append("      ,gpj.name                                                                                                                                     AS hierarchyName" + SPACE);
    sql.append("      ,lclc.folio_txn                                                                                                                               AS folioTxn" + SPACE);
    sql.append("      ,lclc.user_by_register                                                                                                                        AS linkUser" + SPACE);
    sql.append("      ,lclc.status_id                                                                                                                               AS linkEstatus" + SPACE);
    sql.append("      ,slcclc.name                                                                                                                                  AS linkEstatusDescription" + SPACE);
    sql.append("      ,IF(ISNULL(json_unquote(json_extract(cctlc.info_tx, '$.reference'))) = '', (json_unquote(json_extract(cctlc.info_tx, '$.reference'))), 'NA')  AS linkConcept" + SPACE);
    sql.append("      ,IF (ISNULL(lcclc.description) = 0, lcclc.description, 'NA')                                                                                  AS cancelConcept" + SPACE);
    sql.append("      ,cctlc.type_transaction_id                                                                                                                    AS operationTypeId" + SPACE);
    sql.append("      ,IFNULL(UPPER(toc.name),'INDEFINIDO')                                                                                                         AS operationType" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.amount')) , 0)                              AS amount" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.amount')) , 0)                              AS monthsWithoutInterest" + SPACE);
    //sql.append("      ,lclc.amount                                                                                                                                  AS amount" + SPACE)
    //sql.append("      ,lclc.amount                                                                                                                                  AS monthsWithoutInterest" + SPACE)
    sql.append("      ,IF(cctlc.type_transaction_id = 2, 'PAGADO', 'NO PAGADO')                                                                                     AS paymentStatus" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.responseMessage')), 'NA')                   AS authorizerReplyMessage" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.cardNumber')), 'NA')                        AS cardNumber" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.autCode')), 'NA')                           AS autCode" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.name')), 'NA')                              AS userName" + SPACE);
    //sql.append("      ,user.name                                                                                                                                  AS userName")
    sql.append("      ,CONCAT(IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.lastname')), 'NA'), ' '" + SPACE);
    sql.append("                ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.lastname2')), 'NA'))              AS lastName" + SPACE);
    //sql.append("      ,user.last_name1                                                                                                                            AS lastName" + SPACE)
    sql.append("      ,lclc.trade_reference                                                                                                                         AS tradeReference" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.cardBrand')), 'NA')                         AS cardBrand" + SPACE);
    sql.append("      ,IF(ISNULL(cctlc.cve_msi) = 0, cctlc.cve_msi, 'NA')                                                                                           AS cveMsi" + SPACE);
    sql.append("      ,CASE WHEN IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'), null)  = '1' THEN 'Débito' " + SPACE);
    sql.append("               WHEN " + SPACE);
    sql.append("                       IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'), null) = '2' " + SPACE);
    sql.append("                   THEN 'Crédito' " + SPACE);
    sql.append("               ELSE 'NA' END                                                                                                                        AS cardType" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.subTotal')),'NA')                            AS subTotal" + SPACE);
    sql.append("      ,cctlc.have_tip                                                                                                                               AS haveTip" + SPACE);
    sql.append("      ,FORMAT(cctlc.tip_amount,2)                                                                                                                   AS amountTip " + SPACE);
    sql.append("      ,c.name                                                                                                                                       AS clientName" + SPACE);
    sql.append("      ,CONCAT(IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(info_tx,'$.smartData'),'$.address')),json_unquote(json_extract(json_extract(info_tx,'$.smartData'),'$.address')),'') ,'') , ' ' " + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(info_tx,'$.smartData'),'$.city')) ,json_unquote(json_extract(json_extract(info_tx,'$.smartData'),'$.city')),'') ,'') , ' '" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(info_tx,'$.smartData'),'$.state')),json_unquote(json_extract(json_extract(info_tx,'$.smartData'),'$.state')),'') ,'') , ' '" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(info_tx,'$.smartData'),'$.zipCode')) ,json_unquote(json_extract(json_extract(info_tx,'$.smartData'),'$.zipCode')),''),''))                       AS address" + SPACE);
    //sql.append("      ,CONCAT(ca.calle , ' ', ca.cve_asentamiento, ' ', ca.cve_municipio)                                                                         AS address" + SPACE)
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.email')), 'NA')                             AS email" + SPACE);
    //sql.append("      ,cc.email                                                                                                                                   AS email   " + SPACE)
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.phone')), 'NA')                             AS phone" + SPACE);
    //sql.append("      ,cc.phone                                                                                                                                   AS phone " + SPACE)
    sql.append("      ,m.acquirer                                                                                                                                   AS acquirer" + SPACE);
    sql.append("      ,m.membership                                                                                                                                 AS merchantNumber" + SPACE);
    sql.append("      ,CASE WHEN lclc.type_smartlink = 1 THEN 'CHECKOUT' WHEN lclc.type_smartlink = 2 THEN 'LIGA DE PAGO' ELSE 'TARJETA PRESENTE' END               AS productDescription" + SPACE);
    sql.append("      ,c.client_id                                                                                                                                  AS clientId" + SPACE);
    sql.append("      ,IF(JSON_VALID(info_tx), REPLACE(JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'),'\"',''), null)                             AS cardTypeId" + SPACE);
    sql.append("      ,cstc.name                                                                                                                                    AS saleType" + SPACE); /*Tipo de venta**/
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.TxnType')), 'NA')                           AS txnType" + SPACE); /**Tipo de transaccion**/
    sql.append("      ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.EntryMode')), 'NA')                         AS entryMode" + SPACE); /**Tipo de Entrada**/
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(info_tx,'$.smartData'),'$.eci')),json_unquote(json_extract(json_extract(info_tx,'$.smartData'),'$.eci')),'N/A') ,'N/A') AS eci" + SPACE); /**ECI**/
    sql.append("      ,IF(JSON_VALID(info_tx) ,IF( JSON_VALID(json_extract(json_extract(info_tx,'$.smartData'),'$.cardEmisor')),json_unquote(json_extract(json_extract(info_tx,'$.smartData'),'$.cardEmisor')),'N/A') ,'N/A') AS cardEmisor" + SPACE); /**Emisor**/
    sql.append("  FROM link_config lclc  " + SPACE);
    sql.append("      INNER JOIN status_link_config_catalog slcclc ON slcclc.status_link_config_id = lclc.status_id  " + SPACE);
    sql.append("      INNER JOIN  " + SPACE);
    sql.append("        (  " + SPACE);
    sql.append("          SELECT  " + SPACE);
    sql.append("            cct2lc.folio_tx as maxFolio,  " + SPACE);
    sql.append("            MAX(cct2lc.created_at) as maxDate  " + SPACE);
    sql.append("          FROM core_client_transaction cct2lc  " + SPACE);
    sql.append("          WHERE cct2lc.folio_tx = :folioTxn  " + SPACE);
    sql.append("          GROUP BY cct2lc.folio_tx  " + SPACE);
    sql.append("        ) maxTxnLc ON maxTxnLc.maxFolio = lclc.folio_txn " + SPACE);
    sql.append("      INNER JOIN core_client_transaction cctlc ON maxTxnLc.maxFolio = cctlc.folio_tx AND maxTxnLc.maxDate = cctlc.created_at " + SPACE);
    sql.append("      LEFT OUTER JOIN type_operation_catalog toc ON cctlc.type_transaction_id = toc.type_operation_id " + SPACE);
    sql.append("      LEFT OUTER JOIN link_config_cancel lcclc ON lclc.link_config_id = lcclc.link_config_id  " + SPACE);
    sql.append("      INNER JOIN user userlc ON lclc.user_by_register = userlc.user_id  " + SPACE);
    sql.append("      INNER JOIN `group` gp  ON cctlc.id_client = gp.client_id AND gp.group_id = REPLACE(JSON_EXTRACT(cctlc.info_tx,'$.groupId'),'\"','')" + SPACE);
    sql.append("      INNER JOIN `group` gpj ON cctlc.id_client = gpj.client_id AND gp.parent_group_id = gpj.group_id " + SPACE);
    sql.append("      INNER JOIN client c ON c.client_id = gp.client_id " + SPACE);
    sql.append("     INNER JOIN membership m ON" + SPACE);
    sql.append(SQL_CASE + SPACE);
    sql.append("     WHEN (IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.productTypeRealId')),'NA') = 'TP')  THEN gp.parent_group_id" + SPACE);
    sql.append("     WHEN (IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cctlc.info_tx, '$.smartData'), '$.productTypeRealId')),'NA') = 'CHKO_LP')  THEN gp.group_id" + SPACE);
    sql.append("     ELSE gp.parent_group_id" + SPACE);
    sql.append(SQL_END + SPACE);
    sql.append("     = m.group_id" + SPACE);
    //sql.append("      INNER JOIN membership m ON gp.parent_group_id = m.group_id" + SPACE)
    sql.append("      LEFT OUTER JOIN VIEW_CLIENT_BY_ACQUIRER vca ON cctlc.id_client = vca.client_id_css" + SPACE);
    sql.append("      LEFT OUTER JOIN client_address ca ON c.client_id = ca.client_id " + SPACE);
    sql.append("      LEFT OUTER JOIN client_contact cc ON c.client_id = cc.client_id" + SPACE);
    sql.append("      LEFT OUTER JOIN core_sale_type_catalog cstc ON cctlc.sale_type_id = cstc.idcore_sale_type_catalog" + SPACE);
    sql.append("  WHERE cctlc.folio_tx = :folioTxn  " + SPACE);
    sql.append("  ORDER BY 1 DESC" + SPACE);
    sql.append("  ) TABLE_LC" + SPACE);
    sql.append(") TABLE_UNION" + SPACE);
    
    log.info(SQL_LOG, sql);
    
    try {
      return namedParameterJdbcTemplate.queryForObject(sql.toString(), param, new TransactionsDetailsServPtalMapper());
    } catch (Exception e) {
      log.error("Error GetTransactionsDetailServPtalRepository : {}", e);
      return null;
    }
  }
  
  public String appendSqlDateAndHours(String start, String end, String sqlO, String sqlT, String sqlTh) {
    StringBuilder sql = new StringBuilder();
    if (null != start && null != end && StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {
      sql.append(SPACE + sqlO);
      sql.append(start);
      sql.append(sqlT);
      sql.append(end);
      sql.append(sqlTh + SPACE);
    } else {
      sql.append("");
    }
    return sql.toString();
  }

  public String appendSqlNumber(Integer o, String sqlO, String sqlElse) {
    StringBuilder sql = new StringBuilder();
    if (null != o && !o.equals(0)) {
      sql.append(SPACE + sqlO);
      sql.append(SPACE + o);
      sql.append(SPACE);
    } else {
      sql.append(SPACE + sqlElse + SPACE);
    }
    return sql.toString();
  }
  
  public String appendValidationPointSale(Integer o, String sqlO, String sqlElse) {
    StringBuilder sql = new StringBuilder();
    if (validateEmptyOrNullInt(o).equals(6)) {
      sql.append(SPACE + sqlElse + SPACE);
    } else {      
      sql.append(SPACE + sqlO + SPACE);
    }
    return sql.toString();
  }
  
  public Integer validateEmptyOrNullInt(Integer o) {
    Integer v = 0;
    if (null != o) {
      v= o;
    }else {
      v= 0;
    }
    return v;
  }

  public String appendSqlDouble(Double o, String sqlO, String sqlElse) {
    StringBuilder sql = new StringBuilder();
    if (null != o && !o.equals(0.0)) {
      sql.append(SPACE + sqlO);
      sql.append(SPACE + o);
      sql.append(SPACE);
    } else {
      sql.append(SPACE + sqlElse + SPACE);
    }
    return sql.toString();
  }
  
  public String appendSqlDoubleAmount(Double o, String sqlO, String sqlElse) {
    StringBuilder sql = new StringBuilder();
    if (null != o ) {
      sql.append(SPACE + sqlO);
      sql.append(SPACE + o);
      sql.append(SPACE);
    } else {
      sql.append(SPACE + sqlElse + SPACE);
    }
    return sql.toString();
  }

  public String appendSqlString(String o, String sqlO, String sqlElse) {
    StringBuilder sql = new StringBuilder();
    if (null != o && StringUtils.isNotEmpty(o)) {
      sql.append(SPACE + sqlO);
      sql.append(SPACE + "'" + o + "'");
      sql.append(SPACE);
    } else {
      sql.append(SPACE + sqlElse + SPACE);
    }
    return sql.toString();
  }
  
  public String validUpperValue(String o) {
    String value = "";
    if (StringUtils.isNotBlank(o)) {
      value = o.toUpperCase();
    }
    return value;
  }
  
  public String convertDoubleString(Double o) {
    if (null != o) {
      return String.valueOf(o);
    }else {
      return EMPTY;
    }
  }
  
  public String appendSqlStringLikeEnd(String o, String sqlO, String sqlElse) {
    StringBuilder sql = new StringBuilder();
    if (null != o && StringUtils.isNotEmpty(o)) {
      sql.append(SPACE + sqlO);
      sql.append(SPACE + "'%" + o + "'");
      sql.append(SPACE);
    } else {
      sql.append(SPACE + sqlElse + SPACE);
    }
    return sql.toString();
  }
  
  private class TransactionsDetailsServPtalMapper implements RowMapper<TransactionsDetailsServPtalDto>{

    @Override
    public TransactionsDetailsServPtalDto mapRow(ResultSet rs, int rowNum) throws SQLException {
      TransactionsDetailsServPtalDto td = new TransactionsDetailsServPtalDto();
      TransactionsDetailsClientServPtalDto tdc = new TransactionsDetailsClientServPtalDto();
      TransactionsDetailsPromissoryNoteServPtalDto tdp = new TransactionsDetailsPromissoryNoteServPtalDto();
      TransactionsDetailsCardServPtalDto tdcd = new TransactionsDetailsCardServPtalDto();
      TransactionsDetailsInformationServPtalDto tr = new TransactionsDetailsInformationServPtalDto();
      String amountReal = validateData((String) daoUtil.rsFieldDB(rs, FIELD_AMOUNT));
      tdc.setAddress((String) daoUtil.rsFieldDB(rs, "address"));
      tdc.setClientName((String) daoUtil.rsFieldDB(rs, FIELD_CLIENT_NAME));
      tdc.setEmail((String) daoUtil.rsFieldDB(rs, "email"));
      tdc.setPhone((String) daoUtil.rsFieldDB(rs, "phone"));
      tdc.setAmount(Double.parseDouble(amountReal));
      td.setClientServPtalDto(tdc);
      tdp.setFolio((String) daoUtil.rsFieldDB(rs, "folioTxn"));
      tdp.setMerchantNumber((String) daoUtil.rsFieldDB(rs, "merchantNumber"));
      tdp.setAuthorizationNumber((String) daoUtil.rsFieldDB(rs, FIELD_AUTO_CODE));
      tdp.setCardNumber((String) daoUtil.rsFieldDB(rs, FIELD_CARD_NUMBER));
      tdp.setCardIssuer((String) daoUtil.rsFieldDB(rs, FIELD_CARD_BRAND));
      tdp.setAcquirer((String) daoUtil.rsFieldDB(rs, "cardEmisor"));
      //tdp.setAcquirer((String) daoUtil.rsFieldDB(rs, "acquirer"))
      tdp.setCardType((String) daoUtil.rsFieldDB(rs, FIELD_CARD_TYPE));
      tdp.setCardTypeId((String) daoUtil.rsFieldDB(rs, "cardTypeId"));
      tdp.setProduct((String) daoUtil.rsFieldDB(rs, FIELD_PRODUCT_DESCRIPTION));
      tdp.setMerchantName((String) daoUtil.rsFieldDB(rs, FIELD_CLIENT_NAME));
      tdp.setHasTipping(getHaveTip(daoUtil.rsFieldIntDB(rs, "haveTip")));
      tdp.setTransactionType((String) daoUtil.rsFieldDB(rs, "txnType"));
      if (Boolean.TRUE.equals(tdp.getHasTipping())) {
        tdp.setSaleAmount(validateData((String) daoUtil.rsFieldDB(rs, "subTotal")));
      }else {
        tdp.setSaleAmount(amountReal);
      }
      tdp.setTotalAmount(amountReal);
      tdp.setEci((String) daoUtil.rsFieldDB(rs, "eci"));
      tdp.setTippingAmount(validateData((String) daoUtil.rsFieldDB(rs, "amountTip")));
      
      td.setPromissoryNoteServPtalDto(tdp);
      tdcd.setCardBrand((String) daoUtil.rsFieldDB(rs, FIELD_CARD_BRAND));
      tdcd.setCardType(((String) daoUtil.rsFieldDB(rs, FIELD_CARD_TYPE)).toUpperCase());
      tdcd.setFourDigitCard((String) daoUtil.rsFieldDB(rs, FIELD_CARD_NUMBER));
      tdcd.setClientBank((String) daoUtil.rsFieldDB(rs, "acquirer"));
      td.setCardServPtalDto(tdcd);
      tr.setDate((String) daoUtil.rsFieldDB(rs, "transactionDate"));
      tr.setHour((String) daoUtil.rsFieldDB(rs, "transactionHour"));
      tr.setAmountCharged(amountReal);
      //tr.setAmountCharged((String) daoUtil.rsFieldDB(rs, FIELD_AMOUNT))
      tr.setAuthorizationCode((String) daoUtil.rsFieldDB(rs, FIELD_AUTO_CODE));
      tr.setClientId((String) daoUtil.rsFieldDB(rs, "clientId"));
      tr.setTypeOperation((String) daoUtil.rsFieldDB(rs, "operationType"));
      tr.setProduct((String) daoUtil.rsFieldDB(rs, FIELD_PRODUCT_DESCRIPTION));
      tr.setTypeSale((String) daoUtil.rsFieldDB(rs, "saleType"));
      tr.setPaymentMethod((String) daoUtil.rsFieldDB(rs, "entryMode"));
      tr.setResponse((String) daoUtil.rsFieldDB(rs, "authorizerReplyMessage"));
      td.setInformationServPtalDto(tr);
      return td;
    }
    

  }

  @Override
  public String getSaleRate(String clientId, String cardType) {
    log.info("::: FileManagerExcelDaoImpl - GetSaleRate :::");
    StringBuilder sql = new StringBuilder();
    sql.append(SQL_SELECT + SPACE);
    sql.append("  CONCAT(IFNULL(FORMAT(csrr.value, 2) , '0.00'), ' %') AS saleRate" + SPACE);
    //sql.append("  CONCAT(IFNULL(FORMAT(csrr.value, 1) , '0.0'), ' %') AS saleRate" + SPACE)
    sql.append("FROM" + SPACE);
    sql.append("  core_solution cs" + SPACE);
    sql.append("  INNER JOIN core_client_solution ccs" + SPACE);
    sql.append("    ON cs.core_solution_id = ccs.core_solution_id" + SPACE);
    sql.append("  INNER JOIN core_solution_range csr" + SPACE);
    sql.append("    ON ccs.core_solution_id = csr.core_solution_id" + SPACE);
    sql.append("  INNER JOIN core_solution_range_rate csrr" + SPACE);
    sql.append("    ON csr.core_solution_range_id = csrr.core_solution_range_id" + SPACE);
    sql.append("WHERE" + SPACE);
    sql.append("  ccs.client_id = ? AND csrr.type_rate_id = ?" + SPACE);
    sql.append("LIMIT 1" + SPACE);

    Object[] param = {clientId, cardType};

    log.info(SQL_LOG, sql);
    Arrays.asList(param).forEach(p-> log.info("Params: {}", p));

    try {
      return jdbcTemplate.queryForObject(sql.toString(), param, String.class);
    } catch (Exception e) {
      log.error("Error GetSaleRate: {}", e.getMessage());
      return "0.0 %";
    }
  }

  public TransactionsDetailsServPtalDto getTransactionsDetailServPtalWithSP(TransactionsDetailOperationDto dto) {
    /*
    simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("GetDataByFolio")
    SqlParameterSource in = new MapSqlParameterSource().addValue("p_in_folio_txn", dto.getFolioTxn())
    Map<String, Object> out = simpleJdbcCall.execute(in)
    */
    TransactionsDetailsServPtalDto td;
    Map<String, Object> out = getOutDataExecuteSp(dto, "GetDataByFolio", "p_in_folio_txn");
    Map<String, Object> outHistory = getOutDataExecuteSp(dto, "GetDataHistoryByFolio", "p_in_folio_txn");
    List<TransactionsDetailsServPtalDto> results = transactionsDetailsMapper(out, 1);
    List<TransactionsDetailsServPtalDto> resultsHistory = transactionsDetailsMapper(outHistory, 1);
    if (!results.isEmpty()) {
      td = results.get(0);
    } else if(!resultsHistory.isEmpty()){
      td = resultsHistory.get(0);
    }else {
      td = null;
    }
    return td;
  }
  
  public Map<String, Object> getOutDataExecuteSp(TransactionsDetailOperationDto dto, String nameSp, String inParameter) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(nameSp);
    SqlParameterSource in = new MapSqlParameterSource().addValue(inParameter, dto.getFolioTxn());
    //Map<String, Object> out = simpleJdbcCall.execute(in)
    return simpleJdbcCall.execute(in);
  }
  
  @SuppressWarnings("unchecked")
  public List<TransactionsDetailsServPtalDto> transactionsDetailsMapper(Map<String, Object> out, Integer pos) {
  //'public' 'TransactionsDetailsServPtalDto' transactionsDetailsMapper(Map<String, Object> out, Integer pos) '{'
    String keyResultSet = "#result-set-" + pos;
    //TransactionsDetailsServPtalDto td = new TransactionsDetailsServPtalDto()
    List<TransactionsDetailsServPtalDto> results = new ArrayList<>();
    List<TransactionsDetailsServPtalDto> lst = new ArrayList<>();
    if (out != null && !out.isEmpty()) {
      if (out.containsKey(keyResultSet)) {
        List<Map<String, Object>> rs = (List<Map<String, Object>>) out.get(keyResultSet);
        rs.forEach(d->{
          TransactionsDetailsServPtalDto tds = new TransactionsDetailsServPtalDto();
          TransactionsDetailsClientServPtalDto tdc = new TransactionsDetailsClientServPtalDto();
          TransactionsDetailsPromissoryNoteServPtalDto tdp = new TransactionsDetailsPromissoryNoteServPtalDto();
          TransactionsDetailsCardServPtalDto tdcd = new TransactionsDetailsCardServPtalDto();
          TransactionsDetailsInformationServPtalDto tr = new TransactionsDetailsInformationServPtalDto();
          String amountReal = validateData((String) d.get("amountStr"));
          tdc.setAddress((String) d.get("address"));
          tdc.setClientName((String) d.get(FIELD_CLIENT_NAME));
          tdc.setEmail((String) d.get("email"));
          tdc.setPhone((String) d.get("phone"));
          tdc.setAmount(Double.parseDouble(amountReal));
          tds.setClientServPtalDto(tdc);
          tdp.setFolio((String) d.get( "folioTxn"));
          tdp.setMerchantNumber((String) d.get( "merchantNumber"));
          tdp.setAuthorizationNumber((String) d.get( FIELD_AUTO_CODE));
          tdp.setCardNumber((String) d.get( FIELD_CARD_NUMBER));
          tdp.setCardIssuer((String) d.get( FIELD_CARD_BRAND));
          tdp.setAcquirer((String) d.get( "cardEmisor"));
          //tdp.setAcquirer((String) d.get( "acquirer"))
          tdp.setCardType((String) d.get( FIELD_CARD_TYPE));
          tdp.setCardTypeId((String) d.get( "cardTypeId"));
          tdp.setProduct((String) d.get( FIELD_PRODUCT_DESCRIPTION));
          tdp.setMerchantName((String) d.get( FIELD_CLIENT_NAME));
          tdp.setHasTipping(getHaveTip((Integer) d.get( "haveTip")));
          tdp.setTransactionType((String) d.get( "txnType"));
          if (Boolean.TRUE.equals(tdp.getHasTipping())) {
            tdp.setSaleAmount(validateData((String) d.get( "subTotal")));
          }else {
            tdp.setSaleAmount(amountReal);
          }
          tdp.setTotalAmount(amountReal);
          tdp.setEci((String) d.get( "eci"));
          tdp.setTippingAmount(validateData((String) d.get( "amountTip")));
          
          tds.setPromissoryNoteServPtalDto(tdp);
          tdcd.setCardBrand((String) d.get( FIELD_CARD_BRAND));
          tdcd.setCardType(((String) d.get( FIELD_CARD_TYPE)).toUpperCase());
          tdcd.setFourDigitCard((String) d.get( FIELD_CARD_NUMBER));
          tdcd.setClientBank((String) d.get( "acquirer"));
          tds.setCardServPtalDto(tdcd);
          tr.setDate((String) d.get( "transactionDate"));
          tr.setHour((String) d.get( "transactionHour"));
          tr.setAmountCharged(amountReal);
          //tr.setAmountCharged((String) d.get( FIELD_AMOUNT))
          tr.setAuthorizationCode((String) d.get( FIELD_AUTO_CODE));
          tr.setClientId((String) d.get( "clientId"));
          tr.setTypeOperation((String) d.get( "operationType"));
          tr.setProduct((String) d.get( FIELD_PRODUCT_DESCRIPTION));
          tr.setTypeSale((String) d.get( "saleType"));
          tr.setPaymentMethod((String) d.get( "entryMode"));
          tr.setResponse((String) d.get( "authorizerReplyMessage"));
          tds.setInformationServPtalDto(tr);
          lst.add(tds);
        });
        //td = lst.get(0)
        results = lst;
      }else {
        //td = new TransactionsDetailsServPtalDto()
        results = Collections.emptyList();
      }
    }
    
    return results;
  }
  
  public Boolean getHaveTip(Integer hasTip) {
    Boolean haveTip = Boolean.FALSE;
    if (hasTip != null) {
      if (hasTip.equals(1)) {
        haveTip = Boolean.TRUE;
      }else {
        haveTip = Boolean.FALSE;
      }
    }
    return haveTip;
  }
  
  public String validateData(String value) {
    if (value != null) {
      Double valDouble = validCommaDouble(value);
      //Double valDouble = Double.parseDouble(value)
      BigDecimal valNumber = BigDecimal.valueOf(valDouble).divide(BigDecimal.valueOf(100));
      return valNumber.toString();
    }else {
      return "0.0";
    }
  }
  
  public Double validCommaDouble(String value) {
    Double val = 0D;
    if (value != null) {
      String newVal = value.replace(",", "");
      val = Double.parseDouble(newVal);
    }
    return val;
  }
  
}
