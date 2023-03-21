import vtk.*;

import java.applet.Applet;

public class test  {
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


  public static void main(String[] args)
  {

    //parse command line arguments
//    vtkMetaImageReader readerte=new vtkMetaImageReader();
    String inputFilename = "D:\\img\\H0002\\2022-04\\21\\lilanfangDMR0635651\\WHCATFEFAST_901_9";

    //定义绘制器
    vtkRenderer aRenderer=new vtkRenderer();
    vtkRenderWindow renWin=new vtkRenderWindow();
    renWin.AddRenderer(aRenderer);
    vtkRenderWindowInteractor iren=new vtkRenderWindowInteractor();
    iren.SetRenderWindow(renWin);

    //读取图像
    vtkDICOMImageReader reader = new vtkDICOMImageReader();
    vtkMetaImageReader reader1=new vtkMetaImageReader();
    reader1.SetFileName("D:\\img\\H0002\\2021-10\\30\\RDR000002\\fin.mha");
    reader1.SetDataByteOrderToLittleEndian();
    reader.SetDirectoryName(inputFilename);
    reader.SetDataByteOrderToLittleEndian();

    //图像预处理  图像数据预处理，类型转换:通过 vtkimageCast 将不同类型数据集转化为 vtk 可以处理的数据集；
    vtkImageCast cast=new vtkImageCast();
    cast.SetInputConnection(reader1.GetOutputPort());
    cast.SetOutputScalarTypeToUnsignedShort();
    cast.Update();

    //透明度映射函数
    vtkPiecewiseFunction opacityTransform=new vtkPiecewiseFunction();

//    opacityTransform.AddPoint(0, 0.0);
//    opacityTransform.AddPoint(20, 0.0);
//    opacityTransform.AddPoint(200, 1.0);
//    opacityTransform.AddPoint(1000, 3.0);
    opacityTransform.AddPoint(-3024, 0, 0.5, 0.0);
    opacityTransform.AddPoint(-155, 0, 0.5, 0.92);
    opacityTransform.AddPoint(217, .68, 0.33, 0.45);
    opacityTransform.AddPoint(420, .83, 0.5, 0.0);
    opacityTransform.AddPoint(3071, .80, 0.5, 0.0);



//    vtkPiecewiseFunction volumeGradientOpacity = new vtkPiecewiseFunction();
////    volumeGradientOpacity.AddPoint(10, 0.0);
////    volumeGradientOpacity.AddPoint(70, 0.3);
////    volumeGradientOpacity.AddPoint(90, 0.5);
////    volumeGradientOpacity.AddPoint(500, 1.0);
//    volumeGradientOpacity.AddPoint(0, 0.0);
//
//    volumeGradientOpacity.AddPoint(20, 2.0);
//    volumeGradientOpacity.AddPoint(200, 0.1);
//    volumeGradientOpacity.AddPoint(300, 0.1);





    //颜色映射函数定义,梯度上升的
    vtkColorTransferFunction colorTransferFunction = new vtkColorTransferFunction();
//    colorTransferFunction.AddRGBPoint(-3024, 0, 0, 0, 0.5, 0.0);
//    colorTransferFunction.AddRGBPoint(-155, .55, .25, .15, 0.5, .92);
//    colorTransferFunction.AddRGBPoint(217, .88, .60, .29, 0.33, 0.45);
//    colorTransferFunction.AddRGBPoint(420, 1, .94, .95, 0.5, 0.0);
//    colorTransferFunction.AddRGBPoint(3071, .83, .66, 1, 0.5, 0.0);
//
    colorTransferFunction.AddRGBPoint(0.0, 0.0, 0.0, 0.0);
    colorTransferFunction.AddRGBPoint(64.0, 0.0, 0.0, 0.0);
    colorTransferFunction.AddRGBPoint(128.0, 1.0, 0.0, 0.0);
    colorTransferFunction.AddRGBPoint(192.0, 1.0, 0.0, 0.0);
    colorTransferFunction.AddRGBPoint(255.0, 1.0, 0.0, 0.0);


    vtkPiecewiseFunction gradientTransform=new vtkPiecewiseFunction();
    gradientTransform.AddPoint(0, 0.0);
    gradientTransform.AddPoint(20, 0.4);
    gradientTransform.AddPoint(200, 0.6);
    gradientTransform.AddPoint(300, 0.8);

//      gradientTransform.AddPoint(10,0.0);
//    gradientTransform.AddPoint(90,0.5);
//    gradientTransform.AddPoint(100,1.0);

//    gradientTransform.AddPoint(0, 0.0);
//
//    gradientTransform.AddPoint(20, 0.4);
//    gradientTransform.AddPoint(200, 0.6);
//    gradientTransform.AddPoint(300, 0.8);


    //体数据属性
    vtkVolumeProperty volumeProperty=new vtkVolumeProperty();
    volumeProperty.SetColor(colorTransferFunction);
    volumeProperty.SetScalarOpacity(opacityTransform);
    volumeProperty.SetGradientOpacity(gradientTransform);
    volumeProperty.ShadeOn();
    volumeProperty.SetInterpolationTypeToLinear();//直线间样条插值；
    volumeProperty.SetAmbient(0.1);//环境光系数；
    volumeProperty.SetDiffuse(0.9);//漫反射；
    volumeProperty.SetSpecular(0.2);
    volumeProperty.SetSpecularPower(10);//高光强度；

    //新添加
    vtkVolumeRayCastMapper volumeMapper=new vtkVolumeRayCastMapper();
//    vtkVolumeRayCastCompositeFunction compositeFunction=new vtkVolumeRayCastCompositeFunction();

    //光纤映射类型定义
    vtkVolumeRayCastCompositeFunction compositeFunction=new vtkVolumeRayCastCompositeFunction();
    vtkVolumeRayCastIsosurfaceFunction function=new vtkVolumeRayCastIsosurfaceFunction();
    function.SetIsoValue(1200);

    //Mapper定义
    vtkGPUVolumeRayCastMapper hiresMapper=new vtkGPUVolumeRayCastMapper();

    hiresMapper.SetInputData(cast.GetOutput());
    hiresMapper.SetBlendModeToComposite();

    vtkLODProp3D prop=new vtkLODProp3D();
    prop.AddLOD(hiresMapper,volumeProperty,0.0);

    //新添加
    volumeMapper.SetVolumeRayCastFunction(function);
    volumeMapper.SetInputConnection(cast.GetOutputPort());
    vtkFixedPointVolumeRayCastMapper fixedPointVolumeMapper = new vtkFixedPointVolumeRayCastMapper();
    fixedPointVolumeMapper.GetDataSetInput();



    vtkVolume volume=new vtkVolume();
    volume.SetMapper(hiresMapper);
    volume.SetProperty(volumeProperty);//设置体属性

    double[] volumeView={0,0,0.5,1};



    vtkOutlineFilter outlineData=new vtkOutlineFilter();
    outlineData.SetInputConnection(reader.GetOutputPort());
    vtkPolyDataMapper mapOutline=new vtkPolyDataMapper();
    mapOutline.SetInputConnection(outlineData.GetOutputPort());
    vtkActor outline=new vtkActor();
    outline.SetMapper(mapOutline);
    outline.GetProperty().SetColor(0,0,0);

    aRenderer.AddVolume(volume);
    aRenderer.AddActor(outline);
    aRenderer.SetBackground(1,1,1);
    aRenderer.ResetCamera();

    aRenderer.ResetCameraClippingRange();
    renWin.SetSize(800,800);
    renWin.SetWindowName("测试");

    vtkRenderWindowInteractor iren2=new vtkRenderWindowInteractor();
    iren2.SetRenderWindow(renWin);
//
//      vtkXMLPolyDataWriter writer = new vtkXMLPolyDataWriter();
//      writer.SetFileName("D:\\Test\\obj\\test2");
//      writer.SetInputConnection(mapOutline.GetOutputPort());
//      writer.Write();
    vtkInteractorStyleTrackballCamera style=new vtkInteractorStyleTrackballCamera();
    iren2.SetInteractorStyle(style);
    renWin.AddRenderer(aRenderer);
    renWin.Render();
//vtkDataObject dataObject=new vtkImageData();
//dataObject.Set
//
//    vtkExporter exporter=new vtkExporter();
//
//    exporter.SetInput(renWin);
//    exporter.Write();

    vtkOBJExporter porter =new vtkOBJExporter();

    porter.SetFilePrefix("D:\\Test\\obj\\test");
    porter.SetInput(renWin);
    porter.Write();
//    vtkSTLWriter writer=new vtkSTLWriter();
//    writer.SetFileName("D:\\Test\\obj");
//
//    writer.SetInputConnection(mapOutline.GetOutputPort());
//    writer.Write();

    iren2.Initialize();

    iren2.Start();





  }
}
