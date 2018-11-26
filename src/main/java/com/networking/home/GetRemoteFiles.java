package com.networking.home;

import com.networking.download.DownloadOperations;

public class GetRemoteFiles
{
    public static void main(String[] args)
    {
        DownloadOperations downloadOperations= new DownloadOperations();
        downloadOperations.copyFilesFromDirectory("/home/cuckoo/.cuckoo/storage/analyses/","/home/cuckoo/Desktop/MalwareReports/");
    }
}
