package com.mttk.orche.agent.osFileAgent;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.service.AgentFileService;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.util.FileHelper;

@AgentTemplateFlag(key = "_os-file-agent", name = "操作系统文件管理", description = """
                对本机文件系统的文件进行操作.
                1. 列表  读取目录下(可选子目录)文件
                2. 写入  把本地文件写入到上下文文件系统,返回写入的文件名(可能和本地文件名不同)
                3. 读取  从上下文文件系统读取文件写入到本地,返回本地文件完整路径
        """, callDefineClass = OsFileCallDefine.class, i18n = "/com/mttk/api/addon/basic/i18n")
// @Control(key = "rootPath", label = "根目录", description =
// "如果设置了,所有读写只会在此目录下进行", size = 1)
public class OsFileAgent extends AbstractAgent {
    // private static final Logger logger =
    // LoggerFactory.getLogger(EmailSendAgent.class);

    @Override
    protected String executeInternal(AgentParam para, String transactionId) throws Exception {
        String operation = para.getRequest().getStringMandatory("operation");

        switch (operation) {
            case "list":
                return handleList(para);
            case "writeContextFile":
                return handleWriteContextFile(para);
            case "readContextFile":
                return handleReadContextFile(para);
            default:
                throw new IllegalArgumentException("不支持的操作类型: " + operation);
        }
    }

    /**
     * 列出目录下的文件
     */
    private String handleList(AgentParam para) throws Exception {
        String pathList = para.getRequest().getStringMandatory("pathList");
        boolean includeSubPath = para.getRequest().getBoolean("includeSubPath", false);
        String filePattern = para.getRequest().getString("filePattern");

        Path dirPath = Paths.get(pathList);
        if (!Files.exists(dirPath)) {
            throw new IllegalArgumentException("目录不存在: " + pathList);
        }
        if (!Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("路径不是一个目录: " + pathList);
        }

        // 创建文件匹配器（如果提供了 filePattern）
        PathMatcher matcher = null;
        if (filePattern != null && !filePattern.trim().isEmpty()) {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);
        }

        List<String> fileList = new ArrayList<>();
        final PathMatcher finalMatcher = matcher;

        if (includeSubPath) {
            // 递归列出所有子目录下的文件
            try (Stream<Path> paths = Files.walk(dirPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> finalMatcher == null || finalMatcher.matches(path.getFileName()))
                        .forEach(path -> fileList.add(path.toAbsolutePath().toString()));
            }
        } else {
            // 只列出当前目录下的文件
            try (Stream<Path> paths = Files.list(dirPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> finalMatcher == null || finalMatcher.matches(path.getFileName()))
                        .forEach(path -> fileList.add(path.toAbsolutePath().toString()));
            }
        }

        // 返回文件列表
        StringBuilder result = new StringBuilder();
        for (String file : fileList) {
            result.append(file).append("\n");
        }
        return result.toString();
    }

    /**
     * 读取本地文件写入到上下文文件系统
     */
    private String handleWriteContextFile(AgentParam para) throws Exception {
        String fileLocal = para.getRequest().getStringMandatory("fileLocal");
        Path localPath = Paths.get(fileLocal);
        if (!Files.exists(localPath)) {
            throw new IllegalArgumentException("本地文件不存在: " + fileLocal);
        }
        if (!Files.isRegularFile(localPath)) {
            throw new IllegalArgumentException("路径不是一个文件: " + fileLocal);
        }

        // 读取本地文件内容
        byte[] content = FileHelper.readFile(localPath.toFile());
        // 获取文件名（不含路径）
        String filename = localPath.getFileName().toString();

        // 写入到上下文文件系统
        AgentFileService fileService = para.getContext().getServer().getService(AgentFileService.class);
        AgentFile uploadedFile = fileService.upload(para.getContext(), filename, "从本地文件上传: " + fileLocal, content);

        return uploadedFile.getFileName();
    }

    /**
     * 从上下文文件系统读取文件写入到本地
     */
    private String handleReadContextFile(AgentParam para) throws Exception {
        String fileLocal = para.getRequest().getStringMandatory("fileLocal");
        String fileNameContext = para.getRequest().getStringMandatory("fileNameContext");

        // 从上下文文件系统下载文件
        AgentFileService fileService = para.getContext().getServer().getService(AgentFileService.class);
        byte[] content = fileService.downloadData(para.getContext(), fileNameContext);

        // 写入到本地文件
        Path localPath = Paths.get(fileLocal);

        // 如果父目录不存在，创建父目录
        Path parentDir = localPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        FileHelper.writeFile(content, localPath.toFile(), true);
        //
        return localPath.toAbsolutePath().toString();
    }
}
