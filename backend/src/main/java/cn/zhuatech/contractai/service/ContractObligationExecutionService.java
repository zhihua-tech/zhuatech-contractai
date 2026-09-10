/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** 对合同义务、证据、续约通知期和重大违约风险执行统一履约决策。 */
@Service
public class ContractObligationExecutionService {
    public ExecutionResult evaluate(ExecutionRequest request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        List<ObligationDecision> decisions = new ArrayList<>();
        BigDecimal overdueExposure = BigDecimal.ZERO;
        boolean legalEscalation = false;

        if (!request.counterpartyActive()) blockers.add("交易对手已停用或被冻结");
        if (!request.sanctionsScreenPassed()) blockers.add("交易对手制裁筛查未通过");
        if (request.legalHold()) blockers.add("合同处于法务保全状态，禁止自动处置");

        for (Obligation obligation : request.obligations()) {
            if (obligation.status() == ObligationStatus.COMPLETED) {
                if (obligation.evidenceAttached()) {
                    decisions.add(decision(obligation, ObligationState.COMPLETED, 0, "履约证据完整"));
                } else {
                    actions.add("补齐义务 " + obligation.obligationId() + " 的履约证据");
                    decisions.add(decision(obligation, ObligationState.EVIDENCE_MISSING, 0, "完成状态缺少证据"));
                }
                continue;
            }
            if (obligation.status() == ObligationStatus.WAIVED || obligation.status() == ObligationStatus.DISPUTED) {
                if (!obligation.exceptionApproved()) {
                    actions.add("义务 " + obligation.obligationId() + " 的豁免或争议需要法务审批");
                    decisions.add(decision(obligation, ObligationState.EXCEPTION_REVIEW, 0, "例外尚未批准"));
                } else {
                    decisions.add(decision(obligation, ObligationState.EXCEPTION_APPROVED, 0, "例外审批已归档"));
                }
                continue;
            }

            long daysUntilDue = ChronoUnit.DAYS.between(request.asOfDate(), obligation.dueDate());
            if (daysUntilDue < 0) {
                long overdueDays = Math.abs(daysUntilDue);
                overdueExposure = overdueExposure.add(obligation.exposureAmount());
                decisions.add(decision(obligation, ObligationState.OVERDUE, overdueDays, "义务已经逾期"));
                actions.add("立即处理逾期义务 " + obligation.obligationId());
                if (obligation.critical()) legalEscalation = true;
            } else if (daysUntilDue <= 7) {
                decisions.add(decision(obligation, ObligationState.DUE_SOON, daysUntilDue, "七日内到期"));
                actions.add("确认义务 " + obligation.obligationId() + " 的责任人与交付证据");
            } else {
                decisions.add(decision(obligation, ObligationState.ON_TRACK, daysUntilDue, "义务处于履约窗口内"));
            }
        }

        if (request.autoRenewal() && !request.renewalDecisionRecorded()) {
            if (request.renewalNoticeDate() == null) {
                blockers.add("自动续约合同缺少续约通知截止日");
            } else {
                long daysToNotice = ChronoUnit.DAYS.between(request.asOfDate(), request.renewalNoticeDate());
                if (daysToNotice < 0) {
                    legalEscalation = true;
                    actions.add("自动续约通知期已错过，立即升级法务评估");
                } else if (daysToNotice <= 14) {
                    actions.add("在续约通知截止日前完成续签或终止决策");
                }
            }
        }
        if (!request.monitoringEnabled()) actions.add("启用合同义务到期、逾期和续约通知监控");

        if (!blockers.isEmpty()) {
            return result(Decision.BLOCKED, overdueExposure, decisions, blockers, actions);
        }
        if (legalEscalation) {
            actions.add("创建重大履约风险案件并通知合同负责人和法务");
            return result(Decision.LEGAL_ESCALATION, overdueExposure, decisions, blockers, actions);
        }
        if (!actions.isEmpty()) {
            return result(Decision.ACTION_REQUIRED, overdueExposure, decisions, blockers, actions);
        }
        actions.add("维持履约计划并持续归档可验证证据");
        return result(Decision.ON_TRACK, overdueExposure, decisions, blockers, actions);
    }

    private ObligationDecision decision(Obligation obligation, ObligationState state,
                                        long dayMetric, String reason) {
        return new ObligationDecision(obligation.obligationId(), obligation.type(), state,
                obligation.owner(), dayMetric, obligation.exposureAmount(), reason);
    }

    private ExecutionResult result(Decision decision, BigDecimal overdueExposure,
                                   List<ObligationDecision> obligations, List<String> blockers,
                                   List<String> actions) {
        return new ExecutionResult(decision, overdueExposure, List.copyOf(obligations),
                List.copyOf(blockers), List.copyOf(actions));
    }

    public record ExecutionRequest(
            @NotBlank String contractNo,
            @NotNull LocalDate asOfDate,
            @NotEmpty List<@Valid Obligation> obligations,
            boolean counterpartyActive,
            boolean sanctionsScreenPassed,
            boolean legalHold,
            boolean autoRenewal,
            LocalDate renewalNoticeDate,
            boolean renewalDecisionRecorded,
            boolean monitoringEnabled
    ) {}

    public record Obligation(
            @NotBlank String obligationId,
            @NotNull ObligationType type,
            @NotBlank String owner,
            @NotNull LocalDate dueDate,
            @NotNull ObligationStatus status,
            boolean evidenceAttached,
            boolean critical,
            @NotNull @DecimalMin("0.0") BigDecimal exposureAmount,
            boolean exceptionApproved
    ) {}

    public record ObligationDecision(String obligationId, ObligationType type, ObligationState state,
                                     String owner, long dayMetric, BigDecimal exposureAmount, String reason) {}
    public record ExecutionResult(Decision decision, BigDecimal overdueExposure,
                                  List<ObligationDecision> obligations, List<String> blockers,
                                  List<String> actions) {}

    public enum ObligationType { PAYMENT, DELIVERY, REPORTING, COMPLIANCE, RENEWAL }
    public enum ObligationStatus { OPEN, COMPLETED, WAIVED, DISPUTED }
    public enum ObligationState { ON_TRACK, DUE_SOON, OVERDUE, COMPLETED, EVIDENCE_MISSING,
        EXCEPTION_REVIEW, EXCEPTION_APPROVED }
    public enum Decision { ON_TRACK, ACTION_REQUIRED, LEGAL_ESCALATION, BLOCKED }
}
