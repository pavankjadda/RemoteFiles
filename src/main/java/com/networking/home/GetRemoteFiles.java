package com.networking.home;

import com.networking.config.RemoteHost;
import com.networking.download.DownloadOperations;
import com.networking.ssh.SSHOperations;
import com.networking.upload.DeleteOperations;
import com.networking.util.RemoteOperationsUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GetRemoteFiles
{
    public static void main(String[] args)
    {
        RemoteOperationsUtil remoteOperationsUtil=new RemoteOperationsUtil();
        List<RemoteHost> remoteHosts=remoteOperationsUtil.getRemoteHostsDetails();
        AtomicReference<RemoteHost> remoteHostAtomicReference=new AtomicReference<>();
        for (RemoteHost remoteHost1 : remoteHosts)
        {
            if (remoteHost1.getIpAddress().equals("192.168.1.120"))
            {
                remoteHostAtomicReference.set(remoteHost1);
            }
        }
        RemoteHost remoteHost=remoteHostAtomicReference.get();

        DownloadOperations downloadOperations= new DownloadOperations(remoteHost);
        //downloadOperations.copyReportsFromRemoteToLocalDirectory(remoteHost.getReportsDirectory(),"/home/cuckoo/Desktop/MalwareReports/");

        DeleteOperations deleteOperation=new DeleteOperations(remoteHost);
        //deleteOperation.deleteAnalyzedFiles(remoteHost.getMalwareFilesDirectory(),remoteHost.getReportsDirectory());

         /* Move files */
        //remoteOperationsUtil.moveFiles("/home/cuckoo/Desktop/MalwareReports","/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/");

        /* SSH Operations*/
        SSHOperations sshOperations=new SSHOperations(remoteHost,"shell");
        //sshOperations.executeCommand("ls");
        sshOperations.OpenShell();
    }


}
