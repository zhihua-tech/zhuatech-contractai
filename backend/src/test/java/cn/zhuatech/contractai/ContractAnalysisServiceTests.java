/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.contractai; import cn.zhuatech.contractai.service.ContractAnalysisService; import org.junit.jupiter.api.Test; import java.math.BigDecimal; import java.time.LocalDate; import static org.assertj.core.api.Assertions.assertThat;
class ContractAnalysisServiceTests {private final ContractAnalysisService s=new ContractAnalysisService();
 @Test void escalatesUnlimitedLiabilityContract(){var r=s.analyze(new ContractAnalysisService.Request("CT-88","华东客户",new BigDecimal("2200000"),2,true,true,true,LocalDate.now().plusDays(20)));assertThat(r.riskLevel()).isEqualTo("CRITICAL");assertThat(r.legalApprovalRequired()).isTrue();}
 @Test void fastTracksStandardContract(){var r=s.analyze(new ContractAnalysisService.Request("CT-20","长期客户",new BigDecimal("80000"),0,false,false,false,LocalDate.now().plusDays(300)));assertThat(r.reviewRoute()).isEqualTo("FAST_TRACK");}}
