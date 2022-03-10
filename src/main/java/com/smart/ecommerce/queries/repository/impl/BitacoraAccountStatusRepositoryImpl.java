package com.smart.ecommerce.queries.repository.impl;

import com.smart.ecommerce.queries.model.dto.BitacoraAccountStatusDto;
import com.smart.ecommerce.queries.repository.BitacoraAccountStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Repository
public class BitacoraAccountStatusRepositoryImpl implements BitacoraAccountStatusRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public BitacoraAccountStatusDto getBitacoraByIdMembership(int idMembership, Date initDate, Date endDate) {
        try {
            log.info("Obteniendo bitacora por id membership");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.parse(format.format(initDate));

            return jdbcTemplate.queryForObject(GET_BITACORA_ACCOUNT_STATUS,
                    new Object[]{idMembership, format.format(initDate), format.format(endDate)},
                    new BeanPropertyRowMapper<>(BitacoraAccountStatusDto.class));
        } catch (Exception ex) {
            log.error("Error al realizar consulta--> {}", ex.getMessage());
            return null;
        }
    }
}
