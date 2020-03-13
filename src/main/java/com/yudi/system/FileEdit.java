package com.yudi.system;


import com.yudi.bean.FileOrder;
import com.yudi.bean.FileMsg;
import com.yudi.bean.MyObjectOutputStream;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 实现命令行对文件的操作类
 *
 * @author YUDI
 * @date 2020/2/25
 */
public class FileEdit {

    /**
     * 将文件信息写入data文件的操作
     * 因为此项目会频繁的写入对象信息到data文件
     * 所以封装此方法供其他方法调用 避免代码冗余
     *
     * @param fileMsg 文件信息对象
     * @return 是否写入成功 true或false
     */
    public static boolean addObject(FileMsg fileMsg) {
        try {
            //将对象信息写入data文件
            MyObjectOutputStream moos = MyObjectOutputStream.getInstance(new File(FileOrder.DATAPATH), true);
            moos.writeObject(fileMsg);
            moos.close();
            return true;
        } catch (IOException e) {
            System.out.println("添加失败");
        }
        return false;
    }

    /**
     * cd命令 输入的值中如果有/就需要对字符串进行拆分到达指定的目录
     * 此方法供其他方法调用 避免代码冗余
     *
     * @param name        输入的
     * @param currentPath 输入此命令时的当前路径
     * @return 文件名和其所在父目录的map
     */
    public static Map<String, String> splitDir(String name, String currentPath) {
        //通过 / 进行字符串拆分成字符串数组
        String[] dir = name.split("/");
        //获取拆分后的父目录路径
        currentPath = currentPath + name.substring(0, name.lastIndexOf(dir[dir.length - 1]));
        //获取拆分后的文件名
        name = dir[dir.length - 1];
        Map<String, String> map = new HashMap<>(2);
        map.put("name", name);
        map.put("currentPath", currentPath);
        return map;
    }

    /**
     * 判断父目录是否存在
     * 此方法供其他方法调用 避免代码冗余
     *
     * @param currentPath 当前目录
     * @return 是否存在父目录 true或false
     */
    public static boolean faDirExist(String currentPath) {
        try {
            //读取data文件里的信息
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
            FileMsg fileMsg;
            int flag = 0;
            while (true) {
                try {
                    //循环读取data文件中的文件信息 逐一进行比对
                    fileMsg = (FileMsg) ois.readObject();
                    //文件类型为文本文件
                    if ("file".equals(fileMsg.getType())) {
                        //通过文本文件判断是否存在该父目录
                        if (fileMsg.getRealpath().equals(currentPath)) {
                            flag++;
                        }
                    }
                    //文件类型为目录文件
                    if ("dir".equals(fileMsg.getType())) {
                        //通过目录文件的路径判断存在该父目录
                        if ((fileMsg.getRealpath() + fileMsg.getFilename() + "/").contains(currentPath)) {
                            flag++;
                        }
                    }
                    //捕获EOFException异常 捕获到就意味着文件读取完毕 可以退出循环了
                } catch (EOFException e) {
                    ois.close();
                    break;
                }
            }
            if (flag == 0) {
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 判断要创建的文件是否存在
     * 此方法供其他方法调用 避免代码冗余
     *
     * @param name        文件名
     * @param currentPath 当前目录
     * @return 是否存在 true或false
     */
    public static boolean fileExist(String name, String currentPath) {
        //先判断data文件中的内容是否为空
        try {
            FileReader reader = new FileReader(FileOrder.DATAPATH);
            int len;
            String msg = "";
            //用数组读取 效率更快
            char[] c = new char[1024];
            while ((len = reader.read(c)) != -1) {
                msg += new String(c, 0, len);
            }
            if ("".equals(msg)) {
                reader.close();
                //文件为空 返回false
                return false;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //文件内容不为空就可以读取data文件进行判断文件信息是否存在
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
            FileMsg fileMsg;
            while (true) {
                try {
                    //从数据文件中读取对象
                    fileMsg = (FileMsg) ois.readObject();
                    //获取文件在文件系统中的绝对路径
                    String absolutePah = fileMsg.getRealpath() + fileMsg.getFilename();
                    if (absolutePah.equals(currentPath + name)) {
                        ois.close();
                        return true;
                    }
                    //捕获EOFException异常 捕获到就意味着文件读取完毕 可以退出循环了
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
     * edit命令 创建普通文本文件
     *
     * @param name        文件名
     * @param currentPath 当前目录
     */
    public static void createFile(String name, String currentPath) {
        //先进行判断要创建的文件是否存在
        if (fileExist(name, currentPath)) {
            System.out.println("文件已经存在，请重新来！");
            return;
        }
        //判断键盘输入的文件名是否有 / 进行父目录和文件名的拆分
        if (name.contains("/")) {
            Map<String, String> map = splitDir(name, currentPath);
            name = map.get("name");
            currentPath = map.get("currentPath");
            //拆分好父目录和文件名后判断父目录是否存在
            if (!faDirExist(currentPath)) {
                System.out.println("父级目录不存在！");
                return;
            }
        }
        FileMsg fileMsg = new FileMsg();
        fileMsg.setFilename(name);
        fileMsg.setRealpath(currentPath);
        fileMsg.setType("file");
        fileMsg.setDate(new Date());
        try {
            //从键盘输入文本文件的信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("请输入文件内容：");
            fileMsg.setContent(reader.readLine());
            //将文件信息写入data文件
            if (addObject(fileMsg)) {
                System.out.println("创建成功");
            }
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
        //先判断文件是否存在
        if (!fileExist(name, currentPath)) {
            System.out.println("该文件不存在！");
            return;
        }
        try {
            //读取data文件
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
            FileMsg fileMsg;
            while (true) {
                try {
                    //从数data文件中读取对象信息
                    fileMsg = (FileMsg) ois.readObject();
                    String absolutePath = fileMsg.getRealpath() + fileMsg.getFilename();
                    // 如果该文本文件存在且文件类型为文本文件就将该文本文件的内容打印出来
                    if (absolutePath.equals(currentPath + name) && "file".equals(fileMsg.getType())) {
                        System.out.println(fileMsg.getContent());
                        //判断 如果该文件存在且文件类型为目录文件，则打印无法读取的信息并跳出循环
                    } else if (absolutePath.equals(currentPath + name) && "dir".equals(fileMsg.getType())) {
                        System.out.println("该文件为目录文件，无法读取内容！");
                        ois.close();
                        break;
                    }
                    //捕获EOFException异常 捕获到就意味着data文件读取完毕 可以退出循环了
                } catch (EOFException e) {
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
        //根据当前目录和文件名判断该文件是否存在
        if (fileExist(name, currentPath)) {
            System.out.println("文件已经存在，请重新来！");
            return;
        }
        //判断键盘输入的文件名是否有 / 进行父目录和文件名的拆分
        if (name.contains("/")) {
            Map<String, String> map = splitDir(name, currentPath);
            name = map.get("name");
            currentPath = map.get("currentPath");
            if (!faDirExist(currentPath)) {
                System.out.println("父级目录不存在！");
                return;
            }
        }
        FileMsg fileMsg = new FileMsg();
        fileMsg.setFilename(name);
        fileMsg.setRealpath(currentPath);
        fileMsg.setType("dir");
        fileMsg.setDate(new Date());
        //将文件信息写入data文件中
        if (addObject(fileMsg)) {
            System.out.println("创建成功");
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
        //根据当前目录和文件名判断该文件是否存在
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
                    //捕获EOFException异常 捕获到就意味着data文件读取完毕 可以退出循环了
                } catch (EOFException e) {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FileOrder.DATAPATH));
                    //将未删除的文件信息覆盖到data文件 达到删除的目的
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
        //如果键盘输入的值是../就意味着要进入父目录
        if ("../".equals(name)) {
            String[] path = currentPath.split("/");
            currentPath = currentPath.substring(0, currentPath.lastIndexOf(path[path.length - 1]));
            return currentPath;
        }
        try {
            //读取data文件
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
            FileMsg fileMsg;
            while (true) {
                try {
                    //从数据文件中读取对象
                    fileMsg = (FileMsg) ois.readObject();
                    String absolutePah = fileMsg.getRealpath() + fileMsg.getFilename();
                    if (absolutePah.equals(currentPath + name)) {
                        if ("file".equals(fileMsg.getType())) {
                            System.out.println("该文件不是目录！");
                            return currentPath;
                        }
                        break;
                    }
                    //捕获EOFException异常 捕获到就意味着data文件读取完毕 可以退出循环了
                } catch (EOFException e) {
                    ois.close();
                    System.out.println("不存在该目录");
                    return currentPath;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("当前系统没有目录");
            return currentPath;
        }
        return currentPath + name + "/";
    }

    /**
     * dir命令 显示当前目录下的文件信息
     *
     * @param currentPath 当前目录路径
     */
    public static void lookFileMsg(String currentPath) {
        try {
            //读取data文件是否内容为空
            FileReader fileReader = new FileReader(FileOrder.DATAPATH);
            int len;
            String msg = "";
            //用数组读取 效率更快
            char[] c = new char[1024];
            while ((len = fileReader.read(c)) != -1) {
                msg += (new String(c, 0, len));
            }
            //如果数据存放的文件里没有数据,则不读取文件信息
            if (!"".equals(msg)) {
                FileMsg fileMsg;
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileOrder.DATAPATH));
                while (true) {
                    try {
                        //从数据文件中读取对象
                        fileMsg = (FileMsg) ois.readObject();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (fileMsg.getRealpath().equals(currentPath)) {
                            //打印文件信息:
                            //文件类型(dir表示为目录文件,file表示为文本文件) 文件名 最后一次操作该文件的时间
                            System.out.println(String.format("%-8s%-24s%s", "<" + fileMsg.getType() + ">", format.format(fileMsg.getDate()), fileMsg.getFilename()));
                        }
                        //捕获EOFException异常 捕获到就意味着data文件读取完毕 可以退出循环了
                    } catch (EOFException e) {
                        ois.close();
                        break;
                    }
                }
            } else {
                //data文件为空
                System.out.println("当前系统没有文件");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
