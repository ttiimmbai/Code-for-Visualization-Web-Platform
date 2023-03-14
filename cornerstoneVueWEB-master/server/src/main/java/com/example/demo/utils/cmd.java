package com.example.demo.utils;

import java.io.*;

public class cmd {
    public static void main(String[] args) {
        InputStream in = null;
        String[] cmd = new String[]{"cmd.exe", "/C", "obj2gltf -i D:\\Test\\MCObj\\polywrite.obj -o D:\\Test\\MCObj\\polywrite.gltf -separate"};

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
