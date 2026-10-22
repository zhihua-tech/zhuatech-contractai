/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.contractai.workbench;

import static cn.zhuatech.contractai.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);
    keys(r, "asOf", "obligations");
    var asOf = date(text(r, "asOf", 10));
    Set<String> ids = new HashSet<>();
    List<Map<String, Object>> out = new ArrayList<>();
    int overdue = 0;
    for (var o : rows(r, "obligations", 1, 300)) {
      keys(o, "id", "dueDate", "remindDays", "completed");
      String id = id(o, "id");
      unique(ids, id);
      var due = date(text(o, "dueDate", 10));
      int remind = integer(o, "remindDays", 0, 365);
      boolean completed = flag(o, "completed");
      long delta = java.time.temporal.ChronoUnit.DAYS.between(asOf, due);
      String status =
          completed ? "COMPLETED" : delta < 0 ? "OVERDUE" : delta <= remind ? "DUE_SOON" : "OPEN";
      if (status.equals("OVERDUE")) overdue++;
      out.add(
          Map.of(
              "id",
              id,
              "dueDate",
              due.toString(),
              "reminderDate",
              due.minusDays(remind).toString(),
              "daysToDue",
              delta,
              "overdueDays",
              completed ? 0L : Math.max(0, -delta),
              "status",
              status));
    }
    out.sort(
        Comparator.<Map<String, Object>, String>comparing(o -> o.get("dueDate").toString())
            .thenComparing(o -> o.get("id").toString()));
    return Map.of("asOf", asOf.toString(), "overdueCount", overdue, "obligations", out);
  }
}
