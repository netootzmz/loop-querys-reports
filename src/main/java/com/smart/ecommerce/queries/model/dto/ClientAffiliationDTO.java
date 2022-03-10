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

package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class ClientAffiliationDTO implements Serializable {

  public String clientId;
  public String affiliation;
  public String businessId;
  public Integer typeAffiliation;
  public Integer statusId;
  public Integer typeClient;
  public Integer coreSaleChannelId;





}
