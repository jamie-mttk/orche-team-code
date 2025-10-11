package com.mttk.orche.agent.sqlQuery;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mttk.orche.addon.AdapterConfig;

public class SqlQueryGenUtil {
    public static Document genPrompt(AdapterConfig config,
            Document data) throws Exception {
        //
        StringBuilder sb = new StringBuilder(2048);
        sb.append(config.getString("basePrompt", Prompt.SYSTEM_PROMPT));
        try (Connection conn = SqlQueryUtil.getConnection(data)) {
            // 数据库基本信息
            genDatabaseInfo(conn, sb);
            // 表信息
            genTableInfo(conn, sb, config);
            //
            return new Document("systemPrompt", sb.toString());
        }
    }

    private static void genDatabaseInfo(Connection conn, StringBuilder sb) throws Exception {
        DatabaseMetaData metaData = conn.getMetaData();

        // 1. 数据库类型和版本信息
        sb.append("## 数据库信息\n\n");
        sb.append("数据库类型: ").append(metaData.getDatabaseProductName()).append("\n");
        sb.append("数据库版本: ").append(metaData.getDatabaseProductVersion()).append("\n");
        sb.append("驱动名称: ").append(metaData.getDriverName()).append("\n");
        sb.append("驱动版本: ").append(metaData.getDriverVersion()).append("\n");
        sb.append("支持的SQL关键字: ").append(metaData.getSQLKeywords()).append("\n");
        sb.append("字符串函数: ").append(metaData.getStringFunctions()).append("\n");
        sb.append("数值函数: ").append(metaData.getNumericFunctions()).append("\n");
        sb.append("系统函数: ").append(metaData.getSystemFunctions()).append("\n");
        sb.append("时间日期函数: ").append(metaData.getTimeDateFunctions()).append("\n");
        sb.append("\n\n");

    }

    private static void genTableInfo(Connection conn, StringBuilder sb, AdapterConfig config) throws Exception {
        //
        List<AdapterConfig> tables = config.getBeanList("tables");
        if (tables == null || tables.isEmpty()) {
            return;
        }
        //
        sb.append("## 表信息\n\n");
        //
        for (AdapterConfig tableConfig : tables) {
            genTableInfoSingle(conn, sb, tableConfig);
        }
    }

    private static void genTableInfoSingle(Connection conn, StringBuilder sb, AdapterConfig tableConfig)
            throws Exception {
        String tableName = tableConfig.getString("name");
        if (tableName == null || tableName.trim().isEmpty()) {
            return;
        }

        // 解析schema和表名
        String schema = null;
        String actualTableName = tableName;
        if (tableName.contains(".")) {
            String[] parts = tableName.split("\\.", 2);
            schema = parts[0];
            actualTableName = parts[1];
        }

        //
        sb.append("### ").append(tableName).append("\n\n");
        sb.append("注释: ").append(tableConfig.getString("remark", actualTableName)).append("\n");

        // 获取数据库元数据
        DatabaseMetaData metaData = conn.getMetaData();

        // 获取主键信息
        getPrimaryKeysInfo(metaData, schema, actualTableName, sb);

        // 获取外键信息
        getForeignKeysInfo(metaData, schema, actualTableName, sb);

        // 获取索引信息
        getIndexesInfo(metaData, schema, actualTableName, sb);

        // 获取字段信息
        sb.append("字段信息:\n");
        getTableColumnsInfo(metaData, schema, actualTableName, sb);
        sb.append("\n");

    }

    // 获取指定表的列信息

    private static void getTableColumnsInfo(DatabaseMetaData metaData, String schema, String actualTableName,
            StringBuilder sb) {
        try {
            // 获取表的列信息
            ResultSet columnsResult = metaData.getColumns(schema, schema, actualTableName, null);

            while (columnsResult.next()) {
                String columnName = columnsResult.getString("COLUMN_NAME");
                String dataType = columnsResult.getString("TYPE_NAME");
                int columnSize = columnsResult.getInt("COLUMN_SIZE");
                String nullable = columnsResult.getString("IS_NULLABLE");
                String columnDefault = columnsResult.getString("COLUMN_DEF");
                String columnComment = columnsResult.getString("REMARKS");

                sb.append("  - ").append(columnName).append(" (")
                        .append(dataType);

                if (columnSize > 0) {
                    sb.append("(").append(columnSize).append(")");
                }

                if ("NO".equals(nullable)) {
                    sb.append(" NOT NULL");
                }

                if (columnDefault != null) {
                    sb.append(" DEFAULT ").append(columnDefault);
                }

                if (columnComment != null && !columnComment.isEmpty()) {
                    sb.append(" COMMENT '").append(columnComment).append("'");
                }

                sb.append(")\n");
            }

        } catch (Exception e) {
            sb.append("获取列信息失败: ").append(e.getMessage()).append("\n");
        }
    }

    // 获取指定表的主键信息
    private static void getPrimaryKeysInfo(DatabaseMetaData metaData, String schema, String actualTableName,
            StringBuilder sb) {
        List<String> primaryKeys = new ArrayList<>();

        try {
            ResultSet pkResult = metaData.getPrimaryKeys(schema, schema, actualTableName);

            while (pkResult.next()) {
                primaryKeys.add(pkResult.getString("COLUMN_NAME"));
            }

            if (!primaryKeys.isEmpty()) {
                sb.append("主键: ").append(String.join(", ", primaryKeys)).append("\n");
            }

        } catch (Exception e) {
            // 忽略错误
        }
    }

    // 获取指定表的外键信息

    private static void getForeignKeysInfo(DatabaseMetaData metaData, String schema, String actualTableName,
            StringBuilder sb) {
        boolean hasForeignKeys = false;

        try {
            ResultSet fkResult = metaData.getImportedKeys(schema, schema, actualTableName);

            while (fkResult.next()) {
                if (!hasForeignKeys) {
                    sb.append("外键关系:\n");
                    hasForeignKeys = true;
                }

                String columnName = fkResult.getString("FKCOLUMN_NAME");

                // 获取被引用表的schema和表名
                String referencedSchema = fkResult.getString("PKTABLE_SCHEM");
                String referencedTableName = fkResult.getString("PKTABLE_NAME");
                String fullReferencedTableName = referencedSchema != null ? referencedSchema + "." + referencedTableName
                        : referencedTableName;

                String referencedColumnName = fkResult.getString("PKCOLUMN_NAME");
                String foreignKeyName = fkResult.getString("FK_NAME");

                String fullColumnName = schema != null ? schema + "." + columnName : columnName;
                sb.append("  - ").append(fullColumnName).append(" -> ")
                        .append(fullReferencedTableName)
                        .append("(").append(referencedColumnName).append(")");
                if (foreignKeyName != null) {
                    sb.append(" [约束名: ").append(foreignKeyName).append("]");
                }
                sb.append("\n");
            }

        } catch (Exception e) {
            // 忽略错误
        }
    }

    // 获取指定表的索引信息

    private static void getIndexesInfo(DatabaseMetaData metaData, String schema, String actualTableName,
            StringBuilder sb) {
        boolean hasIndexes = false;

        try {
            ResultSet indexResult = metaData.getIndexInfo(schema, schema, actualTableName, false, false);

            while (indexResult.next()) {
                if (!hasIndexes) {
                    sb.append("索引信息:\n");
                    hasIndexes = true;
                }

                String indexName = indexResult.getString("INDEX_NAME");
                String columnName = indexResult.getString("COLUMN_NAME");
                boolean unique = !indexResult.getBoolean("NON_UNIQUE");

                sb.append("  - ").append(indexName).append(" (")
                        .append(columnName).append(")");
                if (unique) {
                    sb.append(" 唯一");
                }
                sb.append("\n");
            }

        } catch (Exception e) {
            // 忽略错误
        }
    }
}
