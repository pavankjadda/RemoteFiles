package com.networking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networking.util.RemoteOperationsUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass
{
    public static void main(String[] args)
    {
        try
        {
            RemoteOperationsUtil remoteOperationsUtil=new RemoteOperationsUtil();
            remoteOperationsUtil.moveFiles("/home/cuckoo/Desktop/MalwareReports","/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
