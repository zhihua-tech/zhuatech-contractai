/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.controller;

import cn.zhuatech.contractai.common.ApiResponse;
import cn.zhuatech.contractai.service.ContractReviewPublicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enterprise/contractai")
public class ContractReviewPublicationController {
    private final ContractReviewPublicationService service;
    public ContractReviewPublicationController(ContractReviewPublicationService service) { this.service = service; }
    @PostMapping("/contract-review-publication")
    public ApiResponse<ContractReviewPublicationService.Assessment> assess(
            @Valid @RequestBody ContractReviewPublicationService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
