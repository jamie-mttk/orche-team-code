package com.mttk.orche.agent.mcp.util.common;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Closeable;
import java.io.IOException;

/**
 * MCP Transport Layer Interface
 * Defines basic transport methods for MCP protocol
 */
public interface MCPTransport extends Closeable {

    /**
     * Send JSON-RPC request
     * 
     * @param request Request object
     * @return Response result
     * @throws Exception Transport exception
     */
    JsonNode sendRequest(JsonNode request) throws Exception;

    /**
     * Test if connection is available
     * 
     * @throws Exception Connection test failed with detailed error information
     */
    void testConnection() throws Exception;

    /**
     * Close transport connection
     * 
     * @throws IOException if an I/O error occurs
     */
    @Override
    void close() throws IOException;
}
