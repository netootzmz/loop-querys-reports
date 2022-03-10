package com.smart.ecommerce.queries.repository.impl;

import com.smart.ecommerce.entity.core.CoreErrorCode;
import com.smart.ecommerce.queries.repository.ErrorCodeRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class ErrorCodeRepositoryImpl implements ErrorCodeRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<CoreErrorCode> getAll(Integer languageId) {
        List<CoreErrorCode> coreError = new ArrayList<>();
        try {
            coreError = em.createNamedQuery("getAll")
                    .setParameter("language_id", languageId).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return coreError;
    }

    @Override
    public CoreErrorCode getByCode(String code, Integer languageId) {
        List<CoreErrorCode> module = em.createNamedQuery("getByCode")
                .setParameter("code", code)
                .setParameter("languageId", languageId)
                .getResultList();
        return module.size() > 0 ? module.get(0) : new CoreErrorCode();
    }
}
