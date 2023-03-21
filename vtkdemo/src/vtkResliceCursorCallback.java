
import vtk.*;

import java.applet.Applet;

public class vtkResliceCursorCallback extends Applet {

    public static vtkResliceCursorCallback New() {
        return new vtkResliceCursorCallback();

    }
    vtkImagePlaneWidget IPW[]=new vtkImagePlaneWidget[3];
    vtkResliceCursorWidget RCW[]=new vtkResliceCursorWidget[3];

    public void Execute(vtkObject caller,double[] callData) {
        vtkImagePlaneWidget ipw = (vtkImagePlaneWidget) caller;
        if (ipw!=null){
            double[] wl=callData;
            if (ipw==this.IPW[0]){
                this.IPW[1].SetWindowLevel(wl[0], wl[1], 1);
                this.IPW[2].SetWindowLevel(wl[0], wl[1], 1);
            }else if (ipw==this.IPW[1]){
                this.IPW[0].SetWindowLevel(wl[0], wl[1], 1);
                this.IPW[2].SetWindowLevel(wl[0], wl[1], 1);
            }else if (ipw==this.IPW[2]){
                this.IPW[0].SetWindowLevel(wl[0], wl[1], 1);
                this.IPW[1].SetWindowLevel(wl[0], wl[1], 1);
            }
        }

        vtkResliceCursorWidget rcw= (vtkResliceCursorWidget) caller;
        if (rcw!=null){
            vtkResliceCursorLineRepresentation rep= (vtkResliceCursorLineRepresentation) rcw.GetResliceCursorRepresentation();
            vtkResliceCursor rc=rep.GetResliceCursorActor().GetCursorAlgorithm().GetResliceCursor();
            for (int i = 0; i < 3; i++){
                vtkPlaneSource ps= (vtkPlaneSource) this.IPW[i].GetPolyDataAlgorithm();
                ps.SetNormal(rc.GetPlane(i).GetNormal());
                ps.SetCenter(rc.GetPlane(i).GetOrigin());
                this.IPW[i].UpdatePlacement();
            }
        }
            this.RCW[0].Render();
    }
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
        vtkDICOMImageReader reader=new vtkDICOMImageReader();
        reader.SetDirectoryName("D:\\Test\\TOOTHDICOM");
        reader.Update();

        vtkPolyDataMapper outlineMapper=new vtkPolyDataMapper();
        outlineMapper.SetInputConnection(reader.GetOutputPort());
        vtkActor outlineActor=new vtkActor();
        outlineActor.SetMapper(outlineMapper);

        vtkRenderer[] ren=new vtkRenderer[4];
        vtkRenderWindow renWin=new vtkRenderWindow();
        renWin.SetMultiSamples(0);
        for (int i = 0; i < 4; i++)
        {
            ren[i] = new vtkRenderer();
            renWin.AddRenderer(ren[i]);
        }

        vtkRenderWindowInteractor iren=new vtkRenderWindowInteractor();
        iren.SetRenderWindow(renWin);

        vtkCellPicker picker=new vtkCellPicker();
        picker.SetTolerance(0.005);

        vtkProperty ipwProp=new vtkProperty();
        vtkImagePlaneWidget[] planeWidget=new vtkImagePlaneWidget[3];
        int[] imageDims=new int[3];

        imageDims= reader.GetOutput().GetDimensions();

        for (int i = 0; i < 3; i++)
        {
            planeWidget[i] = new vtkImagePlaneWidget();
            planeWidget[i].SetInteractor(iren);
            planeWidget[i].SetPicker(picker);
            planeWidget[i].RestrictPlaneToVolumeOn();
            double[] color = { 0, 0, 0 };
            color[i] = 1;
            planeWidget[i].GetPlaneProperty().SetColor(color);
            planeWidget[i].SetTexturePlaneProperty(ipwProp);
            planeWidget[i].TextureInterpolateOff();
            planeWidget[i].SetResliceInterpolateToLinear();
            planeWidget[i].SetInputConnection(reader.GetOutputPort());
            planeWidget[i].SetPlaneOrientation(i);
            planeWidget[i].SetSliceIndex(imageDims[i] / 2);
            planeWidget[i].DisplayTextOn();
            planeWidget[i].SetDefaultRenderer(ren[3]);
            planeWidget[i].SetWindowLevel(1358, -27,0);  //TODO
            planeWidget[i].On();
            planeWidget[i].InteractionOn();
        }

        planeWidget[1].SetLookupTable(planeWidget[0].GetLookupTable());
        planeWidget[2].SetLookupTable(planeWidget[0].GetLookupTable());

        vtkResliceCursorCallback cbk=new vtkResliceCursorCallback();
        vtkResliceCursor resliceCursor=new vtkResliceCursor();
        resliceCursor.SetCenter(reader.GetOutput().GetCenter());
        resliceCursor.SetThickMode(0);
        resliceCursor.SetThickness(10, 10, 10);
        resliceCursor.SetImage(reader.GetOutput());

        vtkResliceCursorWidget[] resliceCursorWidget=new vtkResliceCursorWidget[3];
        vtkResliceCursorLineRepresentation[] resliceCursorRep=new vtkResliceCursorLineRepresentation[3];
        double[][] viewUp = { { 0, 0, -1 }, { 0, 0, 1 }, { 0, 1, 0 } };

        for (int i = 0; i < 3; i++)
        {
            resliceCursorWidget[i] = new vtkResliceCursorWidget();
            resliceCursorWidget[i].SetInteractor(iren);

            resliceCursorRep[i] = new vtkResliceCursorLineRepresentation();
            resliceCursorWidget[i].SetRepresentation(resliceCursorRep[i]);
            resliceCursorRep[i].GetResliceCursorActor().GetCursorAlgorithm().SetResliceCursor(resliceCursor);
            resliceCursorRep[i].GetResliceCursorActor().GetCursorAlgorithm().SetReslicePlaneNormal(i);

         double minVal = reader.GetOutput().GetScalarRange()[0];
         vtkImageReslice reslice = (vtkImageReslice) resliceCursorRep[i].GetReslice();
            if (reslice!=null);
            {
                reslice.SetBackgroundColor(minVal, minVal, minVal, minVal);
            }

            resliceCursorWidget[i].SetDefaultRenderer(ren[i]);
            resliceCursorWidget[i].SetEnabled(1);

            ren[i].GetActiveCamera().SetFocalPoint(0, 0, 0);
            double[] camPos = { 0, 0, 0 };
            camPos[i] = 1;
            ren[i].GetActiveCamera().SetPosition(camPos);
            ren[i].GetActiveCamera().ParallelProjectionOn();
            ren[i].GetActiveCamera().SetViewUp(viewUp[i][0], viewUp[i][1], viewUp[i][2]);
            ren[i].ResetCamera();
            cbk.IPW[i] = planeWidget[i];
            cbk.RCW[i] = resliceCursorWidget[i];
//
            double[] range;
            range=  reader.GetOutput().GetScalarRange();
            resliceCursorRep[i].SetWindowLevel(range[1] - range[0], (range[0] + range[1]) / 2.0,0);
            planeWidget[i].SetWindowLevel(range[1] - range[0], (range[0] + range[1]) / 2.0,0);
            resliceCursorRep[i].SetLookupTable(resliceCursorRep[0].GetLookupTable());
            planeWidget[i].GetColorMap().SetLookupTable(resliceCursorRep[0].GetLookupTable());
        }


        ren[0].SetBackground(0.3, 0.1, 0.1);
        ren[1].SetBackground(0.1, 0.3, 0.1);
        ren[2].SetBackground(0.1, 0.1, 0.3);
        ren[3].AddActor(outlineActor);
        ren[3].SetBackground(0.1, 0.1, 0.1);
        renWin.SetSize(600, 600);

        ren[0].SetViewport(0, 0, 0.5, 0.5);
        ren[1].SetViewport(0.5, 0, 1, 0.5);
        ren[2].SetViewport(0, 0.5, 0.5, 1);
        ren[3].SetViewport(0.5, 0.5, 1, 1);
        renWin.Render();

        ren[3].GetActiveCamera().Elevation(110);
        ren[3].GetActiveCamera().SetViewUp(0, 0, -1);
        ren[3].GetActiveCamera().Azimuth(45);
        ren[3].GetActiveCamera().Dolly(1.15);
        ren[3].ResetCameraClippingRange();

        vtkOBJExporter porter=new vtkOBJExporter(); 
        porter.SetFilePrefix("D:\\Test\\obj\\test");
        porter.SetInput(renWin);
        porter.Write();
     vtkInteractorStyleImage style = new vtkInteractorStyleImage();
        iren.SetInteractorStyle(style);

        iren.Initialize();
        iren.Start();

    }




    }
