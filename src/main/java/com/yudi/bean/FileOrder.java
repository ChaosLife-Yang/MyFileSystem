package com.yudi.bean;

/**
 * 文件工具类 用来匹配输入的命令
 * 改变目录：cd <目录名>，工作目录转移到指定的目录之下。目录不存在时给出错误信息。
 * 创建文件：edit <文件名>，创建一个指定名字的新文件，即在目录中增加一项，不考虑文件的内容，对于重名文件给出错误信息。
 * 删除文件：del <文件名>，当没有用户使用指定文件时，将其删除。文件不存在时给出错误信息。
 * 显示目录：dir <目录名>，显示指定目录下的全部文件和第一子目录，如果没有指定目录名，则显示当前目录下的相应内容。
 * 创建目录：md <目录名>，在指定路径下创建指定的目录，如没有指定路径，则在当前目录下创建指定的目录。对于重名目录给出错误信息。
 *
 * @author YUDI
 * @date 2020/2/25
 */
public class FileOrder {
    /**
     * 数据文件保存路径
     */
    public static final String DATAPATH = "E:\\JavaStudy\\FileSystem\\data";
//    public static final String DATAPATH = "/usr/local/filedata/data";
    /**
     * 设置根目录为 /
     */
    public static final String ROOTPATH = "/";
    /**
     * 命令
     */
    public static final String CD = "cd";
    public static final String EDIT = "edit";
    public static final String DEL = "del";
    public static final String DIR = "dir";
    public static final String MD = "md";


}
