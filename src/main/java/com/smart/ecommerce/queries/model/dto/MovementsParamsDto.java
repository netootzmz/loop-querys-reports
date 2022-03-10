/* ----------------------------------------------------------------------------
 * All rights reserved Smart Payment Services.
 *
 * This software contains information that is exclusive property of Smart,this
 * information is considered confidential.
 * It is strictly forbidden the copy or spreading of any part of this document
 * in any format, whether mechanic or electronic.
 * ---------------------------------------------------------------------------
 */
package com.smart.ecommerce.queries.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * <code>MovementsParamsDto</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovementsParamsDto {

    @JsonProperty(value = "rfc", required = true)
    @ApiModelProperty(name = "rfc", required = true,
            value = "Represents the rfc.", example = "XXXX000000XX0")
    private String rfc;

    @JsonProperty(value = "startDate", required = true)
    @ApiModelProperty(name = "startDate", required = true,
            value = "Represents the startDate.", example = "1990-01-01")
    private String startDate;

    @JsonProperty(value = "token", required = true)
    @ApiModelProperty(name = "token", required = true,
            value = "Represents the token.", example = "eyJhbGciOi")
    public String token;
}
