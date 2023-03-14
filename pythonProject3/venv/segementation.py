import SimpleITK as sitk

from skimage import measure
import cv2

def lungmask(vol):
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

# def union_image_mask(image_path, mask_path, num):
#     # 读取原图
#     image = cv2.imread(image_path)
#     # print(image.shape) # (400, 500, 3)
#     # print(image.size) # 600000
#     # print(image.dtype) # uint8
#
#     # 读取分割mask，这里本数据集中是白色背景黑色mask
#     mask_2d = cv2.imread(mask_path, cv2.IMREAD_GRAYSCALE)
#     # 裁剪到和原图一样大小
#     mask_2d = mask_2d[0:400, 0:500]
#     h, w = mask_2d.shape
#     cv2.imshow("2d", mask_2d)
#
#     # 在OpenCV中，查找轮廓是从黑色背景中查找白色对象，所以要转成黑色背景白色mask
#     mask_3d = np.ones((h, w), dtype='uint8')*255
#     # mask_3d_color = np.zeros((h,w,3),dtype='uint8')
#     mask_3d[mask_2d[:, :] == 255] = 0
#     cv2.imshow("3d", mask_3d)
#     ret, thresh = cv2.threshold(mask_3d, 127, 255, 0)
#     im2, contours, hierarchy = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
#     cnt = contours[0]
#     cv2.drawContours(image, [cnt], 0, (0, 255, 0), 1)
#     # 打开画了轮廓之后的图像
#     cv2.imshow('mask', image)
#     k = cv2.waitKey(0)
#     if k == 27:
#         cv2.destroyAllWindows()
#     # 保存图像
#     # cv2.imwrite("./image/result/" + str(num) + ".bmp", image)

def main(adjustw, seged, output,tomha):
    vol = sitk.ReadImage(adjustw)
    volarray = sitk.GetArrayFromImage(vol)
    newvol = sitk.GetImageFromArray(volarray)
    newvol.SetSpacing(vol.GetSpacing())
    newvol.SetDirection(vol.GetDirection())
    newvol.SetOrigin(vol.GetOrigin())


    mask = lungmask(newvol)

    sitk.WriteImage(mask, seged)
    mif = sitk.MaskImageFilter()
    ori=sitk.ReadImage(tomha)
    oriarr=sitk.GetArrayFromImage(ori);
    ori.SetSpacing(ori.GetSpacing())
    ori.SetOrigin(ori.GetOrigin())
    ori.SetDirection(ori.GetDirection())

    segedr = sitk.ReadImage(seged)
    segedr.SetSpacing(ori.GetSpacing())
    segedr.SetOrigin(ori.GetOrigin())
    segedr.SetDirection(ori.GetDirection())
    segedarr = sitk.GetArrayFromImage(segedr);
    fin= mif.Execute(ori,segedr)
    sitk.WriteImage(fin,output)


if __name__ == "__main__":
    main("D:\\img\\adjustw.mha","D:\\img\\seged.mha","D:\\img\\fin.mha","D:\\img\\tomha.mha")