# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


from __future__ import print_function
import socket
import SimpleITK as sitk
from skimage import measure
import os
import cv2
import sys
import time
import os
#转成mha格式
def toMha(input,output):
 print("start tomha")
 reader = sitk.ImageSeriesReader()

 dicom_names = reader.GetGDCMSeriesFileNames(input)
 reader.SetFileNames(dicom_names)

 image = reader.Execute()

 size = image.GetSize()
 print("Image size:", size[0], size[1], size[2])

 # print("Writing image:", sys.argv[2])

 sitk.WriteImage(image, output)

 # if ("SITK_NOSHOW" not in os.environ):
 #   sitk.Show(image, "Dicom Series")

#调整窗宽窗位
def adjust_img_level_window(input,output):
 print("start adjust")
 mha_img = sitk.ReadImage(input)
 img_arr = sitk.GetArrayFromImage(mha_img)  # ndarray类型，每个数据的type是什么呢？


 level = -350 # 窗位
 window = 1000  # 窗宽
 window_minimum = level - window / 2  # 设定窗位窗宽后，这就是对应的最小强度值和最大强度值
 window_maximum = level + window / 2
 img_arr[img_arr < window_minimum] = window_minimum
 img_arr[img_arr > window_maximum] = window_maximum

   # 保存图片
 mha_img = sitk.GetImageFromArray(img_arr)
 sitk.WriteImage(mha_img, output)

#分割
def lungmask(vol):
 print("start sege")
 # 获取体数据的尺寸
 size = sitk.Image(vol).GetSize()
 # 获取体数据的空间尺寸
 spacing = sitk.Image(vol).GetSpacing()
 # 将体数据转为numpy数组
 volarray = sitk.GetArrayFromImage(vol)

 # 根据CT值，将数据二值化（一般来说-300以下是空气的CT值）
 volarray[volarray >= -300] = 1
 volarray[volarray <= - 300] = 0
 # 生成阈值图像
 threshold = sitk.GetImageFromArray(volarray)
 threshold.SetSpacing(spacing)

 # 利用种子生成算法，填充空气
 ConnectedThresholdImageFilter = sitk.ConnectedThresholdImageFilter()
 ConnectedThresholdImageFilter.SetLower(0)
 ConnectedThresholdImageFilter.SetUpper(0)
 ConnectedThresholdImageFilter.SetSeedList([(0, 0, 0), (size[0] - 1, size[1] - 1, 0)])

 # 得到body的mask，此时body部分是0，所以反转一下
 bodymask = ConnectedThresholdImageFilter.Execute(threshold)
 bodymask = sitk.ShiftScale(bodymask, -1, -1)

 # 用bodymask减去threshold，得到初步的lung的mask
 temp = sitk.GetImageFromArray(sitk.GetArrayFromImage(bodymask) - sitk.GetArrayFromImage(threshold))
 temp.SetSpacing(spacing)
 # 利用形态学来去掉一定的肺部的小区域

 bm = sitk.BinaryMorphologicalClosingImageFilter()
 bm.SetKernelType(sitk.sitkBall)
 bm.SetKernelRadius(2)
 bm.SetForegroundValue(1)
 lungmask = bm.Execute(temp)

 # 利用measure来计算连通域
 lungmaskarray = sitk.GetArrayFromImage(lungmask)
 label = measure.label(lungmaskarray, connectivity=2)
 props = measure.regionprops(label)

 # 计算每个连通域的体素的个数
 numPix = []
 for ia in range(len(props)):
  numPix += [props[ia].area]

 # 最大连通域的体素个数，也就是肺部
 maxnum = max(numPix)
 # 遍历每个连通区域
 for i in range(len(numPix)):
  # 如果当前连通区域不是最大值所在的区域，则当前区域的值全部置为0，否则为1
  if numPix[i] != maxnum:
   label[label == i + 1] = 0
  else:
   label[label == i + 1] = 1

 label = label.astype("int16")
 l = sitk.GetImageFromArray(label)
 l.SetSpacing(spacing)
 return l


def main(adjustw, seged, output, tomha):

 vol = sitk.ReadImage(adjustw)
 volarray = sitk.GetArrayFromImage(vol)
 newvol = sitk.GetImageFromArray(volarray)
 newvol.SetSpacing(vol.GetSpacing())
 newvol.SetDirection(vol.GetDirection())
 newvol.SetOrigin(vol.GetOrigin())

 mask = lungmask(newvol)
 print("start engage")
 sitk.WriteImage(mask, seged)
 mif = sitk.MaskImageFilter()
 ori = sitk.ReadImage(tomha)
 oriarr = sitk.GetArrayFromImage(ori);
 ori.SetSpacing(ori.GetSpacing())
 ori.SetOrigin(ori.GetOrigin())
 ori.SetDirection(ori.GetDirection())

 segedr = sitk.ReadImage(seged)
 segedr.SetSpacing(ori.GetSpacing())
 segedr.SetOrigin(ori.GetOrigin())
 segedr.SetDirection(ori.GetDirection())
 segedarr = sitk.GetArrayFromImage(segedr);
 fin = mif.Execute(ori, segedr)
 sitk.WriteImage(fin, output)
 print("finish seg")

if __name__ == '__main__':
 # input="C:\\Users\\TimBai\\Desktop\\data\\LIDC-IDRI-0008\\1.3.6.1.4.1.14519.5.2.1.6279.6001.185810436275168701789786930141\\1.3.6.1.4.1.14519.5.2.1.6279.6001.774060103415303828812229821954"
 # mha="D:\\img\\tomha.mha"
 # adjustw="D:\\img\\adjustw.mha"
 # seged="D:\\img\\seged.mha"
 # output="D:\\img\\fin.mha"



 # 在构建socket的时候需要用到ip和端口，必须是元组的形式。
 # 另外，因为是本机上的两个程序通信，所以设置成localhost，
 # 如果要和网络上的其他主机进行通信，则填上相应主机的ip地
 # 址，端口的话随便设置一个，不要和已知的一些服务冲突就行
 address = ('localhost', 9998)
 # 创建socket对象，同时设置通信模式，AF_INET代表IPv4，SOCK_STREAM代表流式socket，使用的是tcp协议
 server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
 # 绑定到我们刚刚设置的ip和端口元组，代表我们的服务运行在本机的9999端口上
 server.bind(address)
 # 开始监听，5位最大挂起的连接数
 server.listen(5)
 # 无限循环，实现反复接收请求
 while True:
  # accept()方法被动接受客户端连接，阻塞，等待连接. client是客户端的socket对象，可以实现消息的接收和发送，addr表示客户端的地址
  client, addr = server.accept()
  baseFileb = client.recv(500)  # 代表从发过来的数据中读取13byte的数据
  # print(rootb)
  # baseFileb=client.recv(500)
  baseFile=baseFileb.decode(encoding='utf-8')
  list=baseFile.split("//")
  root=list[0]+"\\"+list[1]+"\\"+list[2]+"\\"+list[3]+"\\"+list[4]+"\\"+list[5]
  # print(list)
  # baseFile=baseFileb.decode(encoding='utf-8')
  print(root)
  print(baseFile)
  input=baseFile
  mha=root+"\\tomha.mha"
  adjustw=root+"\\adjustw.mha"
  seged=root+"\\seged.mha"
  output=root+"\\fin.mha"
  start_time=time.time()
  toMha(input, mha)
  adjust_img_level_window(mha, adjustw)
  main(adjustw, seged, output, mha)
  end_time=time.time()
  run_time=end_time-start_time
  print('分割时间为'+str(run_time))
  os.remove(mha)
  os.remove(adjustw)
  os.remove(seged)
  client.sendall(b'1')  # 发送消息给客户端，发送的消息必须是byte类型
  client.close()  # 关闭连接
 server.close()

  # toMha("C:\\Users\\TimBai\\Desktop\\data\\LIDC-IDRI-0001\\1.3.6.1.4.1.14519.5.2.1.6279.6001.298806137288633453246975630178\\1.3.6.1.4.1.14519.5.2.1.6279.6001.179049373636438705059720603192","D:\\img\\tomha.mha")
 # toMha(input, mha)
 # adjust_img_level_window(mha, adjustw)
 # main(adjustw, seged, output, mha)
 # os.remove(mha)
 # os.remove(adjustw)
 # os.remove(seged)



