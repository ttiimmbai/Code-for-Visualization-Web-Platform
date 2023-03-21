import vtk.*;


public class ReadDICOM
{
    // -----------------------------------------------------------------
    // Load VTK library and print which library was not properly loaded
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
    // -----------------------------------------------------------------

    public static void main(String args[])
    {

        //parse command line arguments
//        if (args.length != 1)
//        {
//            System.err.println("Usage: java -classpath ... Filename(.img) e.g prostate.img");
//            return;
//        }
        String inputFilename ="D:\\test1\\test\\H0002\\2019-11\\12\\H0002CT000545\\1.2.392.200036.9116.2.5.1.48.1224104417.1313478430.414393\\1.2.392.200036.9116.2.1224104417.1313478696.2.1010300001.1\\1.2.392.200036.9116.2.1224104417.1313478696.980053.1.61.dcm";

        vtkDICOMImageReader reader = new vtkDICOMImageReader();
        reader.SetFileName(inputFilename);
        reader.Update();
        // Visualize
        vtkImageViewer2 imageViewer = new vtkImageViewer2();
        imageViewer.SetInputConnection(reader.GetOutputPort());
        vtkRenderWindowInteractor renderWindowInteractor =new vtkRenderWindowInteractor();
        imageViewer.SetupInteractor(renderWindowInteractor);
        imageViewer.Render();
        imageViewer.GetRenderer().ResetCamera();
        imageViewer.Render();

        renderWindowInteractor.Start();

    }
}
