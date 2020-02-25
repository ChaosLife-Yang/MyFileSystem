package com.yudi.system;

import com.yudi.bean.FileManagerUtil;

import java.util.Scanner;

/**
 * @author YUDI
 * @date 2020/2/25
 */
public class FileSystem {

    /**
     * 默认的当前目录为 /
     */
    public static String currentPath = FileManagerUtil.ROOTPATH;

    public static void run() {
        menu();
        Scanner sc = new Scanner(System.in);
        do {
            System.out.print(currentPath + ">:");
            String op = sc.nextLine();
            //以空格分隔字符串 从而判断输入的命令
            String[] ca = op.trim().split(" ");
            try {
                switch (ca[0].trim()) {
                    case FileManagerUtil.EDIT:
                        FileEdit.createFile(ca[1].trim(), currentPath);
                        break;
                    case FileManagerUtil.MD:
                        FileEdit.createDir(ca[1].trim(), currentPath);
                        break;
                    case FileManagerUtil.CD:
                        currentPath = FileEdit.cdFile(ca[1].trim(), currentPath);
                        break;
                    case FileManagerUtil.DEL:
                        FileEdit.delFile(ca[1].trim(), currentPath);
                        break;
                    case FileManagerUtil.DIR:
                        FileEdit.lookFileMsg(currentPath);
                        break;
                    case "cat":
                        FileEdit.catFile(ca[1].trim(), currentPath);
                        break;
                    case "help":
                        menu();
                        break;
                    case "exit":
                        System.exit(0);
                    default:
                        System.out.println("输入有误");
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
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
