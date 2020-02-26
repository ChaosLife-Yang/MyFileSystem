package com.yudi.system;


import com.yudi.bean.FileOrder;
import com.yudi.bean.FileMsg;
import com.yudi.bean.MyObjectOutputStream;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * 实现命令行对文件的操作类
 *
 * @author YUDI
 * @date 2020/2/25
 */
public class FileEdit {

    /**
     * 判断要创建的文件是否存在
     *
     * @param name        文件名
     * @param currentPath 当前目录
     * @return true或false
     */
    public static boolean fileExist(String name, String currentPath) {
        try {
            FileReader reader = new FileReader(FileOrder.DATAPATH);
            int len;
            String msg = "";
            //用数组读取 效率更快
            char[] c = new char[1024];
            while ((len = reader.read(c)) != -1) {
                msg += new String(c, 0, len);
            }
            if (msg.equals("")) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
            FileMsg fileMsg;
            while (true) {
                try {
                    //从数据文件中读取对象
                    fileMsg = (FileMsg) ois.readObject();
                    String absolutePah = fileMsg.getRealpath() + fileMsg.getFilename();
                    if (absolutePah.equals(currentPath + name)) {
                        ois.close();
                        return true;
                    }
                    //捕获EOFException异常 捕获到就退出循环
                } catch (EOFException e) {
                    ois.close();
                    return false;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * edit命令创建普通文件
     *
     * @param name        文件名
     * @param currentPath 当前目录
     */
    public static void createFile(String name, String currentPath) {
        if (fileExist(name, currentPath)) {
            System.out.println("文件已经存在，请重新来！");
            return;
        }
        if (name.contains("/")) {
            String[] dir = name.split("/");
            currentPath = currentPath + name.substring(0, name.lastIndexOf(dir[dir.length - 1]));
            name = dir[dir.length - 1];
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
                FileMsg fileMsg;
                int flag = 0;
                while (true) {
                    try {
                        fileMsg = (FileMsg) ois.readObject();
                        if (fileMsg.getType().equals("file")) {
                            if (fileMsg.getRealpath().equals(currentPath)) {
                                flag++;
                            }
                        }
                        if (fileMsg.getType().equals("dir")) {
                            if ((fileMsg.getRealpath() + fileMsg.getFilename() + "/").contains(currentPath)) {
                                flag++;
                            }
                        }
                    } catch (EOFException e) {
                        ois.close();
                        break;
                    }
                }

                if (flag == 0) {
                    System.out.println("父级目录不存在！");
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        FileMsg fileMsg = new FileMsg();
        fileMsg.setFilename(name);
        fileMsg.setRealpath(currentPath);
        fileMsg.setType("file");
        fileMsg.setDate(new Date());
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入文件内容：");
        fileMsg.setContent(sc.nextLine());
        try {
            MyObjectOutputStream moos = MyObjectOutputStream.getInstance(new File(FileOrder.DATAPATH), true);
            moos.writeObject(fileMsg);
            System.out.println("创建成功");
            moos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * cat命令 打印文件内容
     *
     * @param name        文件名
     * @param currentPath 当前目录
     */
    public static void catFile(String name, String currentPath) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
            FileMsg fileMsg;
            boolean flag = false;
            while (true) {
                try {
                    //从数据文件中读取对象
                    fileMsg = (FileMsg) ois.readObject();
                    String absolutePath = fileMsg.getRealpath() + fileMsg.getFilename();
                    if (absolutePath.equals(currentPath + name) && fileMsg.getType().equals("file")) {
                        System.out.println(fileMsg.getContent());
                        flag = true;
                    }
                    //捕获EOFException异常 捕获到就退出循环
                } catch (EOFException e) {
                    if (!flag) {
                        System.out.println("该文件为目录或不存在！");
                    }
                    ois.close();
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * md命令 创建目录文件
     *
     * @param name        文件名
     * @param currentPath 当前目录
     */
    public static void createDir(String name, String currentPath) {
        if (fileExist(name, currentPath)) {
            System.out.println("文件已经存在，请重新来！");
            return;
        }
        if (name.contains("/")) {
            String[] dir = name.split("/");
            currentPath = currentPath + name.substring(0, name.lastIndexOf(dir[dir.length - 1]));
            name = dir[dir.length - 1];
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
                FileMsg fileMsg;
                List<FileMsg> list = new LinkedList<>();
                int flag = 0;
                while (true) {
                    try {
                        fileMsg = (FileMsg) ois.readObject();
                        if (fileMsg.getType().equals("file")) {
                            if (fileMsg.getRealpath().equals(currentPath)) {
                                flag++;
                            }
                        }
                        if (fileMsg.getType().equals("dir")) {
                            if ((fileMsg.getRealpath() + fileMsg.getFilename() + "/").contains(currentPath)) {
                                flag++;
                            }
                        }
                    } catch (EOFException e) {
                        ois.close();
                        break;
                    }
                }

                if (flag == 0) {
                    System.out.println("父级目录不存在！");
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        FileMsg fileMsg = new FileMsg();
        fileMsg.setFilename(name);
        fileMsg.setRealpath(currentPath);
        fileMsg.setType("dir");
        fileMsg.setDate(new Date());
        try {
            MyObjectOutputStream moos = MyObjectOutputStream.getInstance(new File(FileOrder.DATAPATH), true);
            moos.writeObject(fileMsg);
            System.out.println("创建成功");
            moos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * del命令 删除文件
     * 思想是把对象全部拿出来然后重新写入data文件
     *
     * @param name        文件名
     * @param currentPath 当前目录
     */
    public static void delFile(String name, String currentPath) {
        if (!fileExist(name, currentPath)) {
            System.out.println("文件不存在");
            return;
        }
        try {
            FileMsg fileMsg;
            List<FileMsg> list = new LinkedList<>();
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
            while (true) {
                try {
                    //从数据文件中读取对象
                    fileMsg = (FileMsg) ois.readObject();
                    String absolutePath = fileMsg.getRealpath() + fileMsg.getFilename();
                    //如果是目录就连同所有子文件一起删除
                    if (!absolutePath.equals(currentPath + name) && !fileMsg.getRealpath().contains(currentPath + name)) {
                        list.add(fileMsg);
                    }
                    //捕获EOFException异常 捕获到就退出循环
                } catch (EOFException e) {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FileOrder.DATAPATH));
                    for (FileMsg f : list) {
                        oos.writeObject(f);
                    }
                    oos.close();
                    System.out.println("删除成功");
                    ois.close();
                    break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * cd命令 进入目录文件
     *
     * @param name        文件名
     * @param currentPath 当前目录
     */
    public static String cdFile(String name, String currentPath) {
        if (name.equals("../")) {
            String[] path = currentPath.split("/");
            currentPath = currentPath.substring(0, currentPath.lastIndexOf(path[path.length - 1]));
            return currentPath;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
            FileMsg fileMsg;
            while (true) {
                try {
                    //从数据文件中读取对象
                    fileMsg = (FileMsg) ois.readObject();
                    String absolutePah = fileMsg.getRealpath() + fileMsg.getFilename();
                    if (absolutePah.equals(currentPath + name)) {
                        if (fileMsg.getType().equals("file")) {
                            System.out.println("该文件不是目录！");
                            return currentPath;
                        }
                        break;
                    }
                    //捕获EOFException异常 捕获到就退出循环
                } catch (EOFException e) {
                    ois.close();
                    System.out.println("不存在该目录");
                    return currentPath;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return currentPath + name + "/";
    }

    /**
     * dir命令 显示当前目录下的文件信息
     */
    public static void lookFileMsg(String currentPath) {
        try {
            FileReader fileReader = new FileReader(FileOrder.DATAPATH);
            int len;
            String msg = "";
            //用数组读取 效率更快
            char[] c = new char[1024];
            while ((len = fileReader.read(c)) != -1) {
                msg += (new String(c, 0, len));
            }
            //如果数据存放的文件里没有数据,则不读取对象
            if (!msg.equals("")) {
                FileMsg fileMsg;
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
                while (true) {
                    try {
                        //从数据文件中读取对象
                        fileMsg = (FileMsg) ois.readObject();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (fileMsg.getRealpath().equals(currentPath)) {
                            System.out.println(String.format("%-8s%-24s%s", "<" + fileMsg.getType() + ">", format.format(fileMsg.getDate()), fileMsg.getFilename()));
                        }
                    } catch (EOFException e) {
                        //捕获EOFException异常 捕获到就退出循环
                        ois.close();
                        break;
                    }
                }
            } else {
                System.out.println("当前系统没有文件");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
