package com.example.processingserver.utils;

import lombok.Data;

import java.io.File;

@Data
public class FileNum {

    private String path;

    public FileNum(String path) {
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public int countFile(){
       String path=this.path;
        int fileCount = 0;
        int folderCount = 0;
        File d = new File(path);
        File list[] = d.listFiles();
        for(int i = 0; i < list.length; i++){
            if(list[i].isFile()){
                fileCount++;
            }else{
                folderCount++;
            }
        }

        return folderCount;
    }
}
