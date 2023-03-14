package com.example.processingserver.utils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendMessage {
    private String ip;
    private int port;

    public SendMessage(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    public String send(byte[] baseFile){
        Socket socket = null;
        try {
            // 连接服务器
            socket = new Socket(this.ip, this.port);
            // 获取输入流
            InputStream in = socket.getInputStream();   //读取数据
            // 获取输出流
            OutputStream out = socket.getOutputStream(); // 发送数据
            // 包装输入流，输出流，包装一下可以直接传输字符串，不包装的话直接使用InputStream和OutputStream只能直接传输byte[]类型数据
            BufferedReader inRead = new BufferedReader(new InputStreamReader(in));
//			PrintWriter outWriter = new PrintWriter(out);



            // 发送数据



           out.write(baseFile);
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
}
