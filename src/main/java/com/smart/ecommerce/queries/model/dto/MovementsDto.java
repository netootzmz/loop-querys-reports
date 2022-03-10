package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MovementsDto {

    public String token;
    public Date month;
    public String groupId;
    public Integer monthId;
    private String rfc;
    private Date startDate;
    private Date endDate;

}
