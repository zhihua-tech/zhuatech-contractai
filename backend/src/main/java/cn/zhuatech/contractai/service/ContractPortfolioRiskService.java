/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;

/** 汇总合同组合的交易对手集中度、法域、制裁与自动续约风险。 */
@Service
public class ContractPortfolioRiskService {
    public Result assess(Request request) {
        BigDecimal calculated = request.counterparties().stream().map(Counterparty::exposure)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal largest = request.counterparties().stream().map(Counterparty::exposure)
                .max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        BigDecimal concentration = request.totalExposure().signum() == 0 ? BigDecimal.ZERO
                : largest.divide(request.totalExposure(), 4, RoundingMode.HALF_UP);
        BigDecimal highRiskExposure = request.counterparties().stream()
                .filter(c -> c.highRiskJurisdiction() || c.sanctionsMatch()).map(Counterparty::exposure)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<String> blockers = new ArrayList<>(), warnings = new ArrayList<>(), actions = new ArrayList<>();
        if (calculated.compareTo(request.totalExposure()) != 0) blockers.add("交易对手敞口合计与组合总额不一致");
        request.counterparties().stream().filter(Counterparty::sanctionsMatch)
                .forEach(c -> blockers.add("交易对手命中制裁筛查: " + c.counterpartyCode()));
        if (concentration.compareTo(request.maxSingleCounterpartyRate()) > 0) warnings.add("单一交易对手集中度超过阈值");
        if (highRiskExposure.compareTo(request.maxHighRiskExposure()) > 0) warnings.add("高风险法域敞口超过阈值");
        request.counterparties().stream().filter(c -> c.autoRenewal() && c.expiresWithinNoticeWindow() && !c.terminationNoticeSent())
                .forEach(c -> warnings.add("自动续约通知窗口即将关闭: " + c.counterpartyCode()));
        Decision decision = !blockers.isEmpty() ? Decision.BLOCKED : !warnings.isEmpty() ? Decision.REVIEW : Decision.MONITOR;
        actions.add(decision == Decision.BLOCKED ? "冻结签署、付款与续约动作并交由法务核验"
                : decision == Decision.REVIEW ? "由法务和业务负责人处理集中度、法域或续约例外"
                : "按月复核敞口、履约和续约窗口");
        return new Result(decision, calculated, concentration, highRiskExposure,
                List.copyOf(blockers), List.copyOf(warnings), List.copyOf(actions));
    }

    public record Request(@NotBlank String portfolioId, @DecimalMin("0.01") BigDecimal totalExposure,
            @DecimalMin("0") @DecimalMax("1") BigDecimal maxSingleCounterpartyRate,
            @DecimalMin("0") BigDecimal maxHighRiskExposure,
            @NotEmpty List<@Valid Counterparty> counterparties) {}
    public record Counterparty(@NotBlank String counterpartyCode, @DecimalMin("0") BigDecimal exposure,
            boolean highRiskJurisdiction, boolean sanctionsMatch, boolean autoRenewal,
            boolean expiresWithinNoticeWindow, boolean terminationNoticeSent) {}
    public record Result(Decision decision, BigDecimal calculatedExposure,
            BigDecimal largestCounterpartyRate, BigDecimal highRiskExposure,
            List<String> blockers, List<String> warnings, List<String> actions) {}
    public enum Decision { MONITOR, REVIEW, BLOCKED }
}
