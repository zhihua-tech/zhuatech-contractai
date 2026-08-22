/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.controller;

import cn.zhuatech.contractai.common.ApiResponse;
import cn.zhuatech.contractai.service.ContractAnalysisService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/contract")
@PreAuthorize("hasAnyRole('DOMAIN_USER','DOMAIN_OPERATOR','ADMIN')")
public class ContractAnalysisController {
    private final ContractAnalysisService service;
    public ContractAnalysisController(ContractAnalysisService service) { this.service = service; }
    @PostMapping("/analyze")
    public ApiResponse<ContractAnalysisService.Result> analyze(@Valid @RequestBody ContractAnalysisService.Request request) {
        return ApiResponse.ok("合同智能审查完成", service.analyze(request));
    }
}
