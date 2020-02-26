package com.yudi.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 重写了ObjectOutputStream类的writeStreamHeader方法
 * 在断断续续的写入文件信息时不会报错
 */
public class MyObjectOutputStream extends ObjectOutputStream {

    private static File f;

    private static boolean is_append;

    private MyObjectOutputStream(File file, boolean append) throws IOException {
        super(new FileOutputStream(file, append));
    }

    /**
     * 重写了writeStreamHeader()方法，添加了一些添加前缀的条件
     */
    @Override
    protected void writeStreamHeader() throws IOException {

        //添加前缀条件：文件不存在、文件内容为空、不追加（覆盖原内容）
        if (!f.exists() || f.length() == 0 || !is_append) {
            super.writeStreamHeader();
        } else {
            super.reset();     //否则，重置。
        }
    }

    /**
     * 不能通过构造函数实例化一个MyObjectOutputStream，否则会直接调用super(new FileOutputStream(file,append))，而父类这个方法包含了
     * super.writeStreamHeader()方法，也就直接在要写的对象前加上了前缀，所以可以通过这个方法得到一个实例，绕过了那个判定，然后在进行判定是否在这个写对象是增加前缀
     */
    public static MyObjectOutputStream getInstance(File file, boolean append) {
        f = file;
        is_append = append;

        try {
            return new MyObjectOutputStream(file, append);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}