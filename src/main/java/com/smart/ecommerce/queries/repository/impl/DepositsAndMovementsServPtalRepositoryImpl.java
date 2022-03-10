package com.smart.ecommerce.queries.repository.impl;

import com.smart.ecommerce.entity.checkout.ReferencePaymentDispersion;
import com.smart.ecommerce.queries.model.dto.*;
import com.smart.ecommerce.queries.repository.DepositsAndMovementsServPtalRepository;
import com.smart.ecommerce.queries.repository.TransactionsServPtalRepository;
import com.smart.ecommerce.queries.util.RepositoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.smart.ecommerce.queries.util.Constants.*;

//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.jdbc.core.JdbcTemplate

@Slf4j
@Repository("DepositsAndMovementsServPtalRepository")
public class DepositsAndMovementsServPtalRepositoryImpl implements DepositsAndMovementsServPtalRepository {
  
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
  
  private static final String FIELD_AMOUNT = "amount";

  @Override
  public List<ReferencePaymentDispersionResponseDto> getDepositsAndMovementsServPtal(
          String initDate,
          String endDate,
          String membership,
          String paymentReference,
          String clabe,
          String clientId
  ) {
    List<ReferencePaymentDispersionResponseDto> list = new ArrayList<>();

    StringBuilder sql = new StringBuilder();

    sql.append( SQL_SELECT );
    sql.append( " rpd.*, cai.clabe, dsc.cve AS dispersionStatusCve " );

    if ( !clabe.isEmpty() ) {
      sql.append( ", CONCAT('" + clabe + "') AS clabe " );
    }

    sql.append( SQL_FROM + " reference_payment_dispersion AS rpd ");
    sql.append( " INNER JOIN dispersion_status_catalog AS dsc ON dsc.dispersion_status_id = rpd.dispersion_status_id" );
    sql.append( " INNER JOIN membership AS m ON m.membership = rpd.commerce " );
    sql.append( " INNER JOIN client_account_info AS cai ON cai.group_id = m.group_id" );
    sql.append( " WHERE " );
    if( !membership.isEmpty() ) {
      sql.append( " commerce = '" + membership + "' AND " );
    }
    sql.append( " DATE(rpd.created_at) BETWEEN '" + initDate + "' AND '" + endDate + "' " +
            " AND cai.client_id = '" + clientId + "' " +
            " AND cai.clabe IS NOT NULL " );
    if ( !paymentReference.isEmpty() ) {
      sql.append( " AND payment_reference = '" + paymentReference + "' " );
    }

    log.info(SQL_LOG, sql);

    try {

      /*
        list = em.createNativeQuery(sql.toString()).unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.aliasToBean(ClientAffiliationResponseDto.class))
                .getResultList()
       */

      list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<ReferencePaymentDispersionResponseDto>(ReferencePaymentDispersionResponseDto.class));

      return list;

    } catch (Exception e) {

      e.printStackTrace();
      return Collections.emptyList();
    }
  }

}
