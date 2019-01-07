package com.networking.constants;

public class CuckooConstants
{
    public static String stopCuckooCommand="kill -9 $(ps -ef | grep '[a-zA-Z/]/bin/cuckoo' | head -n 1 |awk '{print $2}')";
    public static String startCuckooCommand="source venv/bin/activate && supervisord -c $CWD/supervisord.conf";
    public static String externalMediaDirectory="/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/";
    public static String localCuckooDirectory="/home/cuckoo/.cuckoo/storage/analyses/";
    public static String localMalwareReportsDirectory="/home/cuckoo/Desktop/MalwareReports/";

}
