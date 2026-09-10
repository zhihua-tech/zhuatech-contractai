/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.controller;

import cn.zhuatech.contractai.common.ApiResponse;
import cn.zhuatech.contractai.service.ContractObligationExecutionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise/contractai")
public class ContractObligationExecutionController {
    private final ContractObligationExecutionService service;

    public ContractObligationExecutionController(ContractObligationExecutionService service) {
        this.service = service;
    }

    @PostMapping("/obligation-execution")
    public ApiResponse<ContractObligationExecutionService.ExecutionResult> evaluate(
            @Valid @RequestBody ContractObligationExecutionService.ExecutionRequest request) {
        return ApiResponse.ok("合同义务履约评估完成", service.evaluate(request));
    }
}
