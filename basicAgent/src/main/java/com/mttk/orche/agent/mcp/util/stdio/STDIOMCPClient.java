package com.mttk.orche.agent.mcp.util.stdio;

import com.mttk.orche.agent.mcp.util.common.MCPClientBase;
import com.mttk.orche.agent.mcp.util.common.MCPTransport;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * STDIO传输的MCP客户端
 */
public class STDIOMCPClient extends MCPClientBase {

    public STDIOMCPClient() {
        super(new STDIOTransport());
    }

    public STDIOMCPClient(InputStream inputStream, OutputStream outputStream) {
        super(new STDIOTransport(inputStream, outputStream));
    }

    public STDIOMCPClient(MCPTransport transport) {
        super(transport);
    }
}
