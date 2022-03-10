/* ----------------------------------------------------------------------------
 * All rights reserved Smart Payment Services.
 *
 * This software contains information that is exclusive property of Smart,this
 * information is considered confidential.
 * It is strictly forbidden the copy or spreading of any part of this document
 * in any format, whether mechanic or electronic.
 * ---------------------------------------------------------------------------
 */
package com.smart.ecommerce.queries.util;


/**
 * <code>Constants</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */
public class Constants {

  /**
   * Creates a new instance of constants.
   */
  private Constants() { }

  /** The constant SPACE. */
  public static final String SPACE = " ";
  
  public static final String EMPTY = "";

  /** The constant TAB. */
  public static final String TAB = "\t";

  /** La constante SEPARATE_LINE. */
  public static final String SEPARATE_LINE = "\n";

  /** The constant BR. */
  public static final String BR = "<BR>";

  /** La constante OPR_AND. */
  public static final String OPR_AND = " AND ";

  /** La constante OPR_BETWEEN. */
  public static final String OPR_BETWEEN = " BETWEEN ";

  /** La constante OPR_EQUAL. */
  public static final String OPR_EQUAL = " = ";

  /** La constante OPR_EQUAL_MINOR. */
  public static final String OPR_EQUAL_MINOR = " <= ";

  /** La constante OPR_EQUAL_MAJOR. */
  public static final String OPR_EQUAL_MAJOR = " >= ";

  /** La constante OPR_IN. */
  public static final String OPR_IN = " IN ";

  /** The constant SET. */
  public static final String SET = " SET ";

  /** The constant POINT. */
  public static final String POINT = ".";

  /** La constante SQL_LOG. */
  public static final String SQL_LOG = "SQL:  {}";

  /** La constante QUERY_LOG. */
  public static final String QUERY_LOG = "QUERY:  {}";
  
  /** La constante QUERY_KEY_CONDITION_LOG. */
  public static final String QUERY_KEY_CONDITION_LOG = "QUERY KEY CONDITION:  {}";

  /** La constante JSON_ARRAY_LOG . */
  public static final String JSON_ARRAY_LOG = "JSON_ARRAY:  {}";

  /** La constante ARRAY_LIST_LOG . */
  public static final String ARRAY_LIST_LOG = "LIST:  {}";

  /** The constant UNDERSCORE. */
  public static final String UNDERSCORE = "_";

  /** La constante PARENTHESIS_OPEN. */
  public static final String PARENTHESIS_OPEN = "(";

  /** La constante PARENTHESIS_CLOSE. */
  public static final String PARENTHESIS_CLOSE = ")";

  public static final String DATA_EMPTY_DYNAMO = "Data empty in dinamodb: {}";
  
  /** La constante ERROR_QUERY_DYNAMO_LOG . */
  public static final String ERROR_QUERY_DYNAMO_LOG = "Error Query Dynamo:  {}";
  
  /** La constante ERROR_EMPTY_DYNAMODB_LOG . */
  public static final String ERROR_EMPTY_DYNAMODB_LOG = "Error Data empty in dinamodb {}";
  
  /** La constante START_HOURS. */
  public static final String START_HOURS = " 00:00:00";
  
  /** La constante END_HOURS. */
  public static final String END_HOURS = " 23:59:59";

}
