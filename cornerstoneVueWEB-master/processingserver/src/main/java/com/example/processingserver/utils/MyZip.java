package com.example.processingserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MyZip {
    private void zip(String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFileName)); // 创建ZipOutputStream类对象
        zip(out, inputFile, ""); // 调用方法
        System.out.println("压缩中…"); // 输出信息
        out.close(); // 将流关闭
    }

    private void zip(ZipOutputStream out, File f, String base)
            throws Exception { // 方法重载
        if (f.isDirectory()) { // 测试此抽象路径名表示的文件是否是一个目录
            File[] fl = f.listFiles(); // 获取路径数组
            out.putNextEntry(new ZipEntry(base + "/")); // 写入此目录的entry
            base = base.length() == 0 ? "" : base + "/"; // 判断参数是否为空
            for (int i = 0; i < fl.length; i++) { // 循环遍历数组中文件
                zip(out, fl[i], base + fl[i]);
            }
        } else {
            out.putNextEntry(new ZipEntry(base)); // 创建新的进入点
            // 创建FileInputStream对象
            FileInputStream in = new FileInputStream(f);
            int b; // 定义int型变量
            System.out.println(base);
            while ((b = in.read()) != -1) { // 如果没有到达流的尾部
                out.write(b); // 将字节写入当前ZIP条目
            }
            in.close(); // 关闭流
        }
    }

    public static void main(String[] temp) { // 主方法
        MyZip book = new MyZip(); // 创建本例对象
        try {
            // 调用方法，参数为压缩后文件与要压缩文件
            book.zip("D://img//hello.zip", new File("D:\\img\\H0002\\2021-10\\30\\RDR000002\\1.3.6.1.4.1.14519.5.2.1.6279.6001.490157381160200744295382098329\\1.3.6.1.4.1.14519.5.2.1.6279.6001.619372068417051974713149104919"));
            System.out.println("压缩完成"); // 输出信息
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}
