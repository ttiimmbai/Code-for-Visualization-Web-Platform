
import os
import SimpleITK as sitK
import pydicom
import numpy as np
import cv2
from tqdm import tqdm
import time
import numpy as np
import pydicom
from PIL import Image

def JpegToDicom(dcm,jpg):
    ds = pydicom.dcmread(dcm)  # pre-existing dicom file
    jpg_image = Image.open(jpg)  # the PNG or JPG file to be replace

    if jpg_image.mode == 'L':

        np_image = np.array(jpg_image.getdata(), dtype=np.uint8)
        ds.Rows = jpg_image.height
        ds.Columns = jpg_image.width
        ds.PhotometricInterpretation = "MONOCHROME1"
        ds.SamplesPerPixel = 1
        ds.BitsStored = 8
        ds.BitsAllocated = 8
        ds.HighBit = 7
        ds.PixelRepresentation = 0
        ds.PixelData = np_image.tobytes()
        ds.save_as('D:\\img\\H0002\\2019-10\\30\\RDR000001\\detected\\detected\\000003.dcm')

    elif jpg_image.mode == 'RGB':

        np_image = np.array(jpg_image.getdata(), dtype=np.uint8)[:, :3]
        ds.Rows = jpg_image.height
        ds.Columns = jpg_image.width
        ds.PhotometricInterpretation = "RGB"
        ds.SamplesPerPixel = 3
        ds.BitsStored = 8
        ds.BitsAllocated = 8
        ds.HighBit = 7
        ds.PixelRepresentation = 0
        ds.PixelData = np_image.tobytes()
        ds.save_as('D:\\img\\H0002\\2019-10\\30\\RDR000001\\detected\\detected\\000003.dcm')


def convert_from_dicom_to_png(img,low_window,high_window,save_path):
    lungwin = np.array([low_window*1.,high_window * 1.])
    newimg = (img-lungwin[0])/(lungwin[1]-lungwin[0])  #归一化
    newimg = (newimg*255).astype('uint8')  #扩展像素值到【0，255】
    cv2.imwrite(save_path,newimg)

def toPNG(input,output):
    ds_array = sitK.ReadImage(input)
    img_array = sitK.GetArrayFromImage(ds_array)
    shape = img_array.shape
    img_array = np.reshape(img_array, (shape[1], shape[2]))
    high = np.max(img_array)
    low = np.min(img_array)
    convert_from_dicom_to_png(img_array, low, high, output)

file_paths = []
title = []


def load(path):
    for file in os.listdir(path):
        file_path = os.path.join(path, file)
        if os.path.splitext(file)[1] == '.dcm':  # 判断文件类型
            file_paths.append(os.path.join(file_path))  # 文件所在目录
            title.append(os.path.splitext(file)[0])
if __name__ == '__main__':
    start=time.time()
    path='D:\\img\\H0002\\2021-10\\30\\RDR000002\\1.3.6.1.4.1.14519.5.2.1.6279.6001.490157381160200744295382098329\\1.3.6.1.4.1.14519.5.2.1.6279.6001.619372068417051974713149104919'
    load(path)
    for t in title:
        file=path+'\\'+t+'.dcm'
        toPNG(file,'D:\\img\\H0002\\2021-10\\30\\RDR000002\\PNG\\'+t+'.jpg')
    end=time.time()
    run=end-start
    print('运行时间为'+str(run))
    # JpegToDicom('D:\\img\\H0002\\2019-10\\30\\RDR000001\\1.3.6.1.4.1.14519.5.2.1.6279.6001.298806137288633453246975630178\\1.3.6.1.4.1.14519.5.2.1.6279.6001.179049373636438705059720603192\\000001.dcm','D:\\yolov5\\yolov5-6.0\\runs\\detect\\exp\\000003.jpg')

