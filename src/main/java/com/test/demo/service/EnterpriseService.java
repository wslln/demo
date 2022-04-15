package com.test.demo.service;

import com.test.demo.dao.EnterpriseDAO;
import com.test.demo.domain.EnterpriseDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EnterpriseService {

    private final EnterpriseDAO enterpriseDAO;

    public Integer create(EnterpriseDO enterpriseDO) {
        return enterpriseDAO.create(enterpriseDO);
    }

    public List<EnterpriseDO> list() {
        return enterpriseDAO.list();
    }
}
