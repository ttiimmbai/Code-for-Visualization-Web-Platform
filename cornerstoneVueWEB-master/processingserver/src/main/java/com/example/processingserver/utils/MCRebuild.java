package com.example.processingserver.utils;

import lombok.Data;
import org.springframework.stereotype.Component;
import vtk.*;

@Data

public class MCRebuild {

    private String inputfilename;
    private String outputfilename;
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
    public MCRebuild(String inputfilename, String outputfilename){

        this.inputfilename=inputfilename;
        this.outputfilename=outputfilename;
}
    public  void restructing() {
        vtkObjectFactory factory=new vtkObjectFactory();
        factory.RegisterFactory(new vtkRenderingGL2PSOpenGL2ObjectFactory());

        vtkRenderer ren=new vtkRenderer();
        vtkRenderWindow renWin=new vtkRenderWindow();
        renWin.AddRenderer(ren);
//        vtkRenderWindowInteractor iren=new vtkRenderWindowInteractor();
//        iren.SetRenderWindow(renWin);
        vtkDICOMImageReader reader = new vtkDICOMImageReader();
        reader.SetDirectoryName(inputfilename);
        System.out.println("inputfile"+inputfilename);
//        D:\test1\test\H0002\2020-06\08\H0002CT000556\1.2.392.200036.9116.2.5.1.48.1224104417.1313478430.414393\1.2.392.200036.9116.2.1224104417.1313478696.2.1010300001.1
        reader.SetDataByteOrderToLittleEndian();
        reader.Update();

        vtkMarchingCubes marchingcube=new vtkMarchingCubes();
        marchingcube.SetInputConnection(reader.GetOutputPort());
        marchingcube.SetValue(0,200);
        marchingcube.ComputeNormalsOn();

        vtkStripper Stripper=new vtkStripper();
        Stripper.SetInputConnection(marchingcube.GetOutputPort());

        vtkPolyDataMapper Mapper = new vtkPolyDataMapper();//将三角片映射为几何数据；
        Mapper.SetInputConnection(Stripper.GetOutputPort());
        Mapper.ScalarVisibilityOff();

        vtkActor actor = new vtkActor();//Created a actor;
        actor.SetMapper(Mapper);//获得皮肤几何数据
        actor.GetProperty().SetDiffuseColor(1, .49, .25);//设置皮肤颜色；
        actor.GetProperty().SetSpecular(0.3);//反射率；
        actor.GetProperty().SetOpacity(1.0);//透明度；
        actor.GetProperty().SetSpecularPower(20);//反射光强度；
        actor.GetProperty().SetColor(0.87, 0.68, 0.44);//设置角的颜色；
        actor.GetProperty().SetRepresentationToWireframe();//线框；

        //@Todo camera
        vtkCamera camera = new vtkCamera();//Setting the Camera;
        camera.SetViewUp(0, 0, -1);//设置相机向上方向；
        camera.SetPosition(0, 1, 0);//位置：世界坐标系，相机位置；
        camera.SetFocalPoint(0, 0, 0);//焦点，世界坐标系，控制相机方向；
        camera.ComputeViewPlaneNormal();//重置视平面方向，基于当前的位置和焦点；



        vtkOutlineFilter outfilterline = new vtkOutlineFilter();
        outfilterline.SetInputConnection(reader.GetOutputPort());
        vtkPolyDataMapper outmapper = new vtkPolyDataMapper();
        outmapper.SetInputConnection(outfilterline.GetOutputPort());
        vtkActor OutlineActor = new vtkActor();
        OutlineActor.SetMapper(outmapper);
        OutlineActor.GetProperty().SetColor(0, 0, 0);//线框颜色


        ren.AddActor(actor);
        ren.AddActor(OutlineActor);
        //ren->SetActiveCamera(camera);//设置渲染器的相机；
        ren.ResetCamera();
        ren.ResetCameraClippingRange();

        //camera->Dolly(1.5);//使用Dolly()方法延伸着视平面法向移动相机；
        ren.SetBackground(1, 1, 1);//设置背景颜色；
        renWin.SetSize(1000, 600);


        vtkInteractorStyleTrackballCamera style = new vtkInteractorStyleTrackballCamera();
//        iren.SetInteractorStyle(style);

//       renWin.Render();


        vtkOBJExporter porter =new vtkOBJExporter();
        porter.SetFilePrefix(outputfilename);//重建图像输出"D:/Test/MCObj/res/polywrite2"
        porter.SetInput(renWin);
        porter.Write();
//        iren.Initialize();
//        iren.Start();

//        return "EXIT_SUCCESS";

    }

    public static void main(String[] args) {

    }



}
