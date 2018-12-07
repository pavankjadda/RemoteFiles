package com.networking.home;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networking.config.RemoteHost;
import com.networking.download.DownloadOperations;
import com.networking.upload.DeleteAnalyzedMalwareFiles;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GetRemoteFiles
{
    public static void main(String[] args)
    {

        List<RemoteHost> remoteHosts=getRemoteHostsDetails();
        AtomicReference<RemoteHost> remoteHostAtomicReference=new AtomicReference<>();
        remoteHosts.forEach(remoteHost1 -> {
            if(remoteHost1.getIpAddress().equals("192.168.1.125"))
                remoteHostAtomicReference.set(remoteHost1);
        });
        RemoteHost remoteHost=remoteHostAtomicReference.get();

        //DownloadOperations downloadOperations= new DownloadOperations(remoteHost.getUsername(),remoteHost.getPassword(),remoteHost.getIpAddress());
        //downloadOperations.copyFilesFromDirectory(remoteHost.getReportsDirectory(),"/home/cuckoo/Desktop/MalwareReports/");
        //downloadOperations.listFilesInDirectory(remoteHost.getReportsDirectory());

        remoteHost.setLocalTempFilePath("/Users/pjadda/Documents/test/report.json");
        DeleteAnalyzedMalwareFiles deleteAnalyzedMalwareFiles=new DeleteAnalyzedMalwareFiles(remoteHost);
        deleteAnalyzedMalwareFiles.deleteAnalyzedFiles(remoteHost.getMalwareFilesDirectory(),remoteHost.getReportsDirectory());
        //deleteAnalyzedMalwareFiles.deleteAnalyzedFilesFromLocalCustomDirectory("/home/cuckoo/Desktop/VirusShare_00322", "/home/cuckoo/.cuckoo/reports-backup/malwares");
    }

    private static List<RemoteHost> getRemoteHostsDetails()
    {
        File file;
        List<RemoteHost> remoteHosts=null;
        try
        {
            file = new ClassPathResource("remote_hosts.json").getFile();
            remoteHosts=mapJsonToObject(file);
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
        return remoteHosts;
    }

    private static List<RemoteHost> mapJsonToObject(File file)
    {
        List<RemoteHost> remoteHosts=new ArrayList<>();
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode=objectMapper.readTree(file);
            for (JsonNode jsonNode : rootNode)
            {
                remoteHosts.add(objectMapper.readValue(jsonNode.toString(), RemoteHost.class));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return remoteHosts;
    }
}
