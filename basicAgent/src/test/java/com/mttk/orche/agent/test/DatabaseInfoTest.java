package com.mttk.orche.agent.test;

import com.mttk.orche.util.PatternUtil;
import com.mttk.orche.util.StringUtil;

import org.bson.Document;

import java.sql.*;
import java.util.*;

/**
 * 数据库信息获取测试类
 * 用于连接数据库并获取表信息，为大模型生成SQL提供数据库结构信息
 */
public class DatabaseInfoTest {

    // 数据库连接配置
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/ai_demo?serverTimezone=GMT%2B8&closingTimeout=0&useSSL=false";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123456";

    private String simpleTransform(String pattern) {
        if (StringUtil.isEmpty(pattern)) {
            return null;
        }
        // 替换所有的*为%
        return pattern.replace("*", "%");
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    /**
     * 获取数据库中的所有表信息
     * 
     * @param conn    数据库连接
     * @param pattern 模式匹配字符串，支持通配符 * 和 ?
     * @param limit   限制返回的列表个数，0表示不限制
     * @return TableListResult 包含是否完整列出和表列表信息
     */
    public TableListResult listTables(Connection conn, String pattern, int limit) throws SQLException {

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
        schemaPattern = null;
        tablePattern = "data*";
        // 将应用层通配符转换为数据库层通配符
        schemaPattern = simpleTransform(schemaPattern);
        tablePattern = simpleTransform(tablePattern);

        System.out.println("schemaPattern: " + schemaPattern);
        System.out.println("tablePattern: " + tablePattern);

        // 获取所有表 getTablesCount
        ResultSet tablesResult = metaData.getTables(null, schemaPattern, tablePattern,
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
     * 检查字符串是否匹配模式
     * 
     * @param str     要检查的字符串
     * @param pattern 模式字符串，支持 * 和 ? 通配符
     * @return 是否匹配
     */
    private boolean isPatternMatch(String str, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return true;
        }
        if (str == null) {
            return false;
        }

        // 将 SQL 通配符转换为正则表达式
        String regex = pattern.replace("*", ".*").replace("?", ".");
        return str.matches(regex);
    }

    /**
     * 演示为什么需要两次匹配的测试方法
     */
    public void demonstrateDoubleMatching() {
        System.out.println("=== 演示为什么需要两次匹配 ===");

        // 模拟数据库返回的结果（数据库层面可能不完全支持 * 通配符）
        String[] databaseResults = { "test_table1", "test_table2", "other_table", "test" };
        String pattern = "test*";

        System.out.println("模式：" + pattern);
        System.out.println("数据库返回的表：");
        for (String table : databaseResults) {
            System.out.println("  " + table);
        }

        System.out.println("\n应用层匹配结果：");
        for (String table : databaseResults) {
            boolean matches = isPatternMatch(table, pattern);
            System.out.println("  " + table + " -> " + (matches ? "匹配" : "不匹配"));
        }

        System.out.println("\n说明：");
        System.out.println("1. 数据库层面：可能不支持 * 通配符，返回所有表");
        System.out.println("2. 应用层面：精确匹配 test* 模式，过滤出 test_table1, test_table2");
    }

    /**
     * 获取指定表的列信息
     */
    public String getTableColumnsInfo(Connection conn, String tableName) {
        StringBuilder columnsInfo = new StringBuilder();

        try {
            DatabaseMetaData metaData = conn.getMetaData();

            // 解析schema和表名
            String schema = null;
            String actualTableName = tableName;
            if (tableName.contains(".")) {
                String[] parts = tableName.split("\\.", 2);
                schema = parts[0];
                actualTableName = parts[1];
            }
            //

            // 获取表的列信息
            ResultSet columnsResult = metaData.getColumns(schema, schema, actualTableName, null);

            while (columnsResult.next()) {
                String columnName = columnsResult.getString("COLUMN_NAME");
                String dataType = columnsResult.getString("TYPE_NAME");
                int columnSize = columnsResult.getInt("COLUMN_SIZE");
                String nullable = columnsResult.getString("IS_NULLABLE");
                String columnDefault = columnsResult.getString("COLUMN_DEF");
                String columnComment = columnsResult.getString("REMARKS");

                columnsInfo.append("  - ").append(columnName)
                        .append(" (").append(dataType);

                if (columnSize > 0) {
                    columnsInfo.append("(").append(columnSize).append(")");
                }

                if ("NO".equals(nullable)) {
                    columnsInfo.append(" NOT NULL");
                }

                if (columnDefault != null) {
                    columnsInfo.append(" DEFAULT ").append(columnDefault);
                }

                if (columnComment != null && !columnComment.isEmpty()) {
                    columnsInfo.append(" COMMENT '").append(columnComment).append("'");
                }

                columnsInfo.append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return columnsInfo.toString();
    }

    /**
     * 获取指定表的主键信息
     */
    public String getPrimaryKeysInfo(Connection conn, String tableName) {
        List<String> primaryKeys = new ArrayList<>();

        try {
            DatabaseMetaData metaData = conn.getMetaData();

            // 解析schema和表名
            String schema = null;
            String actualTableName = tableName;
            if (tableName.contains(".")) {
                String[] parts = tableName.split("\\.", 2);
                schema = parts[0];
                actualTableName = parts[1];
            }

            ResultSet pkResult = metaData.getPrimaryKeys(schema, schema, actualTableName);

            while (pkResult.next()) {
                primaryKeys.add(pkResult.getString("COLUMN_NAME"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return primaryKeys.isEmpty() ? "" : "主键：" + String.join(", ", primaryKeys) + "\n";
    }

    /**
     * 获取指定表的外键信息
     */
    public String getForeignKeysInfo(Connection conn, String tableName) {
        StringBuilder foreignKeysInfo = new StringBuilder();

        try {
            DatabaseMetaData metaData = conn.getMetaData();

            // 解析schema和表名
            String schema = null;
            String actualTableName = tableName;
            if (tableName.contains(".")) {
                String[] parts = tableName.split("\\.", 2);
                schema = parts[0];
                actualTableName = parts[1];
            }

            ResultSet fkResult = metaData.getImportedKeys(schema, schema, actualTableName);

            while (fkResult.next()) {
                String columnName = fkResult.getString("FKCOLUMN_NAME");

                // 获取被引用表的schema和表名
                String referencedSchema = fkResult.getString("PKTABLE_SCHEM");
                String referencedTableName = fkResult.getString("PKTABLE_NAME");
                String fullReferencedTableName = referencedSchema != null ? referencedSchema + "." + referencedTableName
                        : referencedTableName;

                String referencedColumnName = fkResult.getString("PKCOLUMN_NAME");
                String foreignKeyName = fkResult.getString("FK_NAME");

                String fullColumnName = schema != null ? schema + "." + columnName : columnName;
                foreignKeysInfo.append("  - ").append(fullColumnName)
                        .append(" -> ").append(fullReferencedTableName)
                        .append("(").append(referencedColumnName).append(")")
                        .append(foreignKeyName != null ? " [约束名: " + foreignKeyName + "]" : "")
                        .append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return foreignKeysInfo.length() > 0 ? "外键关系：\n" + foreignKeysInfo.toString() : "";
    }

    /**
     * 获取指定表的索引信息
     */
    public String getIndexesInfo(Connection conn, String tableName) {
        StringBuilder indexesInfo = new StringBuilder();

        try {
            DatabaseMetaData metaData = conn.getMetaData();

            // 解析schema和表名
            String schema = null;
            String actualTableName = tableName;
            if (tableName.contains(".")) {
                String[] parts = tableName.split("\\.", 2);
                schema = parts[0];
                actualTableName = parts[1];
            }

            ResultSet indexResult = metaData.getIndexInfo(schema, schema, actualTableName, false, false);

            while (indexResult.next()) {
                String indexName = indexResult.getString("INDEX_NAME");
                String columnName = indexResult.getString("COLUMN_NAME");
                boolean unique = !indexResult.getBoolean("NON_UNIQUE");

                indexesInfo.append("  - ").append(indexName)
                        .append(" (").append(columnName).append(")")
                        .append(unique ? " [唯一]" : "")
                        .append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return indexesInfo.length() > 0 ? "索引信息：\n" + indexesInfo.toString() : "";
    }

    /**
     * 生成数据库结构描述文本，用于大模型理解数据库结构
     */
    public String generateDatabaseDescription(Connection conn) {
        StringBuilder description = new StringBuilder();

        try {
            // 1. 数据库类型和版本信息
            // description.append("=== 数据库信息 ===\n");
            // description.append("数据库类型：").append(metaData.getDatabaseProductName()).append("\n");
            // description.append("数据库版本：").append(metaData.getDatabaseProductVersion()).append("\n");
            // description.append("驱动名称：").append(metaData.getDriverName()).append("\n");
            // description.append("驱动版本：").append(metaData.getDriverVersion()).append("\n");
            // description.append("数据库URL：").append(metaData.getURL()).append("\n");
            // description.append("用户名：").append(metaData.getUserName()).append("\n");
            // description.append("支持的SQL关键字：").append(metaData.getSQLKeywords()).append("\n");
            // description.append("字符串函数：").append(metaData.getStringFunctions()).append("\n");
            // description.append("数值函数：").append(metaData.getNumericFunctions()).append("\n");
            // description.append("系统函数：").append(metaData.getSystemFunctions()).append("\n");
            // description.append("时间日期函数：").append(metaData.getTimeDateFunctions()).append("\n");
            // description.append("\n");

            // // 2. 数据库约束和限制信息
            // description.append("=== 数据库限制信息 ===\n");
            // description.append("最大表名长度：").append(metaData.getMaxTableNameLength()).append("\n");
            // description.append("最大列名长度：").append(metaData.getMaxColumnNameLength()).append("\n");
            // description.append("最大列数：").append(metaData.getMaxColumnsInTable()).append("\n");
            // description.append("最大索引数：").append(metaData.getMaxIndexLength()).append("\n");
            // description.append("最大SQL语句长度：").append(metaData.getMaxStatementLength()).append("\n");
            // description.append("是否支持外键：是\n");
            // description.append("是否支持存储过程：").append(metaData.supportsStoredProcedures() ?
            // "是" : "否").append("\n");
            // description.append("是否支持事务：").append(metaData.supportsTransactions() ? "是" :
            // "否").append("\n");
            // description.append("是否支持批量更新：").append(metaData.supportsBatchUpdates() ? "是"
            // : "否").append("\n");
            // description.append("\n");

            // 3. 表结构信息
            description.append("=== 表结构信息 ===\n");
            TableListResult tableResult = listTables(conn, "test.c*", 10);

            for (Document table : tableResult.getTables()) {
                description.append("表名：").append(table.getString("tableName")).append("\n");
                String remark = table.getString("remark");
                if (remark != null && !remark.isEmpty()) {
                    description.append("表注释：").append(remark).append("\n");
                }
            }

            // // 获取主键信息
            // List<String> primaryKeys = getPrimaryKeys(table.getTableName());
            // if (!primaryKeys.isEmpty()) {
            // description.append("主键：").append(String.join(", ",
            // primaryKeys)).append("\n");
            // }

            // // 获取外键信息
            // List<ForeignKeyInfo> foreignKeys = getForeignKeys(table.getTableName());
            // if (!foreignKeys.isEmpty()) {
            // description.append("外键关系：\n");
            // for (ForeignKeyInfo fk : foreignKeys) {
            // description.append(" - ").append(fk.getColumnName())
            // .append(" -> ").append(fk.getReferencedTableName())
            // .append("(").append(fk.getReferencedColumnName()).append(")\n");
            // }
            // }

            // // 获取索引信息
            // List<IndexInfo> indexes = getIndexes(table.getTableName());
            // if (!indexes.isEmpty()) {
            // description.append("索引信息：\n");
            // for (IndexInfo index : indexes) {
            // description.append(" - ").append(index.getIndexName())
            // .append(" (").append(index.getColumnName()).append(")")
            // .append(index.isUnique() ? " [唯一]" : "").append("\n");
            // }
            // }

            // description.append("字段信息：\n");
            // for (ColumnInfo column : table.getColumns()) {
            // description.append(" - ").append(column.getColumnName())
            // .append(" (").append(column.getDataType());

            // if (column.getColumnSize() > 0) {
            // description.append("(").append(column.getColumnSize()).append(")");
            // }

            // if ("NO".equals(column.getNullable())) {
            // description.append(" NOT NULL");
            // }

            // if (column.getColumnDefault() != null) {
            // description.append(" DEFAULT ").append(column.getColumnDefault());
            // }

            // if (column.getColumnComment() != null &&
            // !column.getColumnComment().isEmpty()) {
            // description.append(" COMMENT
            // '").append(column.getColumnComment()).append("'");
            // }

            // description.append("\n");
            // }
            // description.append("\n");
            // }

        } catch (Exception e) {
            description.append("获取数据库信息时发生错误：").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        return description.toString();
    }

    /**
     * 获取指定表的完整描述信息
     */
    public String getTableDescription(Connection conn, String tableName) {
        StringBuilder description = new StringBuilder();

        try {
            // 解析schema和表名
            String schema = null;
            String actualTableName = tableName;
            if (tableName.contains(".")) {
                String[] parts = tableName.split("\\.", 2);
                schema = parts[0];
                actualTableName = parts[1];
            }

            // 获取表注释
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tablesResult = metaData.getTables(schema, schema, actualTableName, new String[] { "TABLE" });
            String tableComment = null;
            if (tablesResult.next()) {
                tableComment = tablesResult.getString("REMARKS");
            }

            description.append("表名：").append(tableName).append("\n");
            if (tableComment != null && !tableComment.isEmpty()) {
                description.append("表注释：").append(tableComment).append("\n");
            }

            // 获取主键信息
            description.append(getPrimaryKeysInfo(conn, tableName));

            // 获取外键信息
            description.append(getForeignKeysInfo(conn, tableName));

            // 获取索引信息
            description.append(getIndexesInfo(conn, tableName));

            // 获取字段信息
            description.append("字段信息：\n");
            description.append(getTableColumnsInfo(conn, tableName));

        } catch (Exception e) {
            description.append("获取表信息失败：").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        return description.toString();
    }

    /**
     * 测试方法：获取dms21.goods表的信息
     */
    public void testDataMonitorTable() {
        try (Connection conn = getConnection()) {
            System.out.println("=== 测试获取dms21.goods表信息 ===");
            String description = getTableDescription(conn, "dms21.goods");
            System.out.println(description);
        } catch (Exception e) {
            System.err.println("获取dms21.goods表信息失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试方法：生成完整的数据库描述
     */
    public void testGenerateDatabaseDescription() {
        System.out.println("=== 测试生成数据库描述 ===");

        try (Connection conn = getConnection()) {
            String description = generateDatabaseDescription(conn);
            System.out.println(description);
        } catch (Exception e) {
            System.err.println("生成数据库描述失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试方法：测试新的 listTables 方法
     */
    public void testListTables() {
        System.out.println("=== 测试 listTables 方法 ===");

        try (Connection conn = getConnection()) {
            // 测试1：获取所有表（限制5个）
            System.out.println("1. 获取所有表（限制5个）：");
            TableListResult result1 = listTables(conn, "ai_demo.data_monitor", 5);
            System.out.println("完整列出：" + result1.isFullListed());
            for (Document table : result1.getTables()) {
                System.out.println("  " + table.getString("schema") + "." + table.getString("tableName") + " - "
                        + table.getString("remark"));
            }
            System.out.println();

            // // 测试2：按表名模式匹配
            // System.out.println("2. 按表名模式匹配（sales*）：");
            // TableListResult result2 = listTables(conn, "sales*", 0);
            // System.out.println("完整列出：" + result2.isFullListed());
            // for (Document table : result2.getTables()) {
            // System.out.println(" " + table.getString("schema") + "." +
            // table.getString("tableName") + " - "
            // + table.getString("remark"));
            // }
            // System.out.println();

            // // 测试3：按 schema.table 模式匹配
            // System.out.println("3. 按 schema.table 模式匹配（ai_demo.*）：");
            // TableListResult result3 = listTables(conn, "ai_demo.*", 0);
            // System.out.println("完整列出：" + result3.isFullListed());
            // for (Document table : result3.getTables()) {
            // System.out.println(" " + table.getString("schema") + "." +
            // table.getString("tableName") + " - "
            // + table.getString("remark"));
            // }
            // System.out.println();

            // // 测试4：使用 ? 通配符
            // System.out.println("4. 使用 ? 通配符（sales_?rder）：");
            // TableListResult result4 = listTables(conn, "sales_?rder", 0);
            // System.out.println("完整列出：" + result4.isFullListed());
            // for (Document table : result4.getTables()) {
            // System.out.println(" " + table.getString("schema") + "." +
            // table.getString("tableName") + " - "
            // + table.getString("remark"));
            // }
            // System.out.println();

            // // 测试5：验证数据库层面和应用层面匹配的差异
            // System.out.println("5. 验证匹配差异（使用 * 通配符）：");
            // TableListResult result5 = listTables(conn, "test.*", 0);
            // System.out.println("完整列出：" + result5.isFullListed());
            // System.out.println("匹配到的表数量：" + result5.getTables().size());
            // for (Document table : result5.getTables()) {
            // System.out.println(" " + table.getString("schema") + "." +
            // table.getString("tableName") + " - "
            // + table.getString("remark"));
            // }

        } catch (Exception e) {
            System.err.println("测试 listTables 失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 主测试方法
     */
    public static void main(String[] args) {
        DatabaseInfoTest test = new DatabaseInfoTest();

        // 测试连接
        try {
            Connection conn = test.getConnection();
            conn.close();
            System.out.println("数据库连接成功！");
            System.out.println();
        } catch (Exception e) {
            System.err.println("数据库连接失败：" + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 测试获取data_monitor表信息
        // test.testDataMonitorTable();
        // System.out.println();

        // 测试获取指定表信息（可以传入任意表名）
        // try (Connection conn = test.getConnection()) {
        // String description = test.getTableDescription(conn, "your_table_name");
        // System.out.println(description);
        // }

        // 测试生成完整数据库描述
        // test.testGenerateDatabaseDescription();

        // 测试新的 listTables 方法
        test.testListTables();

        // 演示为什么需要两次匹配
        // test.demonstrateDoubleMatching();

        System.out.println("DONE");
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
            // 对于MySQL，TABLE_SCHEM通常为null，使用TABLE_CAT（数据库名）作为schema
            String schema = tablesResult.getString("TABLE_SCHEM");
            if (schema == null || schema.trim().isEmpty()) {
                // 如果TABLE_SCHEM为空，使用TABLE_CAT（数据库名）作为schema
                schema = tablesResult.getString("TABLE_CAT");
            }
            System.out.println("Schema: " + schema);
            return addTableInfo(schema, tablesResult.getString("TABLE_NAME"),
                    tablesResult.getString("REMARKS"));
        }

        public Document addTableInfo(String schema, String tableName, String remark) {
            Document tableInfo = new Document();
            tableInfo.append("schema", schema);
            tableInfo.append("tableName", tableName);
            tableInfo.append("remark", remark);
            tables.add(tableInfo);

            return tableInfo;
        }

    }

}
