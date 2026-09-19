/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.controller;

import cn.zhuatech.contractai.common.ApiResponse;
import cn.zhuatech.contractai.service.ContractReviewPublicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/enterprise/contractai")
public class ContractReviewPublicationController {
    private final ContractReviewPublicationService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ContractReviewPublicationController(ContractReviewPublicationService service) { this.service = service; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/contract-review-publication")
    public ApiResponse<ContractReviewPublicationService.Assessment> assess(
            @Valid @RequestBody ContractReviewPublicationService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
