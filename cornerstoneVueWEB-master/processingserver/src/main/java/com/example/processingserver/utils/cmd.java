package com.example.processingserver.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class cmd {
    private String inputfilename;
    private String outpufilename;

    public cmd(String inputfilename, String outpufilename) {
        this.inputfilename = inputfilename;
        this.outpufilename = outpufilename;
    }

    public void invert() {
        InputStream in = null;
        String[] cmd = new String[]{"cmd.exe", "/C", "obj2gltf -i "+inputfilename+" -o "+outpufilename+" -separate"};

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            in = process.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, "GBK"));

            String line = null;

            while ((line = read.readLine()) != null) {
                System.out.println(line);
            }

            int exitValue = process.waitFor();
            System.out.println("返回值：" + exitValue);
            process.getOutputStream().close();       // 不要忘记了一定要关


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public  void draco(String input,String output) {

        InputStream in = null;
        String[] cmd = new String[]{"cmd.exe", "/C", "gltf-pipeline -i "+input+" -o "+output+" -d"};

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            in = process.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, "GBK"));

            String line = null;

            while ((line = read.readLine()) != null) {
                System.out.println(line);
            }

            int exitValue = process.waitFor();
            System.out.println("返回值：" + exitValue);
            process.getOutputStream().close();       // 不要忘记了一定要关


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



}
