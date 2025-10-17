package com.mttk.orche.agentExecute;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mttk.orche.addon.agent.Agent;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.service.AgentService;
import com.mttk.orche.service.AgentTemplateService;
import com.mttk.orche.service.support.AgentExecuteRequest;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.FileHelper;

class AgentSupport {

    /**
     * 私有类，用于封装Agent相关信息
     */
    private static class AgentInfo {
        private final Document agentDoc;
        private final Document agentTemplate;
        private final Agent agent;

        public AgentInfo(Document agentDoc, Document agentTemplate, Agent agent) {
            this.agentDoc = agentDoc;
            this.agentTemplate = agentTemplate;
            this.agent = agent;
        }

        public Document getAgentDoc() {
            return agentDoc;
        }

        public Document getAgentTemplate() {
            return agentTemplate;
        }

        public Agent getAgent() {
            return agent;
        }
    }

    /**
     * 获取Agent相关信息
     */
    private static AgentInfo getInfo(String agentId) throws Exception {
        // 1.获得Agent信息
        AgentService agentService = ServerUtil.getService(AgentService.class);
        Document agentDoc = agentService.load(agentId).orElse(null);
        if (agentDoc == null) {
            return null;
        }
        //

        // 2.得到实现类
        //
        //
        // String implCalss = agentTemplate.getString("implClass");
        // // 注意因为每个包都可能有自己的classloader,所以需要获取对应的包的classloader
        // ClassLoader cl =
        // ServerLocator.getServer().obtainClassLoader(agentTemplate.getString("_package_name"));
        String agentTemplateKey = agentDoc.getString("agentTemplate");
        AgentTemplateService agentTemplateService = ServerUtil.getService(AgentTemplateService.class);
        Document agentTemplate = agentTemplateService.load("key", agentTemplateKey).orElseThrow(
                () -> new RuntimeException("Agent template not found: " + agentDoc.getString("agentTemplate")));

        Agent agent = agentTemplateService.obtainAgent(agentTemplateKey);

        return new AgentInfo(agentDoc, agentTemplate, agent);
    }

    public static String execute(AgentContext context, String agentId, String toolName, String request)
            throws Exception {
        // 获取Agent信息
        AgentInfo agentInfo = getInfo(agentId);
        if (agentInfo == null) {
            throw new RuntimeException("No agent is found by " + agentId);
        }

        // 3.调用Agent config/request
        return agentInfo.getAgent().execute(context, context.createAdapterConfig(agentInfo.getAgentDoc()), request,
                toolName);
    }

    //
    public static List<String> getToolDefine(AgentContext context, String agentId) throws Exception {
        // 获取Agent信息
        AgentInfo agentInfo = getInfo(agentId);
        if (agentInfo == null) {
            return null;
        }
        //
        return agentInfo.getAgent().getToolDefine(context.createAdapterConfig(agentInfo.getAgentDoc()));
    }

    public static List<AgentFile> normalizeRequest(AgentContext context, AgentExecuteRequest request) throws Exception {
        List<AgentFile> agentFiles = new ArrayList<>();
        //
        String requestStr = request.getRequest();
        //

        // 0.设置是否修改标记
        boolean modified = false;

        // 1.解析requestStr到Document
        Document document = Document.parse(requestStr);

        // 2.逐个 key循环
        for (String key : document.keySet()) {
            // 3.如果key以_开头且值类型为数组
            if (key.startsWith("_") && document.get(key) instanceof List) {
                //
                List<AgentFile> currentAgentFiles = saveFilesToContext(context, (List<Document>) document.get(key));
                agentFiles.addAll(currentAgentFiles);
                // 修改为file name列表
                document.put(key,
                        currentAgentFiles.stream().map(AgentFile::getFileName).reduce((a, b) -> a + "," + b)
                                .orElse(""));
                // 设置修改标记为true
                modified = true;
            }
        }

        // 4.如果修改标记为true,返回document的JSON字符串;否则直接返回requestStr
        if (modified) {
            request.setRequest(document.toJson());
        }

        //
        return agentFiles;
    }

    private static List<AgentFile> saveFilesToContext(AgentContext context, List<Document> files) throws Exception {
        File uploadPath = ServerUtil.getService(AgentExecuteService.class).getAgentUploadPath();
        //
        List<AgentFile> agentFiles = new ArrayList<>(files.size());
        for (Document file : files) {
            String id = file.getString("id");
            // 检查id必须是由数字和大小写字母组成,禁止其他任何字符- 防止用户修改id读取不允许访问的文件
            if (id == null || !id.matches("^[a-zA-Z0-9]+$")) {
                throw new IllegalArgumentException(
                        "Invalid file id: " + id + ", id must contain only letters and numbers");
            }
            //
            File fileToLoad = new File(uploadPath, id);
            byte[] content = FileHelper.readFile(fileToLoad);
            // 不删除,这样可以重复利用
            fileToLoad.deleteOnExit();
            AgentFile agentFile = AgentUtil.getAgentFileService(context).upload(context, file.getString("name"),
                    file.getString("description"), content);
            agentFiles.add(agentFile);
        }
        return agentFiles;

    }
}
