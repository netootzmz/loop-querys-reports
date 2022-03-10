package com.smart.ecommerce.queries.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionsDto {

    @JsonProperty(value = "approval", required = false)
    @ApiModelProperty(name = "approval", required = false,
            value = "Represents the approval.",
            example = "123456")
    private String approval;

    @JsonProperty(value = "cardBrand", required = false)
    @ApiModelProperty(name = "cardBrand", required = false,
            value = "Represents the card brand : VISA, MASTERCARD, AMERICAN EXPRESS.",
            example = "VISA")
    private String cardBrand;

    @JsonProperty(value = "cardType", required = false)
    @ApiModelProperty(name = "cardType", required = false,
            value = "Represents the card type: 1.- credit card, 2.- debit.",
            example = "1")
    private String cardType;

    @JsonProperty(value = "transmitter", required = false)
    @ApiModelProperty(name = "transmitter", required = false,
            value = "Represents the transmitter.",
            example = "BANCOMER")
    private String transmitter;

    @JsonProperty(value = "endDate", required = false)
    @ApiModelProperty(name = "endDate", required = false)
    private Date endDate;

    @JsonProperty(value = "endHour", required = false)
    @ApiModelProperty(name = "endHour", required = false,
            value = "Represents the end hour in the format 24 hrs.",
            example = "23:59:59")
    private String endHour;

    @JsonProperty(value = "initDate", required = false)
    @ApiModelProperty(name = "initDate", required = false)
    private Date initDate;

    @JsonProperty(value = "initHour", required = false)
    @ApiModelProperty(name = "initHour", required = false,
            value = "Represents the init hour in the format 24 hrs.",
            example = "00:00:00")
    private String initHour;

    @JsonProperty(value = "operationTypeId", required = false)
    @ApiModelProperty(name = "operationTypeId", required = false,
            value = "Represents the operation type id like type transaction id.",
            example = "2")
    private Integer operationTypeId;

    @JsonProperty(value = "paymentReference", required = false)
    @ApiModelProperty(name = "paymentReference", required = false,
            value = "Represents the payment reference.",
            example = "000012345678")
    private String paymentReference;

    @JsonProperty(value = "productId", required = false)
    @ApiModelProperty(name = "productId", required = false,
            value = "Represents the product id like type smartlink.",
            example = "1")
    private Integer productId;

    @JsonProperty(value = "saleAmount", required = false)
    @ApiModelProperty(name = "saleAmount", required = false)
    private Double saleAmount;

    @JsonProperty(value = "token", required = false)
    @ApiModelProperty(name = "token", required = false)
    private String token;

    /*Se depura objecto de entrada y se inhabilatan los siguientes atributos*/
  /*
   **
    private Integer bankCardBin
    private Integer clientId
    private Integer membershipId
    private Integer msiId
    private Integer entryModeId
    private Integer providerId
    //TO - DO no se sabe si son campos capturables o identificadores de catalogo
    private String nature
    private String eci
    private String merchantNumber
    private String transmitter
    private String paymentStatus
    private Integer paymentStatusId
   **
   */

}
