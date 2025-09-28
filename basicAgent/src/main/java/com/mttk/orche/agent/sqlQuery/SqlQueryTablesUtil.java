package com.mttk.orche.agent.sqlQuery;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mttk.orche.addon.AdapterConfig;

import com.mttk.orche.util.StringUtil;

public class SqlQueryTablesUtil {
    public static Document getTables(AdapterConfig config, Document data) throws Exception {

        int limit = 20;
        try (Connection conn = SqlQueryUtil.getConnection(data)) {
            TableListResult tableListResult = listTables(conn, config.getString("tableFilter"), limit);
            List<Document> tables = data.getList("tables", Document.class);

            // 如果tables为null，初始化为空列表
            if (tables == null) {
                tables = new ArrayList<>();
                data.append("tables", tables);
            }

            // 将result.getTables的结果逐个加入到tables
            for (Document newTable : tableListResult.getTables()) {
                processTableInfo(tables, newTable);
            }
            //
            Document result = new Document();
            result.append("tables", tables);
            if (!tableListResult.isFullListed()) {
                result.append("__warning", "表列表未完整列出，只列出了" + tableListResult.getTablesCount() + "个表，可能存在部分表未列出");
            }

            return result;
        }
    }

    /**
     * 处理表信息，如果schema和tableName都相同则不加入，更新remark；否则加入
     * 
     * @param tables   现有的表列表
     * @param newTable 新的表信息
     */
    private static void processTableInfo(List<Document> tables, Document newTable) {
        String name = newTable.getString("name");
        // 查找是否已存在相同的schema和tableName
        boolean found = false;
        for (Document existingTable : tables) {
            if (java.util.Objects.equals(name, existingTable.getString("name"))) {
                //
                existingTable.put("description", newTable.getString("description"));
                found = true;
                break;
            }
        }
        // 如果没有找到相同的schema和tableName，则添加新表
        if (!found) {
            tables.add(newTable);
        }
    }

    /**
     * 获取数据库中的所有表信息
     * 
     * @param conn    数据库连接
     * @param pattern 模式匹配字符串，支持通配符 * 和 ?
     * @param limit   限制返回的列表个数，0表示不限制
     * @return TableListResult 包含是否完整列出和表列表信息
     */
    private static TableListResult listTables(Connection conn, String pattern, int limit) throws SQLException {

        DatabaseMetaData metaData = conn.getMetaData();

        // 解析 pattern 参数
        String schemaPattern = null;
        String tablePattern = null;

        if (pattern != null && !pattern.trim().isEmpty()) {
            String[] parts = pattern.split("\\.");
            if (parts.length > 2) {
                throw new IllegalArgumentException("模式字符串格式错误：不能包含多个点号");
            } else if (parts.length == 1) {
                // 只有表名，没有 schema
                tablePattern = parts[0];
            } else {
                // 包含 schema 和表名
                schemaPattern = parts[0];
                tablePattern = parts[1];
            }
        }
        // System.out.println("schemaPattern 1: " + schemaPattern);
        // System.out.println("tablePattern 2: " + tablePattern);
        // 将应用层通配符转换为数据库层通配符
        schemaPattern = simpleTransform(schemaPattern);
        tablePattern = simpleTransform(tablePattern);
        //
        // System.out.println("schemaPattern 1: " + schemaPattern);
        // System.out.println("tablePattern 2: " + tablePattern);

        // 获取所有表 getTablesCount
        ResultSet tablesResult = metaData.getTables(
                schemaPattern, schemaPattern, tablePattern,
                new String[] { "TABLE", "VIEW" });
        //
        TableListResult result = new TableListResult();

        while (tablesResult.next()) {
            if (limit > 0 && result.getTablesCount() >= limit) {
                result.setFullListed(false);
                break;
            }
            // 数据库层面已经进行了模式匹配，直接添加结果
            result.addTableInfo(tablesResult);
        }

        return result;
    }

    /**
     * 表列表查询结果类
     * 包含是否完整列出和表列表信息
     */
    public static class TableListResult {
        private boolean fullListed = true;
        private List<Document> tables = new ArrayList<>();

        public TableListResult() {
        }

        public boolean isFullListed() {
            return fullListed;
        }

        public void setFullListed(boolean fullListed) {
            this.fullListed = fullListed;
        }

        public int getTablesCount() {
            return tables.size();
        }

        public List<Document> getTables() {
            return tables;
        }

        public Document addTableInfo(ResultSet tablesResult) throws SQLException {
            String schema = tablesResult.getString("TABLE_SCHEM");
            if (StringUtil.isEmpty(schema)) {
                // 如果TABLE_SCHEM为空，使用TABLE_CAT（数据库名）作为schema
                schema = tablesResult.getString("TABLE_CAT");
            }
            String name = tablesResult.getString("TABLE_NAME");
            if (StringUtil.notEmpty(schema)) {
                name = schema + "." + name;
            }
            return addTableInfo(name, tablesResult.getString("REMARKS"));
        }

        public Document addTableInfo(String tableName, String remark) {
            Document tableInfo = new Document();
            tableInfo.append("name", tableName);
            tableInfo.append("description", remark);
            tables.add(tableInfo);
            return tableInfo;
        }

    }

    private static String simpleTransform(String pattern) {
        if (StringUtil.isEmpty(pattern)) {
            return null;
        }
        // 替换所有的*为%
        return pattern.replace("*", "%");
    }
}
