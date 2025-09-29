package com.mttk.orche.agentFile;

import com.mttk.orche.core.impl.AbstractService;
import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.service.AgentFileService;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mttk.orche.addon.agent.AgentContext;

import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.support.BsonUtil;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.FileHelper;
import com.mttk.orche.util.StringUtil;

@ServiceFlag(key = "agentFileService", name = "智能体文件管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class AgentFileServiceImpl extends AbstractService implements AgentFileService {
    // private Logger logger = LoggerFactory.getLogger(AgentFileServiceImpl.class);

    @Override
    public List<AgentFile> list(AgentContext context) throws Exception {
        //
        Document document = findExecuteDoc(context);
        if (document == null) {
            return null;
        }
        List<Document> files = document.get("files", List.class);
        List<AgentFile> agentFiles = new ArrayList<>();
        if (files != null) {
            for (Document fileDoc : files) {
                agentFiles.add(AgentFileImpl.fromDocument(fileDoc));
            }
        }
        return agentFiles;
    }

    @Override
    public AgentFile upload(AgentContext context, String fileName, String description, String content)
            throws Exception {
        AgentFile file = upload(context, fileName, description, content.getBytes("UTF-8"));

        //
        return file;

    }

    @Override
    public AgentFile upload(AgentContext context, String fileName, String description, byte[] data) throws Exception {
        if (data == null || data.length == 0) {
            throw new Exception("Data is empty for file:" + fileName);
        }
        // 移除文件名中不允许的特殊字符
        fileName = removeSpecialChars(fileName);
        // 检查文件是否已存在
        AgentFile existingFile = get(context, fileName);
        if (existingFile != null) {
            // 文件已存在,重新生成文件名
            fileName = fileName + "_" + StringUtil.getUUID();
        }
        // 创建新文件
        AgentFile agentFile = new AgentFileImpl(fileName, description, data.length);

        //
        uploadInternal(context, fileName, data);
        //
        context.sendResponse(new ChatResonseMessage("_file-upload", StringUtil.getUUID(),
                new ObjectMapper().writeValueAsString(agentFile)));
        //
        addFile(context, agentFile);
        //
        return agentFile;

    }

    @Override
    public AgentFile get(AgentContext context, String fileName) throws Exception {
        return list(context).stream()
                .filter(file -> file.getFileName().equals(fileName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String download(AgentContext context, String fileName) throws Exception {
        byte[] data = downloadData(context, fileName);
        if (data == null) {
            return null;
        }
        return new String(data, "UTF-8");
    }

    @Override
    public byte[] downloadData(AgentContext context, String fileName) throws Exception {

        //
        File file = realFile(context, fileName);
        return FileHelper.readFile(file);
    }

    // 以后从context的server中获取到根目录

    //
    private void uploadInternal(AgentContext context, String fileName, byte[] data) throws Exception {
        File file = realFile(context, fileName);
        FileHelper.createDir(file.getParentFile());
        FileHelper.writeFile(data, file, false);
    }

    private File realFile(AgentContext context, String fileName) throws Exception {
        String id = context.getSessionId();
        //

        // logger.info("server 1:" + context.getServer());
        // logger.info("server 2:" + ServerLocator.getServer());
        // logger.info("rootPath:" + ServerUtil.getPathData(ServerLocator.getServer()));
        //
        String rootPath = ServerUtil.getPathData(context.getServer()) + File.separator + "agentFile";
        //
        FileHelper.createDir(new File(rootPath));
        // id="jamie102";
        return new File(rootPath + File.separator + id + File.separator + fileName);
    }

    public Document addFile(AgentContext context, AgentFile file) throws Exception {
        @SuppressWarnings("unchecked")
        Document document = findExecuteDoc(context);
        if (document == null) {
            return null;
        }
        List<Document> files = document.get("files", List.class);
        files.add(BsonUtil.convertObjectToDocument(file));
        document.append("files", files);
        //
        context.getServer().getService(AgentExecuteService.class).update(document);
        //
        return document;
    }

    // 查找当前agentContext对应的执行文档,没找到返回null - 可能是不记录引起的
    private Document findExecuteDoc(AgentContext context) throws Exception {
        String sessionId = context.getSessionId();
        return context.getServer().getService(AgentExecuteService.class).load("sessionId",
                sessionId).orElse(null);
    }

    // 移除文件名中不允许的特殊字符
    private String removeSpecialChars(String input) {
        if (StringUtil.isEmpty(input)) {
            return "";
        }
        // 定义需要过滤的特殊字符集合
        String specialChars = " \"&$@=;:+?,\\{^}%~[]<>#|'\n\r*";
        Set<Character> specialCharsSet = new HashSet<>();
        for (char c : specialChars.toCharArray()) {
            // System.out.println("@@" + c);
            specialCharsSet.add(c);
        }
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (!specialCharsSet.contains(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }
}
