package com.test.demo.dao;

import com.test.demo.domain.EnterpriseDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EnterpriseDAO {

    int insertSelective(EnterpriseDO domain);
}