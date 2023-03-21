import java.io.File;

public class MP4 {
    public static void main(String[] args) {
        //文件夹路径
        File folder = new File("F:\\phone\\0");

        //判断文件夹是否存在
        if(!folder.exists()){
            System.out.println("文件夹不存在");
        }

        //文件夹下所有的文件数组
        File[] files = folder.listFiles();

        //重命名
        for (File file :
                files) {
            File newFile = new File(file.getAbsolutePath() + ".mp4");
            file.renameTo(newFile);
        }

    }
}
