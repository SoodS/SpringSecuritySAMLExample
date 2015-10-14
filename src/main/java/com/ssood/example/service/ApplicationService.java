package com.ssood.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    @Value("${scheme}")
    private String scheme;

    @Value("${example.server.name}")
    private String serverName;

    @Value("#{new Boolean('${include.port.in.request}')}")
    private Boolean includePortInRequest;

    @Value("${example.server.port}")
    private long port;

    public String getHostURL() {
        String url = scheme + "://" + serverName;
        if (includePortInRequest) {
            url += ":" + port;
        }
        return url;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Boolean getIncludePortInRequest() {
        return includePortInRequest;
    }

    public void setIncludePortInRequest(Boolean includePortInRequest) {
        this.includePortInRequest = includePortInRequest;
    }

    public long getPort() {
        return port;
    }

    public void setPort(long port) {
        this.port = port;
    }
}