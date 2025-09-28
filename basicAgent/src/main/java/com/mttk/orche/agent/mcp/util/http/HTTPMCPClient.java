package com.mttk.orche.agent.mcp.util.http;

import com.mttk.orche.agent.mcp.util.common.MCPClientBase;
import com.mttk.orche.agent.mcp.util.common.MCPTransport;

/**
 * HTTP传输的MCP客户端
 */
public class HTTPMCPClient extends MCPClientBase {

    public HTTPMCPClient(String serverUrl) {
        super(new HTTPTransport(serverUrl));
    }

    public HTTPMCPClient(MCPTransport transport) {
        super(transport);
    }
}
