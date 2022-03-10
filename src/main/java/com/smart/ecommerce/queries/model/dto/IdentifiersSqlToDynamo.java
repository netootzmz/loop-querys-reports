package com.smart.ecommerce.queries.model.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IdentifiersSqlToDynamo {
    private String merchantNumber;
    private List<String> folioTxn;
    //private String folioTxn
}
