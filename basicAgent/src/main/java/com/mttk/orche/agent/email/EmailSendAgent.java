package com.mttk.orche.agent.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;

import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.util.StringUtil;

@AgentTemplateFlag(key = "_email-send-agent", name = "邮件发送", description = "发送电子邮件,可以包含标题,内容和附件", callDefineClass = EmailSendCallDefine.class, i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "protocol", label = "发件协议", mode = "select", size = 1, defaultVal = "smtp", mandatory = true, props = "options:smtp:SMTP,smtps:SMTP(SSL),smtp_starttls:SMTP(STARTTLS)")
@Control(key = "host", label = "发件服务器", size = 1, mandatory = true)
@Control(key = "port", label = "发件端口", size = 1, props = { "dataType:number" })
@Control(key = "timeout", label = "连接超时(秒)", size = 1, props = { "dataType:number" })
@Control(key = "user", label = "用户名", size = 1, mandatory = true)
@Control(key = "password", label = "密码", size = 1, props = "type:password", mandatory = true)
@Control(key = "emailSender", label = "发送邮箱地址", size = 1, mandatory = true)
@Control(key = "to", label = "接收邮箱地址", size = 1)
@Control(key = "cc", label = "抄送邮箱地址", size = 1)
public class EmailSendAgent extends AbstractAgent {
    private static final Logger logger = LoggerFactory.getLogger(EmailSendAgent.class);

    @Override
    protected String executeInternal(AgentParam para, String transactionId) throws Exception {
        AgentContext context = para.getContext();
        AdapterConfig config = para.getConfig();
        AdapterConfig request = para.getRequest();

        String subject = request.getString("subject");
        String content = request.getString("content");
        String attachmentsStr = request.getString("_attchments");

        String to = request.getString("to");
        String cc = request.getString("cc");
        if (StringUtil.isEmpty(to)) {
            // 如果提示词没有给出接收者,使用配置里的接收者;如果配置里还没有,则发送到发送者
            to = config.getString("to", config.getString("emailSender"));
        }
        if (StringUtil.isEmpty(cc)) {
            cc = config.getString("cc");
        }
        try {
            // 发送邮件
            sendEmail(context, config, subject, content, attachmentsStr, to, cc);
            //
            String result = "邮件发送成功: 收件人=" + to + ", 主题="
                    + subject + ",附件数量=" + (attachmentsStr != null ? attachmentsStr.split(",").length : 0);
            // context.sendResponse(new ChatResonseMessage("_data-content", transactionId,
            // result));

            return result;

        } catch (Exception e) {

            context.sendResponse(new ChatResonseMessage("_agent-end", transactionId));
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 发送邮件
     */
    private void sendEmail(AgentContext context,
            AdapterConfig config, String subject,
            String content, String attachmentsStr, String to, String cc) throws Exception {

        // 邮件服务器配置
        String protocol = config.getString("protocol");
        String host = config.getString("host");
        int port = config.getInteger("port");
        String user = config.getString("user");
        String password = config.getString("password");
        String emailSender = config.getString("emailSender");

        // 设置邮件服务器属性
        Properties props = new Properties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");

        // 如果是SSL/TLS端口，启用SSL
        if (port == 465 || port == 587) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.starttls.enable", "true");
        }

        // 创建会话
        javax.mail.Session session = javax.mail.Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        // 创建邮件消息
        javax.mail.Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailSender));

        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        if (StringUtil.notEmpty(cc)) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
        }
        message.setSubject(subject);

        // 创建多部分消息
        Multipart multipart = new MimeMultipart();

        // 添加文本内容
        BodyPart textPart = new MimeBodyPart();
        textPart.setText(content);
        multipart.addBodyPart(textPart);

        // 处理附件
        if (attachmentsStr != null && !attachmentsStr.trim().isEmpty()) {
            List<String> attachmentFiles = Arrays.stream(attachmentsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            for (String fileName : attachmentFiles) {

                // 从文件管理器获取附件内容
                byte[] fileData = AgentUtil.getAgentFileService(context).downloadData(context, fileName);
                if (fileData != null && fileData.length > 0) {
                    // 创建附件部分
                    BodyPart attachmentPart = new MimeBodyPart();

                    // 使用自定义数据源
                    attachmentPart.setDataHandler(new javax.activation.DataHandler(
                            new javax.activation.DataSource() {
                                @Override
                                public InputStream getInputStream() throws IOException {
                                    return new ByteArrayInputStream(fileData);
                                }

                                @Override
                                public OutputStream getOutputStream() throws IOException {
                                    throw new IOException("Read-only data source");
                                }

                                @Override
                                public String getContentType() {
                                    return "application/octet-stream";
                                }

                                @Override
                                public String getName() {
                                    return fileName;
                                }
                            }));

                    attachmentPart.setFileName(MimeUtility.encodeText(fileName, "UTF-8", "B"));
                    multipart.addBodyPart(attachmentPart);

                    // logger.info("添加附件: {}, 大小: {} bytes", fileName, fileData.length);
                }

            }
        }

        // 设置邮件内容
        message.setContent(multipart);

        // 发送邮件
        Transport.send(message);
    }
}
