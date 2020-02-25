import com.yudi.bean.FileMsg;
import com.yudi.bean.MyObjectOutputStream;
import org.junit.Test;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author YUDI
 * @date 2020/2/25
 */
public class test {

    @Test
    public void test1() throws IOException {
        MyObjectOutputStream os = MyObjectOutputStream.getInstance(new File("E:\\IdeaProjects\\FileSystemTest\\data"), true);
        FileMsg fileMsg = new FileMsg();
        fileMsg.setDate(new Date());
        fileMsg.setFilename("123123");
        fileMsg.setRealpath("E:\\IdeaProjects\\FileSystemTest");
        fileMsg.setContent("123");
        fileMsg.setType("file");
        os.writeObject(fileMsg);
    }

    @Test
    public void test2() {
        try {
            FileReader fileReader = new FileReader("E:\\IdeaProjects\\FileSystemTest\\data");
            int len;
            String msg = new String();
            //用数组读取 效率更快
            char[] c = new char[1024];
            while ((len = fileReader.read(c)) != -1) {
                msg += (new String(c, 0, len));
            }
//            System.out.println(msg);
            if (!msg.equals("")) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream("E:\\IdeaProjects\\FileSystemTest\\data"));
                FileMsg fileMsg;
                while (true) {
                    try {
                        //从数据文件中读取对象
                        fileMsg = (FileMsg) ois.readObject();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        System.out.println(String.format("%-5s%-20s%s\t%-20s", fileMsg.getType(), format.format(fileMsg.getDate()), fileMsg.getFilename(), fileMsg.getRealpath()));
                        //捕获EOFException异常 捕获到就退出循环
                    } catch (EOFException e) {
                        break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void del() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("E:\\IdeaProjects\\FileSystemTest\\data"));
            FileMsg fileMsg;
            List<FileMsg> list = new LinkedList<>();
            while (true) {
                try {
                    //从数据文件中读取对象
                    fileMsg = (FileMsg) ois.readObject();
                    if (!fileMsg.getFilename().equals("123123"))
                        list.add(fileMsg);
                    //捕获EOFException异常 捕获到就退出循环
                } catch (EOFException e) {
                    break;
                }
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("E:\\IdeaProjects\\FileSystemTest\\data"));
            for (FileMsg f : list) {
                oos.writeObject(f);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
