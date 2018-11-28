package com.networking.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.networking.config.RemoteHost;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class RemoteOperationsUtil
{
    private ChannelSftp channelSftp = null;
    private RemoteHost remoteHost = null;

    public RemoteOperationsUtil()
    {

    }

    public RemoteOperationsUtil(RemoteHost remoteHost)
    {
        JSch jSch = new JSch();
        try
        {
            Session session = jSch.getSession(remoteHost.getUsername(), remoteHost.getIpAddress());
            session.setPassword(remoteHost.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            this.channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            this.remoteHost = remoteHost;
        } catch (JSchException e)
        {
            e.printStackTrace();
        }
    }

    public void getDirectoryNumbers(Vector directories, List<Integer> reportsDirectoryNumbers)
    {
        for (Object directory : directories)
        {
            ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) directory;
            if (lsEntry.getAttrs().isDir() && !(lsEntry.getFilename().equals(".") || lsEntry.getFilename().equals("..")))
            {
                try
                {
                    reportsDirectoryNumbers.add(Integer.valueOf(lsEntry.getFilename()));
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void moveFiles(String source, String destination)
    {
        try
        {
            File file = new File(source);
            File[] fileList = file.listFiles();
            assert fileList != null;
            for (File fileEntry : fileList)
            {
                if (!fileEntry.isDirectory())
                {
                    Files.move(Paths.get(fileEntry.getAbsolutePath()), Paths.get(destination + fileEntry.getName()));
                    System.out.println("File " + fileEntry.getAbsolutePath() + " moved to " + (destination + fileEntry.getName()));
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public List<RemoteHost> getRemoteHostsDetails()
    {
        File file;
        List<RemoteHost> remoteHosts = null;
        try
        {
            file = new ClassPathResource("remote_hosts.json").getFile();
            remoteHosts = mapJsonToObject(file);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return remoteHosts;
    }

    public List<RemoteHost> mapJsonToObject(File file)
    {
        List<RemoteHost> remoteHosts = new ArrayList<>();
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);
            for (JsonNode jsonNode : rootNode)
            {
                remoteHosts.add(objectMapper.readValue(jsonNode.toString(), RemoteHost.class));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return remoteHosts;
    }
}
