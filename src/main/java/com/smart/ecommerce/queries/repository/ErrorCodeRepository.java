package com.smart.ecommerce.queries.repository;

import com.smart.ecommerce.entity.core.CoreErrorCode;

import java.util.List;

public interface ErrorCodeRepository {

  List<CoreErrorCode> getAll(Integer languageId);

  CoreErrorCode getByCode(String code, Integer languageId);

}
