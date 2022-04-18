package com.test.demo.dao;

import com.test.demo.domain.EnterpriseDO;
import com.test.demo.dto.EnterpriseQueryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EnterpriseDAO {

    int create(EnterpriseDO domain);

    List<EnterpriseDO> list(EnterpriseQueryDTO enterpriseQueryDTO);
}
