import lombok.Data;
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
    public MCRebuild(String inputfilename,String outputfilename){

        this.inputfilename=inputfilename;
        this.outputfilename=outputfilename;
}
    public  void restructing() {
        long startTime=System.currentTimeMillis();
        vtkObjectFactory factory=new vtkObjectFactory();
        factory.RegisterFactory(new vtkRenderingGL2PSOpenGL2ObjectFactory());

        vtkRenderer ren=new vtkRenderer();
        vtkRenderWindow renWin=new vtkRenderWindow();
        renWin.AddRenderer(ren);
        vtkRenderWindowInteractor iren=new vtkRenderWindowInteractor();
        iren.SetRenderWindow(renWin);
        vtkDICOMImageReader reader = new vtkDICOMImageReader();
        vtkMetaImageReader reader1=new vtkMetaImageReader();
        vtkNIFTIImageReader reader2 =new vtkNIFTIImageReader();
        reader1.SetFileName("D:\\img\\omhasec.mha");
        reader1.SetDataByteOrderToLittleEndian();
        reader2.SetFileName("D:\\Wechat\\download\\WeChat Files\\wxid_chfigqjad7dv12\\FileStorage\\File\\2023-02\\segmentation-0.nii");
        reader2.SetDataByteOrderToLittleEndian();

        reader.SetDirectoryName(inputfilename);
        System.out.println("inputfile"+inputfilename);
//        D:\test1\test\H0002\2020-06\08\H0002CT000556\1.2.392.200036.9116.2.5.1.48.1224104417.1313478430.414393\1.2.392.200036.9116.2.1224104417.1313478696.2.1010300001.1
        reader.SetDataByteOrderToLittleEndian();
        reader.Update();

        vtkMarchingCubes marchingcube=new vtkMarchingCubes();
        marchingcube.SetInputConnection(reader1.GetOutputPort());
        marchingcube.SetValue(0,300);
        marchingcube.ComputeNormalsOn();

        vtkStripper Stripper=new vtkStripper();
        Stripper.SetInputConnection(marchingcube.GetOutputPort());

        vtkPolyDataMapper Mapper = new vtkPolyDataMapper();//将三角片映射为几何数据；
        Mapper.SetInputConnection(Stripper.GetOutputPort());
        Mapper.ScalarVisibilityOff();

        vtkActor actor = new vtkActor();//Created a actor;
        actor.SetMapper(Mapper);//获得皮肤几何数据
        actor.GetProperty().SetDiffuseColor(1, .49, .25);//设置皮肤颜色；
        actor.GetProperty().SetSpecular(0.1);//反射率；
        actor.GetProperty().SetOpacity(10.0);//透明度；
        actor.GetProperty().SetSpecularPower(50);//反射光强度；
      //  actor.GetProperty().SetColor(0.78, 0.35, 0.38);//设置角的颜色；
       actor.GetProperty().SetColor(0.67, 0.20, 0.24);//设置角的颜色；
//        actor.GetProperty().SetRepresentationToWireframe();//线框；

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
       // OutlineActor.GetProperty().SetColor(, 0, 0);//线框颜色


        ren.AddActor(actor);
        ren.AddActor(OutlineActor);
        ren.SetActiveCamera(camera);//设置渲染器的相机；
        ren.ResetCamera();
        ren.ResetCameraClippingRange();

        camera.Dolly(1.5);//使用Dolly()方法延伸着视平面法向移动相机；
        ren.SetBackground(175, 183, 194);//设置背景颜色；
        renWin.SetSize(1200, 1200);


        vtkInteractorStyleTrackballCamera style = new vtkInteractorStyleTrackballCamera();
        iren.SetInteractorStyle(style);

        renWin.Render();
        long endTime=System.currentTimeMillis();
        System.out.println("当前程序耗时："+(endTime-startTime)+"ms");
        long sTime=System.currentTimeMillis();
        vtkOBJExporter porter =new vtkOBJExporter();
        porter.SetFilePrefix("D:/img/full");//重建图像输出"D:/Test/MCObj/polywrite2"
        porter.SetInput(renWin);
        porter.Write();
        long eTime=System.currentTimeMillis();
        System.out.println("当前程序耗时："+(eTime-sTime)+"ms");
        iren.Initialize();
        iren.Start();


//        return "EXIT_SUCCESS";

    }



}
