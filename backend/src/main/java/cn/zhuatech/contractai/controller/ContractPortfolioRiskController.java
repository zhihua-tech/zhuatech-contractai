/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.controller;
import cn.zhuatech.contractai.common.ApiResponse;
import cn.zhuatech.contractai.service.ContractPortfolioRiskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/enterprise/contractai")
public class ContractPortfolioRiskController {
 private final ContractPortfolioRiskService service;
 public ContractPortfolioRiskController(ContractPortfolioRiskService service){this.service=service;}
 @PostMapping("/portfolio-risk") public ApiResponse<ContractPortfolioRiskService.Result> assess(@Valid @RequestBody ContractPortfolioRiskService.Request request){return ApiResponse.ok("合同组合风险评估完成",service.assess(request));}
}
