/*
 *  ----------------------------------------------------------------------------
 *   All rights reserved © 2020 Smart.
 *
 *   This software contains information that is exclusive property of Smart,this
 *   information is considered confidential.
 *   It is strictly forbidden the copy or spreading of any part of this document
 *   in any format, whether mechanic or electronic.
 *   ---------------------------------------------------------------------------
 */

package com.smart.ecommerce.queries.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <code>ErrorResponse</code>.
 *
 * @author jefloresg
 * @version 1.0
 */
@Data
public class ErrorResponse implements Serializable {

  /**
   * The constant type.
   */
  @ApiModelProperty(name = "type", value = "Represents the type of exception.")
  private final String type;

  /**
   * The constant code.
   */
  @ApiModelProperty(name = "code",
    value = "Represents the response status code.")
  private final String code;

  /**
   * The constant details.
   */
  @ApiModelProperty(name = "details", value = "Represents the error detail.")
  private final String details;

  /**
   * The constant location.
   */
  @ApiModelProperty(name = "location",
    value = "Represents the location where the error was thrown.")
  private final String location;

  /**
   * The constant moreInfo.
   */
  @ApiModelProperty(name = "moreInfo", value = "More information.")
  private final String moreInfo;

  /**
   * Creates a new instance of error response.
   *
   * @param type
   *          type
   * @param code
   *          code
   * @param details
   *          details
   * @param location
   *          location
   * @param moreInfo
   *          more info
   */
  public ErrorResponse(String type, String code, String details,
    String location, String moreInfo) {

    super();
    this.type = type;
    this.code = code;
    this.details = details;
    this.location = location;
    this.moreInfo = moreInfo;
  }

  /**
   * Gets the type.
   *
   * @return type
   */
  public String getType() {

    return type;
  }

  /**
   * Gets the code.
   *
   * @return code
   */
  public String getCode() {

    return code;
  }

  /**
   * Gets the details.
   *
   * @return details
   */
  public String getDetails() {

    return details;
  }

  /**
   * Gets the location.
   *
   * @return location
   */
  public String getLocation() {

    return location;
  }

  /**
   * Gets the more info.
   *
   * @return more info
   */
  public String getMoreInfo() {

    return moreInfo;
  }



}
