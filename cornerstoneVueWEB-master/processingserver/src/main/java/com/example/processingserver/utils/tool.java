package com.example.processingserver.utils;

import java.io.File;

public class tool {
    public static void main(String[] args) {
        File file=new File("D:\\img\\H0002\\2021-11\\25\\H0002CT000556\\38210022");
        File list[]=file.listFiles();
        for (File f:list){
            f.renameTo(new File("D:\\img\\H0002\\2021-11\\25\\H0002CT000556\\38210023\\"+f.getName()+".dcm"));
        }
    }
}
