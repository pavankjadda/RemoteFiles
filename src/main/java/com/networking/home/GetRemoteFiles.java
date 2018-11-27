package com.networking.home;

import com.networking.download.DownloadOperations;
import com.networking.upload.DeleteAnalyzedMalwareFiles;

public class GetRemoteFiles
{
    public static void main(String[] args)
    {
        DownloadOperations downloadOperations= new DownloadOperations();
        //downloadOperations.copyFilesFromDirectory("/home/cuckoo/.cuckoo/storage/analyses/","/home/cuckoo/Desktop/MalwareReports/");
        //downloadOperations.listFilesInDirectory("/home/cuckoo/.cuckoo/storage/analyses/");

        DeleteAnalyzedMalwareFiles deleteAnalyzedMalwareFiles=new DeleteAnalyzedMalwareFiles();
        deleteAnalyzedMalwareFiles.deleteAnalyzedFiles("/home/cuckoo/Desktop/VirusShare/VirusShare_00000/","/home/cuckoo/.cuckoo/storage/analyses/");


    }
}
