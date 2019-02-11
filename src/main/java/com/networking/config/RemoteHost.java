package com.networking.config;

import lombok.Data;

@Data
public class RemoteHost
{
    private String host;

    private String ipAddress;

    private String operatingSystem;

    private String username;

    private String password;

    private Integer port;

    private String reportsDirectory;

    private String malwareFilesDirectory;

    private String localTempFilePath;

    public RemoteHost()
    {
    }

    public RemoteHost(String host, String ipAddress, String operatingSystem, String username, String password, Integer port, String reportsDirectory, String malwareFilesDirectory, String localTempFilePath)
    {
        this.host = host;
        this.ipAddress = ipAddress;
        this.operatingSystem = operatingSystem;
        this.username = username;
        this.password = password;
        this.port = port;
        this.reportsDirectory = reportsDirectory;
        this.malwareFilesDirectory = malwareFilesDirectory;
        this.localTempFilePath = localTempFilePath;
    }


}
