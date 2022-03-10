package com.smart.ecommerce.queries.repository.impl;

import com.smart.ecommerce.queries.model.dto.*;
import com.smart.ecommerce.queries.repository.TransactionsDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.smart.ecommerce.queries.util.Constants.SPACE;
import static com.smart.ecommerce.queries.util.Constants.SQL_LOG;
import static com.smart.ecommerce.queries.util.ConvertDates.convertDateToStr;

@Slf4j
@Repository("TransactionsDetailRepository")
public class TransactionsDetailRepositoryImpl implements TransactionsDetailRepository {

    @PersistenceContext
    EntityManager em;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SQL_SELECT = "SELECT";
    private static final String SQL_FROM = "FROM";

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<ClientAffiliationResponseDto> getTransactionsDetailRepository(TransactionsDto transactionsDto) {
        List<ClientAffiliationResponseDto> list = new ArrayList<>();
        String pattern = "yyyy-MM-dd";
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern)

        /* Toda la consulta detalle de transacción Grid principal datos */

        StringBuilder sql = new StringBuilder();


        sql.append("SELECT DISTINCT ");
        sql.append("     cct.folio_tx                                                                                                                                       AS folioTxn, ");
        sql.append("     DATE_FORMAT(cct.created_at, '%Y-%m-%d')                                                                                                            AS transactionDate, ");
        sql.append("     DATE_FORMAT(cct.created_at, '%r')                                                                                                                  AS transactionHour, ");
        sql.append("     cct.type_transaction_id                                                                                                                            AS operationTypeId, ");
        sql.append("     IFNULL(UPPER(toc.name),'INDEFINIDO')                                                                                                               AS operationType, ");
        sql.append("     FORMAT(IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.amount')) , 0),2) AS amountStr, ");
        sql.append("     0                                                                                                                                                  AS productId, ");
        //sql.append("     lc.type_smartlink                                                                                                                                AS productId, ")
        sql.append("     m.membership                                                                                                                                       AS merchantNumber, ");
        sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardEmisor')),'NA')                                    AS transmitter, ");
        sql.append("     'TARJETA PRESENTE'                                                                                                                                 AS productDescription, ");
        //sql.append("     CASE WHEN lc.type_smartlink = 1 THEN 'CHECKOUT' WHEN lc.type_smartlink = 2 THEN 'LIGA DE PAGO' ELSE 'TARJETA PRESENTE' END                       AS productDescription, ")
        sql.append("     IF(cct.type_transaction_id = 2,'PAGADO','NO PAGADO')                                                                                               AS paymentStatus, ");
        sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.responseMessage')),'NA')                               AS authorizerReplyMessage, ");
        sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.autCode')) ,'NA')                                      AS returnOperation, ");
        sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.refSPNum')),'NA')                                    AS refSpNumber ");
        sql.append(SQL_FROM + SPACE);
        sql.append("     core_client_transaction cct   ");
        //sql.append("     LEFT OUTER JOIN link_config lc ON cct.folio_tx = lc.folio_txn")
        sql.append("     LEFT OUTER JOIN type_operation_catalog toc ON cct.type_transaction_id = toc.type_operation_id");
        //sql.append("     INNER JOIN `group` g ON cct.id_client = g.client_id")
        sql.append("     INNER JOIN `group` g ON cct.id_client = g.client_id AND REPLACE(JSON_EXTRACT(cct.info_tx,'$.groupId'),'\"','') = g.group_id");
        //sql.append("     INNER JOIN membership m ON g.group_id = m.group_id")
        sql.append("     INNER JOIN membership m ON g.parent_group_id = m.group_id");
        sql.append("     LEFT OUTER JOIN VIEW_CLIENT_BY_ACQUIRER vca ON cct.id_client = vca.client_id_css   ");
        sql.append("WHERE 1 = 1 ");


        sql.append(
                appendSqlDateAndHours(
                        convertDateToStr(pattern, transactionsDto.getInitDate()), convertDateToStr(pattern, transactionsDto.getEndDate()),
                        "AND DATE(cct.created_at)  BETWEEN '", "' AND '", "'"
                )
        );

        sql.append(
                appendSqlDateAndHours(transactionsDto.getInitHour(), transactionsDto.getEndHour(),
                        "AND date_format(cct.created_at, '%T')  BETWEEN '", "' AND '", "'")
        );

        /*
        sql.append(
                appendSqlNumber(transactionsDto.getProductId(), "AND lc.type_smartlink =", "")
        )
        */

        sql.append(
                //appendSqlNumber(transactionsDto.getOperationTypeId(), "AND cct.type_transaction_id =", "AND   cct.type_transaction_id  in (2,4,5)")
                appendSqlNumber(transactionsDto.getOperationTypeId(), "AND cct.type_transaction_id =", "AND   cct.type_transaction_id  IN (SELECT toc.type_operation_id FROM type_operation_catalog toc )")
        );

        if (transactionsDto.getSaleAmount() != null) {

            sql.append(
                    appendSqlString(String.valueOf(transactionsDto.getSaleAmount()), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(info_tx, '$.amount'), null) =", "")
            );

        }

        sql.append(
                appendSqlString(transactionsDto.getTransmitter(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardEmisor'), null) =", "")
        );

        sql.append(
                appendSqlString(transactionsDto.getPaymentReference(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.refSPNum'), null) =", "")
        );

        sql.append(
                appendSqlString(transactionsDto.getCardBrand(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardBrand'), null) =", "")
        );

        sql.append(
                appendSqlString(transactionsDto.getApproval(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.autCode'), null) =", "")
        );

        sql.append(
                appendSqlString(transactionsDto.getCardType(), "AND IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'), null) =", "")
        );

        log.info(SQL_LOG, sql);

        try {

          /*  
          list = em.createNativeQuery(sql.toString()).unwrap(org.hibernate.query.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(ClientAffiliationResponseDto.class))
                    .getResultList()
            */
          list = namedParameterJdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<ClientAffiliationResponseDto>(ClientAffiliationResponseDto.class));

            return list;
        } catch (Exception e) {

            e.printStackTrace();
            return Collections.emptyList();
        }


    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public List<ClientAffiliationResponseDto> getTransactionsDetailRepositoryByResponseCode(TransactionsByCodeDto transactionsDto) {
        List<ClientAffiliationResponseDto> list = new ArrayList<>();

        /* Toda la consulta detalle de transacción Grid principal datos */
        StringBuilder sql = new StringBuilder();

        sql.append(SQL_SELECT + SPACE);
        sql.append("     cct.folio_tx                                                                                                            AS folioTxn,");
        sql.append("     DATE_FORMAT(cct.created_at, '%Y-%m-%d')                                                                                 AS transactionDate,");
        sql.append("     DATE_FORMAT(cct.created_at, '%r')                                                                                       AS transactionHour,");
        sql.append("     cct.type_transaction_id                                                                                                 AS operationTypeId,");
        sql.append("     IFNULL(UPPER(toc.name),'INDEFINIDO')                                                                                    AS operationType, ");
        sql.append("     IF(cct.info_tx is not NULL AND cct.info_tx <> '' ,REPLACE(json_extract(cct.info_tx,'$.amount'),'\"','') ,0)             AS amountStr,");
        sql.append("     m.membership                                                                                                            AS merchantNumber,");
        sql.append("     IF(cct.resp_code = 00,json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardEmisor')),NULL)          AS transmitter,");
        sql.append("     'N/A'                                                                                                                   AS productDescription,");
        sql.append("     IF(cct.type_transaction_id = 2,'PAGADO','NO PAGADO')                                                                    AS paymentStatus,");
        sql.append("     IF(cct.info_tx is not NULL AND cct.info_tx <> '' ,json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.responseMessage')) "
                + ",'NO MESSAGE')                                                                                                              AS authorizerReplyMessage,");
        sql.append("     IF(cct.resp_code = 00,json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.autCode')) ,NULL)            AS returnOperation,  ");
        sql.append("     IF(cct.info_tx is not NULL AND cct.info_tx <> '' ,json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData')"
                + ",'$.cardNumber')) ,NULL)                                                                                                    AS cardNumber  ");
        sql.append(SQL_FROM + SPACE);
        sql.append("     core_client_transaction cct     ");
        sql.append("     LEFT OUTER JOIN type_operation_catalog toc ON cct.type_transaction_id = toc.type_operation_id");
        sql.append("     INNER JOIN `group` g ON cct.id_client = g.client_id  ");
        sql.append("     INNER JOIN membership m ON g.group_id = m.group_id     ");
        sql.append("     LEFT OUTER JOIN VIEW_CLIENT_BY_ACQUIRER vca ON cct.id_client = vca.client_id_css  ");
        sql.append("WHERE cct.resp_code <> 00 ");

        sql.append(
                appendSqlDateAndHours(
                        transactionsDto.getStartDate(), transactionsDto.getEndDate(), "AND DATE(cct.created_at)  BETWEEN '", "'  AND  '", "'"
                )
        );

        sql.append(
                appendSqlDouble(transactionsDto.getSaleAmount(), "AND    IF(cct.info_tx is not NULL AND cct.info_tx <> '' ,REPLACE(json_extract(cct.info_tx,'$.amount'),'\"','') ,0) =", "")
        );

        sql.append(
                appendSqlString(transactionsDto.getCardNumber(), "AND IF(cct.info_tx is not NULL AND cct.info_tx <> '' ,json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.cardNumber')) ,'') =", "")
        );

        sql.append(
                appendSqlString(transactionsDto.getFolioTxn(), "AND    cct.folio_tx =", "")
        );

        log.info(SQL_LOG, sql);

        try {

            list = em.createNativeQuery(sql.toString()).unwrap(org.hibernate.query.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(ClientAffiliationResponseDto.class))
                    .getResultList();

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String appendSqlDateAndHours(String start, String end, String sqlO, String sqlT, String sqlTh) {
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

    private String appendSqlNumber(Integer o, String sqlO, String sqlElse) {
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

    private String appendSqlDouble(Double o, String sqlO, String sqlElse) {
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

    private String appendSqlString(String o, String sqlO, String sqlElse) {
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

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<ClientAffiliationResponseDetailDto> getTransactionsDetailOperation(TransactionsDetailOperationDto transactionsDetailOperationDto) {
        //'public' List<ClientAffiliationResponseDto> getTransactionsDetailOperation(TransactionsDetailOperationDto transactionsDetailOperationDto) '{'
        List<ClientAffiliationResponseDetailDto> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT  ");
        sql.append("   DATE_FORMAT(cct.created_at, '%Y-%m-%d')                                                                                                  AS paymentDate");
        sql.append("  ,date_format(cct.created_at, '%r')                                                                                                        AS paymentHour");
        sql.append("  ,gp.name                                                                                                                                  AS hierarchyId");
        sql.append("  ,DATE_FORMAT(cct.created_at, '%Y-%m-%d')                                                                                                   AS linkDate");
        //sql.append("  ,DATE_FORMAT(lc.created_at, '%Y-%m-%d')                                                                                                   AS linkDate")
        sql.append("  ,lc.folio_txn                                                                                                                             AS folioTxn");
        sql.append("  ,DATE_FORMAT(cct.created_at, '%r')                                                                                                         AS linkHour");
        //sql.append("  ,DATE_FORMAT(lc.created_at, '%r')                                                                                                         AS linkHour")
        sql.append("  ,lc.user_by_register                                                                                                                      AS linkUser");
        sql.append("  ,lc.status_id                                                                                                                             AS linkEstatus");
        sql.append("  ,slcc.name                                                                                                                                AS linkEstatusDescription");
        sql.append("  ,IF(ISNULL(json_unquote(json_extract(cct.info_tx, '$.reference'))) = '', (json_unquote(json_extract(cct.info_tx, '$.reference'))), 'NA')  AS linkConcept");
        sql.append("  ,IF (ISNULL(lcc.description) = 0, lcc.description, 'NA')                                                                                  AS cancelConcept");
        sql.append("  ,cct.type_transaction_id                                                                                                                  AS operationTypeId");
        sql.append("  ,lc.amount                                                                                                                                AS amount");
        sql.append("  ,lc.amount                                                                                                                                AS monthsWithoutInterest");
        sql.append("  ,IF(cct.type_transaction_id = 2, 'PAGADO', 'NO PAGADO')                                                                                   AS paymentStatus");
        sql.append("  ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.responseMessage')), 'NA')                  AS authorizerReplyMessage");
        sql.append("  ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.cardNumber')), 'NA')                       AS cardNumber");
        sql.append("  ,IF(JSON_VALID(info_tx), json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.autCode')), 'NA')                          AS autCode");
        sql.append("  ,user.name                                                                                                                                AS userName");
        sql.append("  ,user.last_name1                                                                                                                          AS lastName");
        sql.append("  ,lc.trade_reference                                                                                                                       AS tradeReference");
        sql.append("  ,IF(cct.resp_code = 00, json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.cardBrand')), 'NA')                        AS cardBrand");
        sql.append("  ,IF(ISNULL(cct.cve_msi) = 0, cct.cve_msi, 'NA')                                                                                           AS cveMsi");
        sql.append("  ,CASE WHEN IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'), null)  = '1' THEN 'Débito' ");
        sql.append("           WHEN ");
        sql.append("                   IF(JSON_VALID(info_tx), JSON_EXTRACT(JSON_EXTRACT(info_tx, '$.smartData'), '$.cardType'), null) = '2' ");
        sql.append("               THEN 'Crédito' ");
        sql.append("           ELSE 'NA' END                                                                                                                    AS cardType, ");

        sql.append("     IF(JSON_VALID(info_tx),json_unquote(json_extract(json_extract(cct.info_tx, '$.smartData'), '$.subTotal')),'NA')                                     AS subTotal, ");
        sql.append("     cct.have_tip                                                                                                                 AS haveTip, ");
        sql.append("     FORMAT(cct.tip_amount,2)                                                                                                 AS amountTip ");
        sql.append(" FROM link_config lc  ");
        sql.append("  INNER JOIN status_link_config_catalog slcc   ");
        sql.append("    ON slcc.status_link_config_id = lc.status_id  ");
        sql.append("  INNER JOIN  ");
        sql.append("    (  ");
        sql.append("      SELECT  ");
        sql.append("        cct2.folio_tx as maxFolio,  ");
        sql.append("        MAX(cct2.created_at) as maxDate  ");
        sql.append("      FROM core_client_transaction cct2  ");
        sql.append("      WHERE cct2.folio_tx = :folioTxt  ");
        sql.append("      GROUP BY cct2.folio_tx  ");
        sql.append("    )   ");
        sql.append("  maxTxn  ");
        sql.append("  INNER JOIN core_client_transaction cct  ");
        sql.append("    ON maxTxn.maxFolio = cct.folio_tx AND maxTxn.maxDate = cct.created_at  ");
        sql.append("  LEFT OUTER JOIN link_config_cancel lcc  ");
        sql.append("    ON lc.link_config_id = lcc.link_config_id  ");
        sql.append("  INNER JOIN user user  ");
        sql.append("    ON lc.user_by_register = user.user_id  ");
        sql.append("  INNER JOIN `group` gp  ");
        //sql.append("    ON lc.group_id = gp.group_id  ")
        sql.append("    ON cct.id_client = gp.client_id   ");
        sql.append("WHERE cct.folio_tx = :folioTxt  ");
        sql.append("ORDER BY cct.created_at DESC  ");
        sql.append("LIMIT 1  ");

        log.info(SQL_LOG, sql);


        try {

            /*List<ClientAffiliationResponseDto> dto = em.createNativeQuery(Query).unwrap(org.hibernate.query.Query.class) .setParameter("folioTxt", transactionsDetailOperationDto.getFolioTxn()) .setResultTransformer(Transformers.aliasToBean(ClientAffiliationResponseDetailDto.class)) .getResultList()*/
            list = em.createNativeQuery(sql.toString()).unwrap(org.hibernate.query.Query.class)
                    .setParameter("folioTxt", transactionsDetailOperationDto.getFolioTxn())
                    .setResultTransformer(Transformers.aliasToBean(ClientAffiliationResponseDetailDto.class))
                    .getResultList();

            return list;
        } catch (Exception e) {

            e.printStackTrace();
            //return null
            return list;
        }


    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<ClientAffiliationResponseDto> getMovements(MovementsDto movementsDto) {
        List<ClientAffiliationResponseDto> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(SQL_SELECT + SPACE);
        sql.append("     DATE_FORMAT(cct.created_at, '%Y-%m-%d')                                                                             AS paymentDate, ");
        sql.append("     DATE_FORMAT(cct.created_at, '%r')                                                                                   AS paymentHour, ");
        sql.append("     ( ");
        sql.append("     SELECT getpath(lc.group_id) ");
        sql.append("     )                                                                                                                   AS hierarchyId, ");
        sql.append("     DATE_FORMAT( lc.created_at, '%Y-%m-%d')                                                                             AS linkDate, ");
        sql.append("     DATE_FORMAT( lc.created_at, '%r')                                                                                   AS linkHour, ");
        sql.append("     lc.user_by_register                                                                                                 AS linkUser, ");
        sql.append("     lc.type_smartlink                                                                                                   AS linkEstatus, ");
        sql.append("     lc.folio_txn                                                                                                        AS folioTxn, ");
        sql.append("     lc.concept                                                                                                          AS linkConcept, ");
        sql.append("     IF(isnull(lcc.description)= 0,lcc.description,NULL )                                                                AS cancelConcept, ");
        sql.append("     cct.type_transaction_id                                                                                             AS operationTypeId, ");
        sql.append("     lc.amount                                                                                                           AS amount, ");
        sql.append("     lc.amount                                                                                                           AS monthsWithoutInterest, ");
        sql.append("     IF(cct.type_transaction_id = 2,'PAGADO','NO PAGADO')                                                                AS paymentStatus, ");
        sql.append("     IF(cct.resp_code = 00,json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.responseMessage')),NULL) AS authorizerReplyMessage, ");
        sql.append("     IF(cct.resp_code = 00,json_unquote(json_extract(json_extract(cct.info_tx,'$.smartData'),'$.autCode')) ,NULL)        AS returnOperation ");
        sql.append(SQL_FROM + SPACE);
        sql.append("     core_client_transaction cct ");
        sql.append("INNER JOIN link_config lc ON cct.folio_tx = lc.folio_txn ");
        sql.append("LEFT JOIN link_config_cancel lcc ON lc.link_config_id = lcc.link_config_id ");
        sql.append("WHERE ");
        sql.append("     MONTH(cct.created_at) = :monthId ");
        sql.append(
                appendSqlString(movementsDto.getGroupId(), "AND lc.group_id =", "")
        );
        sql.append("ORDER BY ");
        sql.append("     cct.created_at DESC ");
        sql.append("LIMIT 1 ");

        try {

            list = em.createNativeQuery(sql.toString()).unwrap(org.hibernate.query.Query.class)
                    .setParameter("monthId", movementsDto.getMonthId())
                    //.setParameter("hierarchyId", movementsDto.getGroupId())
                    .setResultTransformer(Transformers.aliasToBean(ClientAffiliationResponseDetailDto.class))
                    .getResultList();

            return list;
        } catch (Exception e) {

            e.printStackTrace();
            return list;
        }


    }

    @Override
    public List<ClientAffiliationResponseDto> getMovementsDetail(TransactionsDetailOperationDto transactionsDetailOperationDto) {
        return Collections.emptyList();
        //return null
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<UserDetailDto> getUserDetail(Integer userId) {
        List<UserDetailDto> dto = new ArrayList<>();
        String query =
                " select " +
                        " user2.user_id as userId, " +
                        "       user2.name as userName, " +
                        "       user2.last_name1 as userLastName " +
                        " from user user " +
                        " inner join user user2 on user.group_id = user2.group_id " +
                        " and user.client_id = user2.client_id " +
                        " where user.user_id = :userId and user2.user_id <> :userId ";

        try {

            dto = em.createNativeQuery(query).unwrap(org.hibernate.query.Query.class)
                    .setParameter("userId", userId)
                    .setResultTransformer(Transformers.aliasToBean(UserDetailDto.class))
                    .getResultList();

            return dto;
        } catch (Exception e) {

            e.printStackTrace();
            return dto;
        }

    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<ParamsTransactionsConciliationDto> getMerchantNumberByRfc(/*MovementsDto*/ MovementsParamsDto dto) {
        List<ParamsTransactionsConciliationDto> merchantNumbers = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT  ");
        sql.append("     membership AS merchantNumber ");
        sql.append("FROM tax_data td  ");
        //sql.append("     INNER JOIN membership m ON td.client_id = m.idClient  ")
        sql.append("     INNER JOIN `group` g ON td.client_id = g.client_id  ");
        sql.append("     INNER JOIN membership m ON g.group_id = m.group_id  ");
        sql.append("WHERE td.status_id = 1 AND td.RFC = :rfc ");
        sql.append("LIMIT 1 ");
        sql.append("");

        try {
            merchantNumbers = em.createNativeQuery(sql.toString()).unwrap(org.hibernate.query.Query.class)
                    .setParameter("rfc", dto.getRfc())
                    .setResultTransformer(Transformers.aliasToBean(ParamsTransactionsConciliationDto.class)).getResultList();
        } catch (Exception e) {
            log.error("Error - getMerchantNumberByRfc: {}", e.getMessage());
        }
        return merchantNumbers;
    }

    @Override
    public BigDecimal getFeeCommissions(String rfc, Integer idPeriodFee, Integer idFee) {
        log.info(":: TransactionsDetailRepositoryImpl - GetFeeCommissions ::");
        BigDecimal rateFee = BigDecimal.ZERO;
        StringBuilder sql = new StringBuilder();
        sql.append(SQL_SELECT + SPACE);
        sql.append("    IFNULL(REPLACE(FORMAT(csf.unit_price ,2), ',', '')  , 0.00) rateFee ");
        //sql.append("    IFNULL(csf.unit_price,0) rateFee ")
        sql.append("  FROM core_client_solution ccs");
        sql.append("  INNER JOIN core_solution cs ON ccs.core_solution_id = cs.core_solution_id");
        sql.append("  INNER JOIN core_solution_fee csf ON csf.core_solution_id = cs.core_solution_id");
        sql.append("  INNER JOIN core_fee cf ON csf.core_fee_id = cf.core_fee_id");
        sql.append("  INNER JOIN fee_period_catalog fpc ON csf.fee_period_id = fpc.fee_period_id");
        sql.append("  INNER JOIN client c ON ccs.client_id = c.client_id");
        sql.append("  INNER JOIN `group` g ON c.client_id = g.client_id");
        sql.append("  INNER JOIN tax_data td ON c.client_id = td.client_id ");
        sql.append("  WHERE td.RFC = ? ");
        sql.append("    AND c.status_id = 1");
        sql.append("    AND csf.status_id = 1");
        sql.append("    AND fpc.fee_period_id = ?");
        sql.append("    AND cf.core_fee_id = ?");
        sql.append("  GROUP BY  ");
        sql.append("    csf.unit_price");
        sql.append("");

        Object[] param = {rfc, idPeriodFee, idFee};

        try {
            rateFee = jdbcTemplate.queryForObject(sql.toString(), param, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            rateFee = BigDecimal.ZERO;
        }

        return rateFee;
    }
    
    public Integer getNextValSeq() {
      log.info(":: TransactionsDetailRepositoryImpl - GetFeeCommissions ::");
      Integer nextValSeq = 0;
      StringBuilder sql = new StringBuilder();
      sql.append(SQL_SELECT + SPACE);
      sql.append("getNextVal() nextVal" + SPACE);
      try {
        nextValSeq = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
      } catch (EmptyResultDataAccessException e) {
        nextValSeq = 0;
      }
      return nextValSeq;
    }

}
