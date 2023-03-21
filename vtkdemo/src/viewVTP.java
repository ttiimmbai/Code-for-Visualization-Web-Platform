import vtk.vtkNativeLibrary;
import vtk.*;

public class viewVTP {
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
        vtkXMLPolyDataReader reader=new vtkXMLPolyDataReader();
        reader.SetFileName("D:\\img\\mc_surface.vtp");
        reader.Update();
        vtkPolyDataMapper mapper=new vtkPolyDataMapper();
        mapper.SetInputConnection(reader.GetOutputPort());
        vtkActor actor=new vtkActor();
        actor.SetMapper(mapper);
        vtkRenderer renderer=new vtkRenderer();
        vtkRenderWindow renderWindow=new vtkRenderWindow();
        renderWindow.AddRenderer(renderer);
        vtkRenderWindowInteractor renderWindowInteractor=new vtkRenderWindowInteractor();
        renderWindowInteractor.SetRenderWindow(renderWindow);
        renderer.AddActor(actor);
        renderer.SetBackground(.3, .6, .3);
        renderWindow.Render();
        renderWindowInteractor.Start();

    }
}
