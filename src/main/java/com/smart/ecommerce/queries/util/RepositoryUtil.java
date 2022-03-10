package com.smart.ecommerce.queries.util;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/** The constant log. */
@Slf4j
@Component

/**
 * <code>RepositoryUtil</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */
public class RepositoryUtil {


  /**
   * Gets the sequence.
   *
   * @param sequenceName sequence name
   * @param jdbcTemplate jdbc template
   * @return sequence
   */
  public Long getSequence(String sequenceName, JdbcTemplate jdbcTemplate) {

    log.info("daoUtil.getSequence : {}", sequenceName);
    Long id = null;
    OracleSequenceMaxValueIncrementer seq =
      new OracleSequenceMaxValueIncrementer();
    seq.setDataSource(jdbcTemplate.getDataSource());
    seq.setIncrementerName(sequenceName);
    id = seq.nextLongValue();
    log.info("daoUtil.getSequence : {} NextVal : {}", sequenceName, id);
    return id;
  }
  
  /**
   * Checks for column.
   *
   * @param rs rs
   * @param columnName column name
   * @return true, if the condition is satisfied.
   */
  public boolean hasColumn(ResultSet rs, String columnName) {
    Boolean rtn = Boolean.FALSE;
    try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            for (int x = 1; x <= columns; x++) {
                    if (columnName.equals(rsmd.getColumnName(x))) {
                            rtn = Boolean.TRUE;
                    }
            }
    } catch (Exception ex) {
            log.warn(ex.getMessage());
    }
    return rtn;
}

  /**
   * Rs field DB.
   *
   * @param rs rs
   * @param nameField name field
   * @return object
   * @throws SQLException SQL exception.
   */
  /* Valida el hasColumn para los set de tipo BigDecimal, String, Date, Blob */
  public Object rsFieldDB(ResultSet rs, String nameField) throws SQLException {
          return hasColumn(rs, nameField) ? rs.getObject(nameField) : null;
  }

  /**
   * Rs field int DB.
   *
   * @param rs rs
   * @param nameField name field
   * @return integer
   * @throws SQLException SQL exception.
   */
  /* Valida el hasColumn para Integer y para el Long se sobrepone el longValue */
  public Integer rsFieldIntDB(ResultSet rs, String nameField) throws SQLException {
          return hasColumn(rs, nameField) ? rs.getInt(nameField) : null;
  }

  /**
   * Rs field long DB.
   *
   * @param rs rs
   * @param nameField name field
   * @return long
   * @throws SQLException SQL exception.
   */
  /* Valida el hasColumn para Integer y para el Long se sobrepone el longValue */
  public Long rsFieldLongDB(ResultSet rs, String nameField) throws SQLException {
          return hasColumn(rs, nameField) ? rs.getInt(nameField) : 0L;
  }


}