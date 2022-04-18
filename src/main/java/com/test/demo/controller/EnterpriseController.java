package com.test.demo.controller;

import com.test.demo.domain.EnterpriseDO;
import com.test.demo.dto.EnterpriseQueryDTO;
import com.test.demo.exception.BusinessException;
import com.test.demo.response.Result;
import com.test.demo.service.EnterpriseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Api(tags = "企业")
@RestController
@RequestMapping("/api/enterprise")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EnterpriseController {
    private final EnterpriseService enterpriseService;

    @ApiOperation("创建企业")
    @PostMapping("/create")
    public Result create() {
        EnterpriseDO enterpriseDO = new EnterpriseDO();
        enterpriseDO.setCreateBy(1);
        enterpriseDO.setCreateTime(new Date());
        enterpriseDO.setUpdateBy(1);
        enterpriseDO.setUpdateTime(new Date());
        Integer result = enterpriseService.create(enterpriseDO);
        if (result < 1) {
            throw new BusinessException("400110000", "创建新企业失败");
        }
        return Result.ok();
    }

    @ApiOperation("企业列表")
    @PostMapping("/list")
    public Result<List<EnterpriseDO>> list(@RequestBody EnterpriseQueryDTO enterpriseQueryDTO) {
        return Result.of(enterpriseService.list(enterpriseQueryDTO));
    }
}
