package com.mercado.sistema.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file.monitor")
@Data
public class FileMonitorConfig {

    @Value("${file.monitor.path_raiz}")
    private String path;

    @Value("${file.monitor.input-directorio}")
    private String inputDirectory;

    @Value("${file.monitor.output-directorio}")
    private String outputDirectory;

    private long pollingInterval = 5000L;
    private String filePattern = "produtos_*.json";
}