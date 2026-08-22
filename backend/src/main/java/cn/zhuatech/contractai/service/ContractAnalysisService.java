/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.service;
import jakarta.validation.constraints.*; import org.springframework.stereotype.Service; import java.math.BigDecimal; import java.time.LocalDate; import java.time.temporal.ChronoUnit; import java.util.*;
/** 合同条款、金额、履约和续约信号的可解释风险评估。 */
@Service public class ContractAnalysisService {
 public Result analyze(Request r){int score=0;List<String> f=new ArrayList<>();if(r.missingRequiredClauses()>0){score+=Math.min(30,r.missingRequiredClauses()*10);f.add("存在必备条款缺失");}if(r.unlimitedLiability()){score+=30;f.add("包含无限责任条款");}if(r.autoRenewal()){score+=12;f.add("包含自动续约约定");}if(r.nonStandardPaymentTerm()){score+=16;f.add("付款条件偏离标准模板");}if(r.contractAmount().compareTo(new BigDecimal("1000000"))>=0){score+=15;f.add("合同金额达到重大合同门槛");}long days=Math.max(0,ChronoUnit.DAYS.between(LocalDate.now(),r.expiryDate()));if(days<=30){score+=12;f.add("合同将在30天内到期");}score=Math.min(100,score);String level=score>=70?"CRITICAL":score>=45?"HIGH":score>=20?"MEDIUM":"LOW";String route=score>=70?"LEGAL_DIRECTOR":score>=45?"LEGAL_REVIEW":score>=20?"BUSINESS_CONFIRM":"FAST_TRACK";if(f.isEmpty())f.add("未命中重大合同风险规则");return new Result(r.contractNo(),score,level,route,days,f,score>=45||r.unlimitedLiability(),score<45?"可进入业务审批":"完成法务复核后再签署");}
 public record Request(@NotBlank String contractNo,@NotBlank String counterparty,@DecimalMin("0") BigDecimal contractAmount,@Min(0)@Max(20)int missingRequiredClauses,boolean unlimitedLiability,boolean autoRenewal,boolean nonStandardPaymentTerm,@NotNull LocalDate expiryDate){}
 public record Result(String contractNo,int riskScore,String riskLevel,String reviewRoute,long daysToExpire,List<String> findings,boolean legalApprovalRequired,String recommendation){}
}
