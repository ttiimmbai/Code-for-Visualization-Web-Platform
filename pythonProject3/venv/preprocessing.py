from __future__ import print_function
import socket
import SimpleITK as sitk
from skimage import measure
import os
import cv2
import sys
import os
import pydicom

import nibabel as nib
import dicom2nifti

#调整窗宽窗位
def adjust_img_level_window(input,output):
 print("start adjust")
 mha_img = sitk.ReadImage(input)
 img_arr = sitk.GetArrayFromImage(mha_img)  # ndarray类型，每个数据的type是什么呢？


 level =50 # 窗位
 window = 400  # 窗宽
 window_minimum = level - window / 2  # 设定窗位窗宽后，这就是对应的最小强度值和最大强度值
 window_maximum = level + window / 2
 img_arr[img_arr < window_minimum] = window_minimum
 img_arr[img_arr > window_maximum] = window_maximum

   # 保存图片
 mha_img = sitk.GetImageFromArray(img_arr)
 sitk.WriteImage(mha_img, output)

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




if __name__ == '__main__':
     toMha("D:\\img\\H0002\\2022-04\\25\\DJ20200731A0798\\TOF3Dmulti3slab_4_4","D:\\img\\tomhasec.mha")
     # adjust_img_level_window("D:\\img\\H0002\\2021-10\\20\\RDR000003\\fin.mha","D:\\img\\H0002\\2021-10\\20\\RDR000003\\zongge.mha")
     # vol = sitk.ReadImage("D:\\img\\H0002\\2017-08\\20\\RDR000007\\tomhaw.mha")
     # volarray = sitk.GetArrayFromImage(vol)
     # newvol = sitk.GetImageFromArray(volarray)
     # newvol.SetSpacing(vol.GetSpacing())
     # newvol.SetDirection(vol.GetDirection())
     # newvol.SetOrigin(vol.GetOrigin())
     #
     # mask = lungmask(newvol)
     # print("start engage")
     # sitk.WriteImage(mask, "D:\\img\\H0002\\2017-08\\20\\RDR000007\\seged.mha")

     #融合图像
     # mif = sitk.MaskImageFilter()
     # ori=sitk.ReadImage("D:\\Test\\Test\\Brats18_2013_2_1_t1.nii",sitk.sitkInt16)
     # oriarr=sitk.GetArrayFromImage(ori);
     # ori.SetSpacing(ori.GetSpacing())
     # ori.SetOrigin(ori.GetOrigin())
     # ori.SetDirection(ori.GetDirection())
     #
     # segedr = sitk.ReadImage("D:\\Test\\Test\\Brats18_2013_2_1_seg.nii",sitk.sitkInt16)
     # segedr.SetSpacing(ori.GetSpacing())
     # segedr.SetOrigin(ori.GetOrigin())
     # segedr.SetDirection(ori.GetDirection())
     # segedarr = sitk.GetArrayFromImage(segedr);
     # fin= mif.Execute(ori,segedr)
     # sitk.WriteImage(fin,"D:\\Test\\finpv1.nii")


     # root_path =r"F:\brain\PAT00001\STD00001\SER00002"
     #转换nii
     # file_path = 'D:\\img\\H0002\\2022-04\\25\\DJ20200731A0798\\TOF3Dmulti3slab_4_4'
     # #dicom存放⽂件夹
     # series_IDs = sitk.ImageSeriesReader.GetGDCMSeriesIDs(file_path)
     # series_file_names = sitk.ImageSeriesReader.GetGDCMSeriesFileNames(file_path)
     # series_reader = sitk.ImageSeriesReader()
     # series_reader.SetFileNames(series_file_names)
     # image3D = series_reader.Execute()
     # sitk.WriteImage(image3D, 'F:\\0002.nii.gz')

     # filedir = 'C:\\Users\\TimBai\\Desktop\\a full study\\0002.nii'
     # outdir = 'C:\\Users\\TimBai\\Desktop\\a full study\\todicom\\'
     # if not os.path.isdir(outdir):
     #   os.mkdir(outdir)
     # img = sitk.ReadImage(filedir,sitk.sitkInt16)
     # img = sitk.GetArrayFromImage(img)
     # for i in range(img.shape[0]):
     #   select_img = sitk.GetImageFromArray(img[i])
     #   sitk.WriteImage(select_img, outdir + str(img.shape[0] - i) + '.dcm')
     #
     # # 图片存放的路径
     # path = r"F:\brain\PAT00001\STD00001\SER00003"
     #
     # # 遍历更改文件名
     # num = 1
     # for file in os.listdir(path):
     #  os.rename(os.path.join(path, file), os.path.join(path, str(num)) + ".dcm")
     #  num=num+1

