package com.example.processingserver.utils;

import vtk.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class test {
    static
    {
        if (!vtkNativeLibrary.LoadAllNativeLibraries())
        {
            for (vtkNativeLibrary lib : vtkNativeLibrary.values())
            {
                if (!lib.IsLoaded())
                {
                    System.out.println(lib.GetLibraryName() + " not loaded");
                }
            }
        }
        vtkNativeLibrary.DisableOutputWindow(null);
    }
    static void renderMhd () {
        // read input image
        vtkMetaImageReader reader =new vtkMetaImageReader ();
        reader.SetFileName ("D:\\img\\fin.mha");
        reader.Update ();

        vtkImageViewer2 viewer =new vtkImageViewer2();
        viewer.SetInputConnection (reader.GetOutputPort());

        // render window
        vtkRenderWindowInteractor renderWindowInteractor =  new vtkRenderWindowInteractor ();

        viewer.SetupInteractor (renderWindowInteractor);
        viewer.GetRenderer ().ResetCamera();

        renderWindowInteractor.Initialize ();
        renderWindowInteractor.Start ();
    }
    public static String sendMessage() {
        String ip = "localhost";        // 设置发送地址和端口号
        int port = 9999;
        Socket socket = null;
        try {
            // 连接服务器
            socket = new Socket(ip, port);
            // 获取输入流
            InputStream in = socket.getInputStream();   //读取数据
            // 获取输出流
            OutputStream out = socket.getOutputStream(); // 发送数据
            // 包装输入流，输出流，包装一下可以直接传输字符串，不包装的话直接使用InputStream和OutputStream只能直接传输byte[]类型数据
            BufferedReader inRead = new BufferedReader(new InputStreamReader(in));
//			PrintWriter outWriter = new PrintWriter(out);


            String message="C:\\Users\\TimBai\\Desktop\\data\\LIDC-IDRI-0008\\1.3.6.1.4.1.14519.5.2.1.6279.6001.185810436275168701789786930141\\1.3.6.1.4.1.14519.5.2.1.6279.6001.774060103415303828812229821954";
            byte[] m=message.getBytes();
            // 发送数据
            String message2="C:\\Users\\TimBai\\Desktop\\data\\LIDC-IDRI-0008";
            byte[] n=message2.getBytes();
            out.write(m);
            out.write(n);
            // 接受应答
            String result = inRead.readLine();  // 使用了包装后的输入流方便读取消息
            return result;
        } catch(UnknownHostException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return "defeat";
    }


    public static void main(String[] args) {
        SendMessage sendMessage=new SendMessage("localhost",9999);
        String root="D:\\img\\H0002\\2008-10\\22\\RDR000008";
        String baseFile="D:\\img\\H0002\\2008-10\\22\\RDR000008\\uiyyut";
        byte[] rootb=root.getBytes();
        byte[] baseFileb=baseFile.getBytes();

       if (sendMessage.send(baseFileb).equals("1")){
           System.out.println("进行分割");
       }
            //执行重建

    }
}
