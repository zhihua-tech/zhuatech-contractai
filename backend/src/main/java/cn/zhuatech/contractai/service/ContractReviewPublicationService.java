/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContractReviewPublicationService {
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (!request.sourceCitationsVerified()) blockers.add("审查结论来源引用未验证");
        if (!request.clauseTraceabilityComplete()) blockers.add("风险结论无法追溯到合同条款");
        if (!request.jurisdictionConfirmed()) blockers.add("适用法域未确认");
        if (!request.confidentialityControlsEnabled()) blockers.add("合同保密控制未启用");
        if (!request.piiHandlingApproved()) blockers.add("个人信息处理方式未批准");
        if (request.materialRiskDetected() && !request.materialRiskEscalated()) blockers.add("重大合同风险未升级");
        if (!request.finalLegalApprovalComplete()) blockers.add("最终法律审批未完成");
        if (!blockers.isEmpty()) {
            actions.add("阻断审查意见发布并交由授权法务复核");
            return new Assessment(Decision.BLOCKED, blockers, actions);
        }
        if (!request.qualifiedCounselReviewed() || !request.modelVersionRecorded() || !request.auditTrailComplete()) {
            if (!request.qualifiedCounselReviewed()) actions.add("由具备资质的法务人员完成人工复核");
            if (!request.modelVersionRecorded()) actions.add("记录模型、提示词和知识库版本");
            if (!request.auditTrailComplete()) actions.add("补齐输入、修改、审批和发布审计轨迹");
            return new Assessment(Decision.COUNSEL_REVIEW, blockers, actions);
        }
        actions.add("发布审查意见并归档条款证据、版本和审批记录");
        return new Assessment(Decision.PUBLISH, blockers, actions);
    }

    public record Request(@NotBlank String reviewId, boolean sourceCitationsVerified,
                          boolean clauseTraceabilityComplete, boolean jurisdictionConfirmed,
                          boolean confidentialityControlsEnabled, boolean piiHandlingApproved,
                          boolean materialRiskDetected, boolean materialRiskEscalated,
                          boolean qualifiedCounselReviewed, boolean modelVersionRecorded,
                          boolean auditTrailComplete, boolean finalLegalApprovalComplete) {}
    public record Assessment(Decision decision, List<String> blockers, List<String> actions) {}
    public enum Decision { PUBLISH, COUNSEL_REVIEW, BLOCKED }
}
