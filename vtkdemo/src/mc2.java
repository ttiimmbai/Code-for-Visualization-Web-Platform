import vtk.*;

public class mc2 {
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

    public static void main(String[] args) {

        vtkRenderer aRenderer = new vtkRenderer();
        vtkRenderWindow renWin =new  vtkRenderWindow();
        renWin.AddRenderer(aRenderer);
        vtkRenderWindowInteractor iren =new vtkRenderWindowInteractor();
        iren.SetRenderWindow(renWin);


        vtkDICOMImageReader v16 =new vtkDICOMImageReader();
        v16.SetDataByteOrderToLittleEndian();
        v16.SetDirectoryName("D:\\\\test1\\\\test\\\\H0002\\\\2020-06\\\\08\\\\H0002CT000556\\\\1.2.392.200036.9116.2.5.1.48.1224104417.1313478430.414393\\\\1.2.392.200036.9116.2.1224104417.1313478696.2.1010300001.1");
        v16.SetDataSpacing (3.2, 3.2, 1.5);


        vtkContourFilter skinExtractor =new vtkContourFilter();
        skinExtractor.SetInputConnection(v16.GetOutputPort());
        skinExtractor.SetValue(500, 1200);
        vtkPolyDataNormals skinNormals =new vtkPolyDataNormals();
        skinNormals.SetInputConnection(skinExtractor.GetOutputPort());
        skinNormals.SetFeatureAngle(60.0);
        vtkPolyDataMapper skinMapper =new vtkPolyDataMapper();
        skinMapper.SetInputConnection(skinNormals.GetOutputPort());
        skinMapper.ScalarVisibilityOff();
        vtkActor skin =new vtkActor();
        skin.SetMapper(skinMapper);


        vtkOutlineFilter outlineData = new vtkOutlineFilter();
        outlineData.SetInputConnection(v16.GetOutputPort());
        vtkPolyDataMapper mapOutline =new vtkPolyDataMapper();
        mapOutline.SetInputConnection(outlineData.GetOutputPort());
        vtkActor outline =new vtkActor();
        outline.SetMapper(mapOutline);
        outline.GetProperty().SetColor(0,0,0);


        vtkCamera aCamera =new vtkCamera();
        aCamera.SetViewUp (0, 0, -1);
        aCamera.SetPosition (0, 1, 0);
        aCamera.SetFocalPoint (0, 0, 0);
        aCamera.ComputeViewPlaneNormal();


        aRenderer.AddActor(outline);
        aRenderer.AddActor(skin);
        aRenderer.SetActiveCamera(aCamera);
        aRenderer.ResetCamera ();
        aCamera.Dolly(1.5);


        aRenderer.SetBackground(1,1,1);
        renWin.SetSize(640, 480);


        aRenderer.ResetCameraClippingRange ();

        // Initialize the event loop and then start it.
        iren.Initialize();
        iren.Start();

        v16.Delete();
        skinExtractor.Delete();
        skinNormals.Delete();
        skinMapper.Delete();
        skin.Delete();
        outlineData.Delete();
        mapOutline.Delete();
        outline.Delete();
        aCamera.Delete();
        iren.Delete();
        renWin.Delete();
        aRenderer.Delete();

//        vtkDICOMImageReader reader = new vtkDICOMImageReader();
//        reader.SetDataByteOrderToLittleEndian();
//        reader.SetDirectoryName("D:\\test1\\test\\H0002\\2020-06\\08\\H0002CT000556\\1.2.392.200036.9116.2.5.1.48.1224104417.1313478430.414393\\1.2.392.200036.9116.2.1224104417.1313478696.2.1010300001.1");
//        vtkImageShrink3D shrink3D=new vtkImageShrink3D();
//        shrink3D.SetShrinkFactors(4,4,1);
//        shrink3D.AveragingOn();
//        shrink3D.SetInputData(reader.GetOutput());
//        vtkMarchingCubes marchingcube=new vtkMarchingCubes();
//       marchingcube.SetValue(0,300);
//       marchingcube.SetInputConnection(shrink3D.GetOutputPort());
//       vtkDecimatePro decimatePro=new vtkDecimatePro();
//       decimatePro.SetTargetReduction(0.3);
//       decimatePro.SetInputConnection(shrink3D.GetOutputPort());
//        vtkSmoothPolyDataFilter smooth=new vtkSmoothPolyDataFilter();
//        smooth.SetInputConnection(decimatePro.GetOutputPort());
//        smooth.SetNumberOfIterations(200);
//        vtkPolyDataNormals skinNormals=new vtkPolyDataNormals();
//        skinNormals.SetInputConnection(smooth.GetOutputPort());
//        skinNormals.SetFeatureAngle(60.0);
//        vtkStripper stripper=new vtkStripper();
//        stripper.SetInputConnection(skinNormals.GetOutputPort());
//        vtkPolyDataMapper skinMapper =new vtkPolyDataMapper();  //将几何数据映射成图像数据
//
//        skinMapper.SetInputDataObject(stripper.GetOutput());
//        skinMapper.ScalarVisibilityOff();
//
//
//        vtkCamera aCamera =new vtkCamera();
//
//        aCamera.SetViewUp (0, 0, -1);
//
//        aCamera.SetPosition (0, 1, 0);
//
//        aCamera.SetFocalPoint (0, 0, 0);
//
//        aCamera.ComputeViewPlaneNormal();
//
////设置Actor相关系数
//
//        vtkActor coneActor = new vtkActor();
//
//        coneActor.SetMapper(skinMapper);
//
//        coneActor.GetProperty().SetAmbient(0.5);
//
//        coneActor.GetProperty().SetDiffuse(1);
//
//        coneActor.GetProperty().SetSpecular(0.6);
//
////显示类
//
//        vtkRenderer renderer =new vtkRenderer();
//
//        renderer.AddActor(coneActor);//添加coneActor对象
//
////        renderer.AddActor2D(textActor);//添加textActor对象
//
//        renderer.SetBackground(1,1,1);
//
//        renderer.SetActiveCamera(aCamera);//添加照相机
//
//        renderer.ResetCamera ();
//
//        vtkRenderWindow renWin =new vtkRenderWindow();//
//         renWin.AddRenderer(renderer);//装载绘图类  设置绘图窗口
//
//        vtkWin32RenderWindowInteractor iren =new vtkWin32RenderWindowInteractor();//设置绘图窗口交互
//        vtkInteractorStyleTrackballCamera style = new vtkInteractorStyleTrackballCamera();
//        iren.SetInteractorStyle(style);
//        iren.SetRenderWindow(renWin);//装载绘图窗口
//        renWin.SetSize(1000, 600);
//        renWin.Render();
//        iren.Initialize();
//      iren.Start();
//
//        System.out.println("inpu
//        tfile"+inputfilename);
//        D:\test1\test\H0002\2020-06\08\H0002CT000556\1.2.392.200036.9116.2.5.1.48.1224104417.1313478430.414393\1.2.392.200036.9116.2.1224104417.1313478696.2.1010300001.1

    }
    public  void restructing() {
//        vtkObjectFactory factory=new vtkObjectFactory();
//        factory.RegisterFactory(new vtkRenderingGL2PSOpenGL2ObjectFactory());
//
//        vtkRenderer ren=new vtkRenderer();
//        vtkRenderWindow renWin=new vtkRenderWindow();
//        renWin.AddRenderer(ren);
//        vtkRenderWindowInteractor iren=new vtkRenderWindowInteractor();
//        iren.SetRenderWindow(renWin);
//        vtkDICOMImageReader reader = new vtkDICOMImageReader();
//        reader.SetDirectoryName(inputfilename);
//        System.out.println("inputfile"+inputfilename);
////        D:\test1\test\H0002\2020-06\08\H0002CT000556\1.2.392.200036.9116.2.5.1.48.1224104417.1313478430.414393\1.2.392.200036.9116.2.1224104417.1313478696.2.1010300001.1
//        reader.SetDataByteOrderToLittleEndian();
//        reader.Update();
//
//        vtkMarchingCubes marchingcube=new vtkMarchingCubes();
//        marchingcube.SetInputConnection(reader.GetOutputPort());
//        marchingcube.SetValue(0,100);
//        marchingcube.ComputeNormalsOn();
//
//        vtkStripper Stripper=new vtkStripper();
//        Stripper.SetInputConnection(marchingcube.GetOutputPort());
//
//        vtkPolyDataMapper Mapper = new vtkPolyDataMapper();//将三角片映射为几何数据；
//        Mapper.SetInputConnection(Stripper.GetOutputPort());
//        Mapper.ScalarVisibilityOff();
//
//        vtkActor actor = new vtkActor();//Created a actor;
//        actor.SetMapper(Mapper);//获得皮肤几何数据
//        actor.GetProperty().SetDiffuseColor(1, .49, .25);//设置皮肤颜色；
//        actor.GetProperty().SetSpecular(0.3);//反射率；
//        actor.GetProperty().SetOpacity(1.0);//透明度；
//        actor.GetProperty().SetSpecularPower(20);//反射光强度；
//        actor.GetProperty().SetColor(1, 0, 0);//设置角的颜色；
//        actor.GetProperty().SetRepresentationToWireframe();//线框；
//
//        //@Todo camera
//        vtkCamera camera = new vtkCamera();//Setting the Camera;
//        camera.SetViewUp(0, 0, -1);//设置相机向上方向；
//        camera.SetPosition(0, 1, 0);//位置：世界坐标系，相机位置；
//        camera.SetFocalPoint(0, 0, 0);//焦点，世界坐标系，控制相机方向；
//        camera.ComputeViewPlaneNormal();//重置视平面方向，基于当前的位置和焦点；
//
//
//
//        vtkOutlineFilter outfilterline = new vtkOutlineFilter();
//        outfilterline.SetInputConnection(reader.GetOutputPort());
//        vtkPolyDataMapper outmapper = new vtkPolyDataMapper();
//        outmapper.SetInputConnection(outfilterline.GetOutputPort());
//        vtkActor OutlineActor = new vtkActor();
//        OutlineActor.SetMapper(outmapper);
//        OutlineActor.GetProperty().SetColor(0, 0, 0);//线框颜色
//
//
//        ren.AddActor(actor);
//        ren.AddActor(OutlineActor);
//        ren.SetActiveCamera(camera);//设置渲染器的相机；
//        ren.ResetCamera();
//        ren.ResetCameraClippingRange();
//
//        camera.Dolly(1.5);//使用Dolly()方法延伸着视平面法向移动相机；
//        ren.SetBackground(1, 1, 1);//设置背景颜色；
//        renWin.SetSize(1000, 600);
//
//
//        vtkInteractorStyleTrackballCamera style = new vtkInteractorStyleTrackballCamera();
//        iren.SetInteractorStyle(style);
//
//        renWin.Render();
//
//
////        vtkOBJExporter porter =new vtkOBJExporter();
////        porter.SetFilePrefix(outputfilename);//重建图像输出"D:/Test/MCObj/polywrite2"
////        porter.SetInput(renWin);
////        porter.Write();
//        iren.Initialize();
//        iren.Start();
//
////        return "EXIT_SUCCESS";

    }


}
