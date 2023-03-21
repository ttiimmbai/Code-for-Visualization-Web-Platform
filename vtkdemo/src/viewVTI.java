import vtk.vtkNativeLibrary;
import vtk.*;
public class viewVTI {
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
        vtkXMLImageDataReader reader = new vtkXMLImageDataReader();
        reader.SetFileName("D:\\img\\level_sets.mha");
        reader.Update();
        //转换数据类型
        vtkImageCast readercast=new vtkImageCast();
        readercast.SetInputConnection(reader.GetOutputPort());
        readercast.SetOutputScalarTypeToUnsignedChar();//转换为图像数据
        readercast.ClampOverflowOn();//默认情况下，该变量值为0；当设置为1时，输出的像素值不能超过输出类型的最大值。
        readercast.Update();

        // 可视化
       vtkDataSetMapper mapper =new vtkDataSetMapper();

        mapper.SetInputConnection(readercast.GetOutputPort());
        //渲染
       vtkActor actor = new vtkActor();

        actor.SetMapper(mapper);
        actor.GetProperty().SetRepresentationToWireframe();

        vtkRenderer renderer = new vtkRenderer();

        renderer.AddActor(actor);
        renderer.ResetCamera();
        renderer.SetBackground(1, 1, 1);

        vtkRenderWindow renderWindow = new  vtkRenderWindow();

        renderWindow.AddRenderer(renderer);

        vtkRenderWindowInteractor renderWindowInteractor = new vtkRenderWindowInteractor();

        renderWindowInteractor.SetRenderWindow(renderWindow);
        renderWindowInteractor.Initialize();
        renderWindowInteractor.Start();


    }

}
