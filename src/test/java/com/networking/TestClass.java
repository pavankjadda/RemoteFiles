package com.networking;

import java.io.File;
import java.nio.file.Files;

public class TestClass
{
    public static void main(String[] args)
    {
        boolean isDirectory=Files.isDirectory(new File("/Users/pjadda/Desktop").toPath());
        System.out.println("/Users/pjadda/Desktop is Directory => "+isDirectory);
    }
}
