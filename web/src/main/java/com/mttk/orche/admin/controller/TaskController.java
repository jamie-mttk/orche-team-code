package com.mttk.orche.admin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.StringUtil;

@RestController
@RequestMapping(value = "/task")
public class TaskController extends PersistableControllerBase {
    @Override
    public Class getServiceClass() {
        return AgentExecuteService.class;
    }

    @Override
    public Bson parseCriteriaSingle(String key, Object value) throws Exception {

        if ("dateRange".equals(key)) {
            if (!(value instanceof List)) {
                return null;
            }
            List<String> list = (List<String>) value;
            if (list.size() != 2) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = sdf.parse(list.get(0));
            Date endDate = sdf.parse(list.get(1));

            return Filters.and(Filters.gte("_insertTime", startDate), Filters.lt("_insertTime", endDate));

        }
        return super.parseCriteriaSingle(key, value);
    }

    /**
     * 汇总任务执行状态统计
     * GET /task/summarize
     * 
     * @param startTime 开始时间，格式：yyyy/MM/dd HH:mm:ss，可以为空
     * @param endTime   结束时间，格式：yyyy/MM/dd HH:mm:ss，可以为空
     * @return 包含所有status值及其对应统计数量的Map，例如：{"failed": 12, "success": 10, "running":
     *         3}
     */
    @GetMapping("/summarize")
    public Map<String, Long> summarize(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) throws Exception {

        // 获取sysAgentExecute集合
        AgentExecuteService agentExecuteService = ServerUtil.getService(AgentExecuteService.class);
        MongoCollection<Document> collection = agentExecuteService.obtainCollection();

        // 构建查询条件
        Bson filter = buildTimeFilter(startTime, endTime);

        // 使用聚合查询统计所有status值的数量
        List<Document> pipeline = new ArrayList<>();

        // 如果有时间过滤条件，先添加match阶段
        boolean hasTimeFilter = StringUtil.notEmpty(startTime) || StringUtil.notEmpty(endTime);
        if (hasTimeFilter) {
            pipeline.add(new Document("$match", filter));
        }

        // 按status分组并计数
        pipeline.add(new Document("$group",
                new Document("_id", "$status")
                        .append("count", new Document("$sum", 1))));

        // 执行聚合查询
        List<Document> results = new ArrayList<>();
        collection.aggregate(pipeline).into(results);

        // 构建返回结果
        Map<String, Long> result = new HashMap<>();
        for (Document doc : results) {
            String status = doc.getString("_id");
            Object countObj = doc.get("count");
            Long count = null;
            if (countObj instanceof Integer) {
                count = ((Integer) countObj).longValue();
            } else if (countObj instanceof Long) {
                count = (Long) countObj;
            } else if (countObj instanceof Number) {
                count = ((Number) countObj).longValue();
            }

            if (status != null && count != null) {
                result.put(status, count);
            }
        }

        return result;
    }

    /**
     * 构建时间过滤条件
     * 
     * @param startTime 开始时间字符串
     * @param endTime   结束时间字符串
     * @return MongoDB查询条件
     * @throws Exception 时间格式解析异常
     */
    private Bson buildTimeFilter(String startTime, String endTime) throws Exception {
        Bson filter = new Document(); // 空条件，表示不过滤

        if (StringUtil.notEmpty(startTime) || StringUtil.notEmpty(endTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            if (StringUtil.notEmpty(startTime) && StringUtil.notEmpty(endTime)) {
                // 两个时间都有，使用范围查询
                Date start = sdf.parse(startTime);
                Date end = sdf.parse(endTime);
                filter = Filters.and(
                        Filters.gte("startTime", start),
                        Filters.lt("startTime", end));
            } else if (StringUtil.notEmpty(startTime)) {
                // 只有开始时间
                Date start = sdf.parse(startTime);
                filter = Filters.gte("startTime", start);
            } else if (StringUtil.notEmpty(endTime)) {
                // 只有结束时间
                Date end = sdf.parse(endTime);
                filter = Filters.lt("startTime", end);
            }
        }

        return filter;
    }

}
