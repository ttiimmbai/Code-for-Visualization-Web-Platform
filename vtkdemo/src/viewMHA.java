import vtk.*;

public class viewMHA {
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
     vtkMetaImageReader reader=new vtkMetaImageReader();
        reader.SetFileName ("D:\\img\\H0002\\2018-10\\20\\RDR000012\\adjustzongge.mha");
        reader.Update();

        vtkImageViewer2 viewer = new vtkImageViewer2();
        viewer.SetInputConnection(reader.GetOutputPort());

        // render window
        vtkRenderWindowInteractor renderWindowInteractor =new vtkRenderWindowInteractor();

        viewer.SetupInteractor(renderWindowInteractor);

        viewer.SetSlice(160);
        viewer.SetSliceOrientationToXY();

        //横断面、矢状面、冠状面

        //SetSliceOrientationToXY()、SetSliceOrientationToXZ()、SetSliceOrientationToYZ()

        viewer.SetSliceOrientationToXY();//SetSliceOrientationToXZ();SetSliceOrientationToYZ()

        viewer.Render();

        renderWindowInteractor.Initialize();
        renderWindowInteractor.Start();
    }
}
