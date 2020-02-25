package com.yudi.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件详情实体类
 *
 * @author YUDI
 * @date 2020/2/25
 */
@Data
public class FileMsg implements Serializable {
    /**
     * 文件名
     */
    private String filename;
    /**
     * 文件所在的目录路径
     */
    private String realpath;
    /**
     * 最后一次修改日期
     */
    private Date date;
    /**
     * 文件内容
     */
    private String content;
    /**
     * 文件类型 文件或目录
     */
    private String type;


}
