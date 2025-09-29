package com.mttk.orche.agent.sqlQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.StringJoiner;

import com.mttk.orche.addon.AdapterConfig;

public class SqlQueryUtil {

    public static QueryResult executeQueryToCsv(String sql, AdapterConfig config) throws Exception {

        StringBuilder csvOutput = new StringBuilder();
        String header = "";
        int count = 0;
        //

        sql = cleanSqlPrefix(sql);

        //
        try (Connection conn = getConnection(config.toMap());
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            // 获取结果集元数据
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 1. 构建CSV标题行（使用字段注释）
            StringJoiner headerJoiner = new StringJoiner(",");
            for (int i = 1; i <= columnCount; i++) {
                String columnComment = getColumnComment(conn, metaData, i);
                headerJoiner.add(escapeCsv(columnComment));
            }
            header = headerJoiner.toString();
            csvOutput.append(header).append("\n");

            // 2. 构建数据行
            while (rs.next()) {
                StringJoiner rowJoiner = new StringJoiner(",");
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    rowJoiner.add(escapeCsv(value != null ? value.toString() : ""));
                }
                csvOutput.append(rowJoiner).append("\n");
                count++;
            }

        } catch (SQLException e) {
            // e.printStackTrace();
            throw new RuntimeException("数据库操作失败: " + e.getMessage(), e);
        }
        return new QueryResult(csvOutput.toString(), header, count);
    }

    public static Connection getConnection(Map<String, Object> data) throws Exception {

        // 检查四个必需的数据库连接参数
        String jdbcDriver = (String) data.get("jdbcDriver");
        String jdbcUrl = (String) data.get("jdbcUrl");
        String jdbcUser = (String) data.get("jdbcUser");
        String jdbcPassword = (String) data.get("jdbcPassword");
        Boolean jdbcReadonly = (Boolean) data.getOrDefault("jdbcReadonly", true);
        if (jdbcDriver == null || jdbcDriver.trim().isEmpty()) {
            throw new IllegalArgumentException("jdbcDriver参数不能为空");
        }
        if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("jdbcUrl参数不能为空");
        }
        if (jdbcUser == null || jdbcUser.trim().isEmpty()) {
            throw new IllegalArgumentException("jdbcUser参数不能为空");
        }
        if (jdbcPassword == null) {
            throw new IllegalArgumentException("jdbcPassword参数不能为空");
        }

        //
        Class.forName(jdbcDriver);
        Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
        conn.setReadOnly(jdbcReadonly);
        return conn;
    }

    // 获取字段注释（无注释时返回字段名）
    private static String getColumnComment(Connection conn, ResultSetMetaData metaData, int columnIndex)
            throws SQLException {

        return metaData.getColumnLabel(columnIndex);
    }

    // CSV特殊字符转义
    private static String escapeCsv(String input) {
        if (input == null)
            return "";
        if (input.contains("\"") || input.contains(",") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }

    // 查询结果数据类
    public static class QueryResult {
        private String csv;
        private String header;
        private int count;

        public QueryResult(String csv, String header, int count) {
            this.csv = csv;
            this.header = header;
            this.count = count;
        }

        public String getCsv() {
            return csv;
        }

        public String getHeader() {
            return header;
        }

        public int getCount() {
            return count;
        }
    }

    // 生成SQL有时候带```sql和```,需要去掉
    public static String cleanSqlPrefix(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }

        // 处理单行格式 (```json{...}```)
        if (trimmed.startsWith("```sql") && trimmed.endsWith("```")) {
            return trimmed.substring(7, trimmed.length() - 3).trim();
        }

        return trimmed; // 返回标准 JSON
    }
}
