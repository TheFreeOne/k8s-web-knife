package org.freeone.k8s.web.knife.entity.vo;

import io.kubernetes.client.custom.IntOrString;

import java.util.List;
import java.util.Map;

public class HTTPGetActionVo {

    private String host;

    private List<Map<String, String>> httpHeaders = null;

    private String path;



    private String portString;


    private Integer portInt;

    private String scheme;

    public String getPortString() {
        return this.portString;
    }

    public void setPortString(String portString) {
        this.portString = portString;
    }

    public Integer getPortInt() {
        return this.portInt;
    }

    public void setPortInt(Integer portInt) {
        this.portInt = portInt;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<Map<String, String>> getHttpHeaders() {
        return this.httpHeaders;
    }

    public void setHttpHeaders(List<Map<String, String>> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
}
