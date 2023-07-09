package com.hhh.server.config;

/**
 * @Description CustomConfig
 * @Author HHH
 * @Date 2023/7/9 11:01
 */
public class CustomConfig {

    private String filename;
    private String namespace;

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public CustomConfig(String filename, String namespace) {
        this.filename = filename;
        this.namespace = namespace;
    }

    public CustomConfig() {
    }

}
