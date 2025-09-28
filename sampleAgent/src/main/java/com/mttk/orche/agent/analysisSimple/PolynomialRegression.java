package com.mttk.orche.agent.analysisSimple;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 多项式回归分析工具类
 */
public class PolynomialRegression {

    /**
     * 执行多项式回归分析
     *
     * @param input           CSV输入流
     * @param output          结果输出流
     * @param independentVars 自变量列名列表
     * @param dependentVar    因变量列名
     * @param config          配置参数（可空，使用默认值）
     * @return 包含回归结果的对象
     * @throws IOException    当I/O错误发生
     * @throws ParseException 当日期解析失败
     */
    public static Result analyze(InputStream input, OutputStream output,
            List<String> independentVars, String dependentVar, Config config)
            throws IOException, ParseException {

        // 处理默认配置
        if (config == null) {
            config = new Config();
        }

        // 读取CSV数据
        CSVParser parser = CSVParser.parse(new InputStreamReader(input),
                CSVFormat.DEFAULT.withFirstRecordAsHeader());
        List<CSVRecord> records = parser.getRecords();
        List<String> headers = new ArrayList<>(parser.getHeaderNames());

        // 验证列名存在性
        validateColumns(headers, independentVars, dependentVar);

        // 转换数据为数值形式
        DataMatrix data = convertToDataMatrix(records, headers, independentVars, dependentVar, config);

        // 生成多项式特征
        double[][] features = generatePolynomialFeatures(
                data.getIndependentValues(), config.getPolynomialDegree());

        // 执行线性回归
        OLSMultipleLinearRegression regression = performRegression(
                features, data.getDependentValues());

        // 计算拟合值
        double[] fittedValues = calculateFittedValues(features, regression.estimateRegressionParameters());

        // 创建结果对象
        Result result = buildResult(regression, independentVars, config.getPolynomialDegree());
        result.setFittedColumnName(dependentVar + "(拟合值)");

        // 写入结果CSV
        writeResultCSV(records, headers, fittedValues, result.getFittedColumnName(), output);

        return result;
    }

    // 验证列名是否存在
    private static void validateColumns(List<String> headers,
            List<String> independentVars,
            String dependentVar) {
        if (!headers.contains(dependentVar)) {
            throw new IllegalArgumentException("因变量列不存在: " + dependentVar);
        }
        for (String var : independentVars) {
            if (!headers.contains(var)) {
                throw new IllegalArgumentException("自变量列不存在: " + var);
            }
        }
    }

    // 将CSV记录转换为数值矩阵
    private static DataMatrix convertToDataMatrix(List<CSVRecord> records,
            List<String> headers,
            List<String> independentVars,
            String dependentVar,
            Config config) throws ParseException {

        int rowCount = records.size();
        int colCount = independentVars.size();

        double[][] independentValues = new double[rowCount][colCount];
        double[] dependentValues = new double[rowCount];

        SimpleDateFormat sdf = new SimpleDateFormat(config.getDateFormat());

        for (int i = 0; i < rowCount; i++) {
            CSVRecord record = records.get(i);

            // 处理因变量
            dependentValues[i] = parseDouble(record.get(dependentVar));

            // 处理自变量
            for (int j = 0; j < colCount; j++) {
                String value = record.get(independentVars.get(j));
                independentValues[i][j] = parseNumericOrDate(value, sdf);

            }
        }

        return new DataMatrix(independentValues, dependentValues);
    }

    // 解析数值或日期
    private static double parseNumericOrDate(String value, SimpleDateFormat sdf) throws ParseException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            try {
                // 946656000000 = 2000/01/01
                Double v = (sdf.parse(value).getTime() - 946656000000l) / (1000.0 * 60 * 60 * 24); // 转换为天数
                return v.intValue();
            } catch (ParseException ex) {
                throw new ParseException("无法解析值: " + value + " 为数值或日期(" + sdf.toPattern() + ")", 0);
            }
        }
    }

    // 解析纯数值
    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无法解析数值: " + value);
        }
    }

    // 生成多项式特征
    private static double[][] generatePolynomialFeatures(double[][] baseFeatures, int degree) {
        int rows = baseFeatures.length;
        int baseCols = baseFeatures[0].length;
        int totalCols = baseCols * degree;

        double[][] polynomialFeatures = new double[rows][totalCols];

        for (int i = 0; i < rows; i++) {
            int colIndex = 0;
            for (int j = 0; j < baseCols; j++) {
                double baseValue = baseFeatures[i][j];
                for (int d = 1; d <= degree; d++) {
                    polynomialFeatures[i][colIndex++] = Math.pow(baseValue, d);
                }
            }
        }
        return polynomialFeatures;
    }

    // 执行回归分析
    private static OLSMultipleLinearRegression performRegression(double[][] features, double[] target) {
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.setNoIntercept(true);
        regression.newSampleData(target, features);
        return regression;
    }

    // 计算拟合值
    private static double[] calculateFittedValues(double[][] features, double[] coefficients) {
        int numRows = features.length;
        if (numRows == 0) {
            return new double[0];
        }
        int numFeatures = features[0].length;
        int numCoefficients = coefficients.length;

        // 自动检测是否有截距项
        boolean hasIntercept = (numCoefficients == numFeatures + 1);

        double[] fitted = new double[numRows];
        for (int i = 0; i < numRows; i++) {
            double prediction = 0.0;
            int coefIndex = 0;

            // 如果有截距项，先加上截距
            if (hasIntercept) {
                prediction += coefficients[coefIndex++];
            }

            // 添加特征项
            for (int j = 0; j < numFeatures; j++) {
                prediction += coefficients[coefIndex++] * features[i][j];
            }
            fitted[i] = prediction;
        }
        return fitted;
    }

    // 构建结果对象
    private static Result buildResult(OLSMultipleLinearRegression regression,
            List<String> independentVars,
            int degree) {
        Result result = new Result();

        // 计算评估指标
        result.setRSquared(regression.calculateRSquared());
        result.setAdjustedRSquared(regression.calculateAdjustedRSquared());

        // 构建LaTeX公式
        result.setFormulaLatex(buildLatexFormula(
                regression.estimateRegressionParameters(),
                independentVars,
                degree));

        return result;
    }

    // 构建LaTeX公式（修复后）
    private static String buildLatexFormula(double[] coefficients,
            List<String> independentVars,
            int degree) {
        int totalFeatures = independentVars.size() * degree;
        boolean hasIntercept = (coefficients.length == totalFeatures + 1);

        StringBuilder formula = new StringBuilder("y = ");
        int coefIndex = 0;

        // 处理截距项
        if (hasIntercept) {
            formula.append(String.format(Locale.US, "%.4f", coefficients[coefIndex++]));
        } else {
            formula.append("0"); // 无截距项时从0开始
        }

        // 处理特征项
        for (int varIndex = 0; varIndex < independentVars.size(); varIndex++) {
            String var = independentVars.get(varIndex);
            for (int d = 1; d <= degree; d++) {
                double coef = coefficients[coefIndex++];
                if (Math.abs(coef) > 1e-4) {
                    formula.append(coef >= 0 ? " + " : " - ");
                    formula.append(String.format(Locale.US, "%.4f", Math.abs(coef)));
                    formula.append(var);
                    if (d > 1) {
                        formula.append("^{").append(d).append("}");
                    }
                }
            }
        }
        return formula.toString();
    }

    // 写入结果CSV
    private static void writeResultCSV(List<CSVRecord> records,
            List<String> headers,
            double[] fittedValues,
            String fittedColumnName,
            OutputStream output) throws IOException {

        CSVPrinter printer = new CSVPrinter(
                new OutputStreamWriter(output),
                CSVFormat.DEFAULT.withHeader(appendHeader(headers, fittedColumnName)));

        for (int i = 0; i < records.size(); i++) {
            List<String> record = new ArrayList<>();
            for (String header : headers) {
                record.add(records.get(i).get(header));
            }
            record.add(String.format(Locale.US, "%.4f", fittedValues[i]));
            printer.printRecord(record);
        }

        printer.flush();
    }

    // 添加新列头
    private static String[] appendHeader(List<String> headers, String newHeader) {
        List<String> newHeaders = new ArrayList<>(headers);
        newHeaders.add(newHeader);
        return newHeaders.toArray(new String[0]);
    }

    /**
     * 多项式回归配置参数
     */
    public static class Config {
        /**
         * 多项式阶数配置（默认值：4）
         * 取值范围：正整数，建议1-6之间避免过拟合
         */
        private int polynomialDegree = 4;

        /**
         * 日期格式配置（默认值："yyyy-MM-dd"）
         * 支持Java SimpleDateFormat格式，如：
         * - "yyyy-MM-dd" (默认)
         * - "dd/MM/yyyy"
         * - "MM/dd/yyyy HH:mm:ss"
         */
        private String dateFormat = "yyyy-MM-dd";

        // 默认构造函数
        public Config() {
        }

        // 带参数的构造函数
        public Config(int polynomialDegree, String dateFormat) {
            if (polynomialDegree < 1) {
                throw new IllegalArgumentException("多项式阶数必须大于0");
            }
            this.polynomialDegree = polynomialDegree;
            this.dateFormat = dateFormat;
        }

        // Getter和Setter
        public int getPolynomialDegree() {
            return polynomialDegree;
        }

        public void setPolynomialDegree(int polynomialDegree) {
            if (polynomialDegree < 1) {
                throw new IllegalArgumentException("多项式阶数必须大于0");
            }
            this.polynomialDegree = polynomialDegree;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }
    }

    /**
     * 回归分析结果对象
     */
    public static class Result {
        private String formulaLatex; // LaTeX格式的回归公式
        private double rSquared; // 决定系数R²
        private double adjustedRSquared; // 调整后的决定系数
        private String fittedColumnName; // 拟合值列名

        // Getter方法
        public String getFormulaLatex() {
            return formulaLatex;
        }

        // 内部使用的Setter
        private void setFormulaLatex(String formulaLatex) {
            this.formulaLatex = formulaLatex;
        }

        public double getRSquared() {
            return rSquared;
        }

        private void setRSquared(double rSquared) {
            this.rSquared = rSquared;
        }

        public double getAdjustedRSquared() {
            return adjustedRSquared;
        }

        private void setAdjustedRSquared(double adjustedRSquared) {
            this.adjustedRSquared = adjustedRSquared;
        }

        public String getFittedColumnName() {
            return fittedColumnName;
        }

        private void setFittedColumnName(String fittedColumnName) {
            this.fittedColumnName = fittedColumnName;
        }
    }

    // 数据转换类
    private static class DataMatrix {
        private final double[][] independentValues;
        private final double[] dependentValues;

        public DataMatrix(double[][] independentValues, double[] dependentValues) {
            this.independentValues = independentValues;
            this.dependentValues = dependentValues;
        }

        public double[][] getIndependentValues() {
            return independentValues;
        }

        public double[] getDependentValues() {
            return dependentValues;
        }
    }
}
