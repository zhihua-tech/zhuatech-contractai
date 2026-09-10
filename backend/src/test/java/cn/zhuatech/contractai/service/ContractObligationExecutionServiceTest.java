/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContractObligationExecutionServiceTest {
    private final ContractObligationExecutionService service = new ContractObligationExecutionService();
    private final LocalDate today = LocalDate.of(2026, 9, 11);

    @Test
    void keepsVerifiedObligationsOnTrack() {
        var result = service.evaluate(request(List.of(obligation("O1", today.plusDays(30), false,
                ContractObligationExecutionService.ObligationStatus.OPEN, true)), false, null, true));
        assertThat(result.decision()).isEqualTo(ContractObligationExecutionService.Decision.ON_TRACK);
        assertThat(result.obligations().getFirst().state())
                .isEqualTo(ContractObligationExecutionService.ObligationState.ON_TRACK);
    }

    @Test
    void requiresActionForMissingEvidenceAndUpcomingDuty() {
        var result = service.evaluate(request(List.of(
                obligation("O1", today.minusDays(1), false,
                        ContractObligationExecutionService.ObligationStatus.COMPLETED, false),
                obligation("O2", today.plusDays(5), false,
                        ContractObligationExecutionService.ObligationStatus.OPEN, false)
        ), false, null, true));
        assertThat(result.decision()).isEqualTo(ContractObligationExecutionService.Decision.ACTION_REQUIRED);
        assertThat(result.actions()).hasSize(2);
    }

    @Test
    void escalatesCriticalOverdueExposureAndMissedRenewalNotice() {
        var result = service.evaluate(request(List.of(obligation("O1", today.minusDays(9), true,
                ContractObligationExecutionService.ObligationStatus.OPEN, false)), true,
                today.minusDays(2), false));
        assertThat(result.decision()).isEqualTo(ContractObligationExecutionService.Decision.LEGAL_ESCALATION);
        assertThat(result.overdueExposure()).isEqualByComparingTo("500000");
    }

    @Test
    void blocksSanctionedCounterparty() {
        var base = request(List.of(obligation("O1", today.plusDays(30), false,
                ContractObligationExecutionService.ObligationStatus.OPEN, true)), false, null, true);
        var unsafe = new ContractObligationExecutionService.ExecutionRequest(base.contractNo(), today,
                base.obligations(), true, false, false, false, null, true, true);
        assertThat(service.evaluate(unsafe).decision())
                .isEqualTo(ContractObligationExecutionService.Decision.BLOCKED);
    }

    private ContractObligationExecutionService.ExecutionRequest request(
            List<ContractObligationExecutionService.Obligation> obligations, boolean autoRenewal,
            LocalDate noticeDate, boolean renewalDecision) {
        return new ContractObligationExecutionService.ExecutionRequest("CT-100", today, obligations,
                true, true, false, autoRenewal, noticeDate, renewalDecision, true);
    }

    private ContractObligationExecutionService.Obligation obligation(
            String id, LocalDate dueDate, boolean critical,
            ContractObligationExecutionService.ObligationStatus status, boolean evidence) {
        return new ContractObligationExecutionService.Obligation(id,
                ContractObligationExecutionService.ObligationType.DELIVERY, "owner-a", dueDate,
                status, evidence, critical, new BigDecimal("500000"), false);
    }
}
