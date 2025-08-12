package com.zrlog.plugin.data.codec;

import com.zrlog.plugin.common.type.HttpMethod;

import java.util.Map;

public class BaseHttpRequestInfo {

    protected String accessUrl;
    protected Map<String, String> header;
    protected byte[] requestBody;
    protected HttpMethod httpMethod;

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }
}
