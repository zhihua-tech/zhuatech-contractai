/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.controller;

import cn.zhuatech.contractai.common.ApiResponse;
import cn.zhuatech.contractai.service.ContractAnalysisService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/ai/contract")
@PreAuthorize("hasAnyRole('DOMAIN_USER','DOMAIN_OPERATOR','ADMIN')")
public class ContractAnalysisController {
    private final ContractAnalysisService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ContractAnalysisController(ContractAnalysisService service) { this.service = service; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/analyze")
    public ApiResponse<ContractAnalysisService.Result> analyze(@Valid @RequestBody ContractAnalysisService.Request request) {
        return ApiResponse.ok("合同智能审查完成", service.analyze(request));
    }
}
