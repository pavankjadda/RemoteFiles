package com.networking.home;

import com.networking.download.DownloadOperations;

public class GetRemoteFiles
{
    public static void main(String[] args)
    {
        DownloadOperations downloadOperations= new DownloadOperations();
        downloadOperations.getFile("/home/cuckoo/Desktop/hellotest.txt",
                "/Users/pjadda/Downloads/test2.txt");

    }
}
