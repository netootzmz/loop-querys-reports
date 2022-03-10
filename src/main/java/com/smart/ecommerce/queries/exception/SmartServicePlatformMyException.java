/* ----------------------------------------------------------------------------
 * All rights reserved Smart Payment Services.
 *  
 * This software contains information that is exclusive property of Smart,this 
 * information is considered confidential.
 * It is strictly forbidden the copy or spreading of any part of this document 
 * in any format, whether mechanic or electronic.
 * ---------------------------------------------------------------------------
 */
package com.smart.ecommerce.queries.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <code>SmartServicePlatformMyException</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */

/**
 * Gets the more info.
 *
 * @return more info
 */
@Getter
@Setter

/**
 * To string.
 *
 * @return java.lang. string
 */
@ToString
public class SmartServicePlatformMyException extends Exception {

  /** The constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The constant uuid. */
  private final String uuid;

  /** The constant date. */
  private final String date;

  /** The constant type. */
  private final String type;

  /** The constant code. */
  private final String code;

  /** The constant moreInfo. */
  private final String moreInfo;

  /** La constante DATE_FORMAT. */
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

  /**
   * Creates a new instance of smart service platform my exception.
   *
   * @param message message
   */
  public SmartServicePlatformMyException(String message) {

    super(message);
    this.uuid = UUID.randomUUID().toString();
    this.date =
      LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    this.type = "invalid-request";
    this.code = "400";
    this.moreInfo = message;
  }
}
