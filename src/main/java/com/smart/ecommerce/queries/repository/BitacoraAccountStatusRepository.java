package com.smart.ecommerce.queries.repository;

import com.smart.ecommerce.queries.model.dto.BitacoraAccountStatusDto;

import java.util.Date;

public interface BitacoraAccountStatusRepository {
    String GET_BITACORA_ACCOUNT_STATUS = "SELECT * FROM `smart_core_platform`.`bitacora_account_status` " +
            "WHERE idMembership=? AND init_date=? AND end_date=?";

    BitacoraAccountStatusDto getBitacoraByIdMembership(int idMembership, Date initDate, Date endDate);
}
