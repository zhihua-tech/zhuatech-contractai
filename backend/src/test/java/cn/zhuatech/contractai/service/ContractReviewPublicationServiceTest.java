/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class ContractReviewPublicationServiceTest {
    private final ContractReviewPublicationService service = new ContractReviewPublicationService();
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void publishesControlledReview() {
        var result = service.assess(new ContractReviewPublicationService.Request("C1", true, true, true,
                true, true, false, false, true, true, true, true));
        assertThat(result.decision()).isEqualTo(ContractReviewPublicationService.Decision.PUBLISH);
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void sendsGovernanceGapsToCounsel() {
        var result = service.assess(new ContractReviewPublicationService.Request("C2", true, true, true,
                true, true, false, false, false, false, false, true));
        assertThat(result.actions()).hasSize(3);
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void blocksUnsafePublication() {
        var result = service.assess(new ContractReviewPublicationService.Request("C3", false, false, false,
                false, false, true, false, true, true, true, false));
        assertThat(result.blockers()).hasSize(7);
    }
}
