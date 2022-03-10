package com.smart.ecommerce.queries.repository;

import com.smart.ecommerce.entity.admin.Client;
import com.smart.ecommerce.entity.checkout.ReferencePaymentDispersion;
import com.smart.ecommerce.queries.model.dto.ReferencePaymentDispersionResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DepositsAndMovementsServPtalRepository {

    List<ReferencePaymentDispersionResponseDto> getDepositsAndMovementsServPtal(
            String initDate,
            String endDate,
            String membership,
            String paymentReference,
            String clabe,
            String clientId
    );
}
