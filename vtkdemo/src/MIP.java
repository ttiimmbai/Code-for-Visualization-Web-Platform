import vtk.vtkNativeLibrary;
import vtk.*;
public class MIP {
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
       vtkDICOMImageReader reader =new vtkDICOMImageReader();
        reader.SetDirectoryName("D:\\img\\H0002\\2018-10\\20\\RDR000012\\1.3.6.1.4.1.14519.5.2.1.6279.6001.225213110794629789874295007045\\1.3.6.1.4.1.14519.5.2.1.6279.6001.286061375572911414226912429210");
        reader.Update();
        vtkMetaImageReader reader1=new vtkMetaImageReader();
        reader1.SetFileName("D:\\img\\H0002\\2021-10\\20\\RDR000003\\fin.mha");
        reader1.Update();

        int extent[]=new int[6];//维度
        extent=reader1.GetOutput().GetExtent();
        double spacing[]=new double[3];//间隔
        spacing=reader1.GetOutput().GetSpacing();
        double origin[]=new double[3];//原点
        origin= reader1.GetOutput().GetOrigin();
        //计算体数据中心点
        double center[]=new double[3];
        center[0] = origin[0] + spacing[0] * 0.5 * (extent[0] + extent[1]);
        center[1] = origin[1] + spacing[1] * 0.5 * (extent[2] + extent[3]);
        center[2] = origin[2] + spacing[2] * 0.5 * (extent[4] + extent[5]);

        //axial 横断面的矩阵
        double Matrix[]={
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1

        };
        double m_axialMatrix40[] = {
                1, 0, 0, 0,
                0, 0.77, -0.64, 0,
                0, 0.64, 0.77, 0,
                0, 0, 0, 1
        };
        double m_axialMatrix60[] = {
                1, 0, 0, 0,
                0, 0.5, -0.87, 0,
                0, 0.87, 0.5, 0,
                0, 0, 0, 1
        };

        double m_axialMatrix80[] = {
                1, 0, 0, 0,
                0, 0.17, -0.98, 0,
                0, 0.98, 0.17, 0,
                0, 0, 0, 1
        };

        double m_axialMatrix100[] = {
                1, 0, 0, 0,
                0, -0.17, -0.98, 0,
                0, 0.98, -0.17, 0,
                0, 0, 0, 1
        };
        double m_axialMatrix120[] = {
                1, 0, 0, 0,
                0, -0.5, -0.87, 0,
                0, 0.87, -0.5, 0,
                0, 0, 0, 1
        };

        double m_axialMatrix140[] = {
                1, 0, 0, 0,
                0, -0.77, -0.64, 0,
                0, 0.64, -0.77, 0,
                0, 0, 0, 1
        };

        double m_axialMatrix160[] = {
                1, 0, 0, 0,
                0, -0.94, -0.34, 0,
                0, 0.34, -0.94, 0,
                0, 0, 0, 1
        };
        double m_axialMatrixyoz[] = {
                1, 0, 0, 0,
                0, 0, 1, 0,
                0,-1, 0, 0,
                0, 0, 0, 1
        };
        double m_axialMatrixxoz[] = {
                0, 0,-1, 0,
                1, 0, 0, 0,
                0,-1, 0, 0,
                0, 0, 0, 1
        };
        vtkMatrix4x4 resliceAxes=new vtkMatrix4x4();
        resliceAxes.DeepCopy(m_axialMatrixyoz);
        resliceAxes.SetElement(0, 3, center[0]);
        resliceAxes.SetElement(1, 3, center[1]);
        resliceAxes.SetElement(2, 3, center[2]);
        //通过extent截取对应的体数据
        vtkExtractVOI extractVOI = new vtkExtractVOI();
        extractVOI.SetInputConnection(reader1.GetOutputPort());
        extractVOI.SetVOI(extent);
        extractVOI.Update();
        //mip
        vtkImageSlabReslice reslice =new vtkImageSlabReslice();
        reslice.SetInputConnection(extractVOI.GetOutputPort());//截取的体数据
        reslice.SetOutputDimensionality(2);//设置输出为2维图片
        reslice.SetInterpolationModeToCubic();//设置插值方式为最邻近插值
        reslice.SetBlendModeToMax();//设置为获取最大投影，其他方式还有均值和最小
        reslice.SetSlabThickness(extent[5]/32);//设置厚度
        reslice.SetResliceAxes(resliceAxes);//设置矩阵
        reslice.Update();

        vtkImageData imageData = reslice.GetOutput();
        vtkImageFlip flip = new vtkImageFlip();
        flip.SetInputData(imageData);
        flip.SetFilteredAxis(1);//y轴为1，x轴为0，z轴为2；
        flip.Update();


        int[] dims1=new int[3];
        imageData.GetDimensions(dims1);
        vtkImageViewer2  imageViewer =new vtkImageViewer2();
        imageViewer.SetInputConnection(flip.GetOutputPort());
        vtkRenderWindowInteractor renderWindowInteractor =new vtkRenderWindowInteractor();
        imageViewer.SetupInteractor(renderWindowInteractor);
        imageViewer.Render();
        imageViewer.GetRenderer().ResetCamera();
        imageViewer.Render();
        imageViewer.SetColorWindow(1400);
        imageViewer.SetColorLevel(-400);


        renderWindowInteractor.Start();







    }
}
