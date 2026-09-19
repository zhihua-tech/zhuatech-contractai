/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.service;
import org.junit.jupiter.api.Test;import java.math.BigDecimal;import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
class ContractPortfolioRiskServiceTest {
 private final ContractPortfolioRiskService service=new ContractPortfolioRiskService();
 @Test void monitorsBalancedPortfolio(){var r=service.assess(req("0.7","500",List.of(cp("A","500",false,false,false),cp("B","500",false,false,false))));assertThat(r.decision()).isEqualTo(ContractPortfolioRiskService.Decision.MONITOR);}
 @Test void reviewsConcentrationAndRenewal(){var r=service.assess(req("0.6","500",List.of(cp("A","800",false,false,true),cp("B","200",false,false,false))));assertThat(r.decision()).isEqualTo(ContractPortfolioRiskService.Decision.REVIEW);assertThat(r.warnings()).hasSize(2);}
 @Test void blocksSanctionsAndMismatchedExposure(){var r=service.assess(new ContractPortfolioRiskService.Request("P",new BigDecimal("900"),new BigDecimal("0.8"),new BigDecimal("500"),List.of(cp("A","1000",true,true,false))));assertThat(r.decision()).isEqualTo(ContractPortfolioRiskService.Decision.BLOCKED);assertThat(r.blockers()).hasSize(2);}
 private ContractPortfolioRiskService.Request req(String rate,String high,List<ContractPortfolioRiskService.Counterparty> c){return new ContractPortfolioRiskService.Request("P",new BigDecimal("1000"),new BigDecimal(rate),new BigDecimal(high),c);}
 private ContractPortfolioRiskService.Counterparty cp(String code,String amount,boolean high,boolean sanction,boolean renew){return new ContractPortfolioRiskService.Counterparty(code,new BigDecimal(amount),high,sanction,renew,renew,false);}
}
