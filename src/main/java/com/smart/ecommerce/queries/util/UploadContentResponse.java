package com.smart.ecommerce.queries.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

@Getter
@Setter
public class UploadContentResponse implements Serializable {

  @ApiModelProperty(name = "ownerDocumentId", example = "21",
    value = "Represents the record identifier of the owner document.")
  private Long ownerDocumentId;

  @ApiModelProperty(name = "name", example = "IFE_14_4.pdf",
    value = "Represents the name of the registered document.")
  private String name;

  @ApiModelProperty(name = "createdAt", example = "2020-10-10",
    value = "Represents the create date .")
  private String createdAt;

  @ApiModelProperty(name = "userByRegister", example = "user123",
    value = "Represents the registered user.")
  private String userByRegister;

  @ApiModelProperty(name = "ownerId", example = "Usuario",
    value = "Represents the owner.")
  private String ownerId;

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

}
