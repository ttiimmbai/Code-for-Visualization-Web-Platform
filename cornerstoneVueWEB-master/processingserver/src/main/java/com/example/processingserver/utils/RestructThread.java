package com.example.processingserver.utils;

import com.example.processingserver.entity.DicomList;
import com.example.processingserver.service.DicomListService;
import lombok.Data;

@Data
public class RestructThread implements Runnable{
    private String inputfilename;
    private String outputfilename;
    private DicomListService dicomListService;
    private DicomList dicomList;
    private String root;

    public RestructThread(String inputfilename, String outputfilename, DicomListService dicomListService, DicomList dicomList, String root) {
        this.inputfilename = inputfilename;
        this.outputfilename = outputfilename;
        this.dicomListService = dicomListService;
        this.dicomList = dicomList;
        this.root = root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public void setDicomListService(DicomListService dicomListService) {
        this.dicomListService = dicomListService;
    }

    public void setDicomList(DicomList dicomList) {
        this.dicomList = dicomList;
    }

    public void setInputfilename(String inputfilename) {
        this.inputfilename = inputfilename;
    }

    public void setOutputfilename(String outputfilename) {
        this.outputfilename = outputfilename;
    }



    @Override
    public void run() {
        if (dicomList.getDevtype().equals("4") ) {
            System.out.println("inputfilename是+++"+inputfilename);
            System.out.println("outputfilename是+++"+outputfilename);
            dicomList.setStatus(50);
            restruction r = new restruction(inputfilename, outputfilename);
            r.restructMRA();
            dicomList.setStatus(100);
            dicomListService.updateById(dicomList);

        } else {
            //未分割重建
            restruction r = new restruction(inputfilename, outputfilename);
            r.restruct();
            dicomList.setStatus(50);
            //分割重建
            //与python分割服务器通信
            SendMessage sendMessage = new SendMessage("localhost", 9998);
            String baseFile = inputfilename;
            System.out.println("basefile:" + baseFile);
            System.out.println("root:" + this.root);
            byte[] rootb = this.root.getBytes();
            byte[] baseFileb = baseFile.getBytes();
            if (sendMessage.send(baseFileb).equals("1")) {
                //执行重建
                dicomList.setStatus(75);
                System.out.println("发送socket");
                restruction r2 = new restruction(this.root + "\\fin.mha", outputfilename + "_seg");
                r2.restructSeged();
                dicomList.setStatus(100);
            }
            //更新status
            dicomListService.updateById(dicomList);
        }
    }
}
