package com.example.processingserver.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Data

public class restruction {
    private String inputfilename;
    private String outputfilename;
    public restruction(String inputfilename,String outputfilename){
        this.inputfilename=inputfilename;
        this.outputfilename=outputfilename;
    }


    public boolean restruct(){
        try {
            long startTime = System.currentTimeMillis();    //获取开始时间
            MCRebuild mcRebuild=new MCRebuild(inputfilename,outputfilename);
            mcRebuild.restructing();
            cmd c=new cmd(outputfilename+".obj",outputfilename+".gltf");
            c.invert();
            File obj=new File(outputfilename+".obj");
            File mtl=new File(outputfilename+".mtl");
            if (obj.delete()){
                System.out.println("obj删除成功");

            }
            if (mtl.delete()){
                System.out.println("mtl删除成功");
            }
            c.draco(outputfilename+".gltf",outputfilename+"Draco.gltf");
            File gltf=new File(outputfilename+".gltf");
            File bin=new File(outputfilename+".bin");
            if (gltf.delete()){
                System.out.println("gltf删除成功");

            }
            if (bin.delete()){
                System.out.println("bin删除成功");
            }
            long endTime = System.currentTimeMillis();    //获取结束时间

            System.out.println("程序运行时间：" + (endTime - startTime)/1000 + "s");    //输出程序运行时间
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public boolean restructSeged(){
        try {
            long startTime = System.currentTimeMillis();    //获取开始时间
            SegMCRebuild mcRebuild=new SegMCRebuild(inputfilename,outputfilename);
            mcRebuild.restructing();
            cmd c=new cmd(outputfilename+".obj",outputfilename+".gltf");
            c.invert();
            File obj=new File(outputfilename+".obj");
            File mtl=new File(outputfilename+".mtl");
            if (obj.delete()){
                System.out.println("obj删除成功");

            }
            if (mtl.delete()){
                System.out.println("mtl删除成功");
            }
            c.draco(outputfilename+".gltf",outputfilename+"Draco.gltf");
            File gltf=new File(outputfilename+".gltf");
            File bin=new File(outputfilename+".bin");
            if (gltf.delete()){
                System.out.println("gltf删除成功");

            }
            if (bin.delete()){
                System.out.println("bin删除成功");
            }

            long endTime = System.currentTimeMillis();    //获取结束时间

            System.out.println("程序运行时间：" + (endTime - startTime)/1000 + "s");    //输出程序运行时间
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public boolean restructMRA(){
        try {
            long startTime = System.currentTimeMillis();    //获取开始时间
            MRAMCRebuild mcRebuild=new MRAMCRebuild(inputfilename,outputfilename);
            mcRebuild.restructing();
            cmd c=new cmd(outputfilename+".obj",outputfilename+".gltf");
            c.invert();
            File obj=new File(outputfilename+".obj");
            File mtl=new File(outputfilename+".mtl");
            if (obj.delete()){
                System.out.println("obj删除成功");

            }
            if (mtl.delete()){
                System.out.println("mtl删除成功");
            }
            c.draco(outputfilename+".gltf",outputfilename+"Draco.gltf");
            File gltf=new File(outputfilename+".gltf");
            File bin=new File(outputfilename+".bin");
            if (gltf.delete()){
                System.out.println("gltf删除成功");

            }
            if (bin.delete()){
                System.out.println("bin删除成功");
            }

            long endTime = System.currentTimeMillis();    //获取结束时间

            System.out.println("程序运行时间：" + (endTime - startTime)/1000 + "s");    //输出程序运行时间
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

}
