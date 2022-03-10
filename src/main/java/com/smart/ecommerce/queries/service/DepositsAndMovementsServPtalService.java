package com.smart.ecommerce.queries.service;

import com.smart.ecommerce.queries.model.dto.DepositsAndMovementsServPtalDto;
import com.smart.ecommerce.queries.model.dto.InfoTokenDto;
import com.smart.ecommerce.queries.util.GenericResponse;

public interface DepositsAndMovementsServPtalService {
    GenericResponse getDepositsAndMovementsServPtal(String idOperation, DepositsAndMovementsServPtalDto dto, InfoTokenDto infoTokenDto);
}
