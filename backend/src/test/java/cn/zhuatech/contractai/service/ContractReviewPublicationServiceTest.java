/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ContractReviewPublicationServiceTest {
    private final ContractReviewPublicationService service = new ContractReviewPublicationService();
    @Test void publishesControlledReview() {
        var result = service.assess(new ContractReviewPublicationService.Request("C1", true, true, true,
                true, true, false, false, true, true, true, true));
        assertThat(result.decision()).isEqualTo(ContractReviewPublicationService.Decision.PUBLISH);
    }
    @Test void sendsGovernanceGapsToCounsel() {
        var result = service.assess(new ContractReviewPublicationService.Request("C2", true, true, true,
                true, true, false, false, false, false, false, true));
        assertThat(result.actions()).hasSize(3);
    }
    @Test void blocksUnsafePublication() {
        var result = service.assess(new ContractReviewPublicationService.Request("C3", false, false, false,
                false, false, true, false, true, true, true, false));
        assertThat(result.blockers()).hasSize(7);
    }
}
