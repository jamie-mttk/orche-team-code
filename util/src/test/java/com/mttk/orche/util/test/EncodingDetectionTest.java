package com.mttk.orche.util.test;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件编码检测测试类
 * 使用juniversalchardet库检测指定目录下所有文件的编码
 */
public class EncodingDetectionTest {

    /**
     * 检测单个文件的编码
     * 
     * @param file 要检测的文件
     * @return 检测到的编码名称，如果无法检测则返回"UTF-8"
     * @throws IOException
     */
    public static String detectEncoding(File file) throws IOException {
        byte[] buf = new byte[4096];
        UniversalDetector detector = new UniversalDetector(null);

        try (FileInputStream fis = new FileInputStream(file)) {
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
        }

        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();

        return encoding;
    }

    /**
     * 递归获取目录下所有文件
     * 
     * @param directory 目录
     * @param fileList  文件列表
     */
    private static void listAllFiles(File directory, List<File> fileList) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        listAllFiles(file, fileList);
                    } else {
                        fileList.add(file);
                    }
                }
            }
        } else if (directory.isFile()) {
            fileList.add(directory);
        }
    }

    /**
     * 测试指定目录下所有文件的编码
     * 
     * @param directoryPath 目录路径
     */
    public static void testDirectoryEncoding(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            System.err.println("错误: 目录不存在 - " + directoryPath);
            return;
        }

        System.out.println("开始检测目录: " + directoryPath);
        System.out.println("=".repeat(80));

        List<File> fileList = new ArrayList<>();
        listAllFiles(directory, fileList);

        if (fileList.isEmpty()) {
            System.out.println("目录中没有文件");
            return;
        }

        System.out.println("找到 " + fileList.size() + " 个文件\n");

        int successCount = 0;
        int failCount = 0;

        for (File file : fileList) {
            try {
                String encoding = detectEncoding(file);
                String relativePath = directory.toPath().relativize(file.toPath()).toString();
                System.out.printf("%-60s -> %s%n", relativePath, encoding);
                successCount++;
            } catch (Exception e) {
                System.err.printf("%-60s -> 检测失败: %s%n",
                        file.getName(), e.getMessage());
                failCount++;
            }
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("检测完成!");
        System.out.println("成功: " + successCount + " 个文件");
        if (failCount > 0) {
            System.out.println("失败: " + failCount + " 个文件");
        }
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        // 测试目录
        String testDirectory = "C:\\Users\\jamie\\Desktop\\bel";

        // 如果有命令行参数，使用命令行参数指定的目录
        if (args.length > 0) {
            testDirectory = args[0];
        }

        testDirectoryEncoding(testDirectory);
    }
}
