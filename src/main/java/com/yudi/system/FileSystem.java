package com.yudi.system;

import com.yudi.bean.FileOrder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author YUDI
 * @date 2020/2/25
 */
public class FileSystem {

    /**
     * 程序启动时所处的目录是默认的根目录，为 /
     */
    public static String currentPath = FileOrder.ROOTPATH;

    public static void run() {
        //程序一起动就会显示命令的菜单
        menu();
        //控制台读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        do {
            //打印出当前目录
            System.out.print(currentPath + ">:");
            String op = null;
            try {
                //获取键盘输入的字符串
                op = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //以空格分隔字符串 从而判断输入的命令
            String[] ca = op.trim().split(" ");
            try {
                //判断键盘输入的命令，并执行相应的操作
                switch (ca[0].trim()) {
                    //输入的命令为edit 创建文本文件并进行编辑
                    case FileOrder.EDIT:
                        FileEdit.createFile(ca[1].trim(), currentPath);
                        break;
                    //输入的命令为md 创建目录文件
                    case FileOrder.MD:
                        FileEdit.createDir(ca[1].trim(), currentPath);
                        break;
                    //进入指定目录
                    //策略是将当前的目录打印出来从而模拟进入到指定目录
                    //将返回的路径字符串赋值到currentPath变量通过do while循环打印出来
                    //然后程序会根据当前的目录判断出当前目录存在哪些文件和目录文件
                    case FileOrder.CD:
                        currentPath = FileEdit.cdFile(ca[1].trim(), currentPath);
                        break;
                    //删除文件或目录文件
                    case FileOrder.DEL:
                        FileEdit.delFile(ca[1].trim(), currentPath);
                        break;
                    //查看当前目录的所有文件
                    case FileOrder.DIR:
                        FileEdit.lookFileMsg(currentPath);
                        break;
                    //查看文本文件的内容
                    case FileOrder.CAT:
                        FileEdit.catFile(ca[1].trim(), currentPath);
                        break;
                    //查看帮助(有哪些命令)
                    case FileOrder.HELP:
                        //输入help命令就会重新打印一遍命令菜单
                        menu();
                        break;
                    //退出文件系统
                    case FileOrder.EXIT:
                        //直接退出程序运行
                        System.exit(0);
                    default:
                        System.out.println("输入有误");
                        break;
                }
            }//判断 如果多输入了一个空格就提示 输入有误
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("输入有误");
            }
        } while (true);
    }

    public static void menu() {
        System.out.println("==================================================");
        System.out.println(" 改变目录：cd <目录名>\n" +
                " 创建文件：edit <文件名>\n" +
                " 删除文件：del <文件名>\n" +
                " 显示目录：dir <目录名>\n" +
                " 创建目录：md <目录名>\n" +
                " 查看命令：help");
        System.out.println("==================================================");
    }

}
