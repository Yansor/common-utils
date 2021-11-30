package com.yansor.utils;

import java.io.*;
import java.net.URL;

public class FileUtil {

    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * 创建文件，如果文件存在直接返回这个文件
     *
     * @param fullFilePath 文件全路径，使用posix风格
     * @return
     * @throws IOException
     */
    public static File touch(String fullFilePath) throws IOException {
        if (fullFilePath == null) {
            return null;
        }

        File file = new File(fullFilePath);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 创建文件夹，如果存在直接返回
     *
     * @param dirPath 文件夹路径，使用POSIX格式，无论哪个平台
     * @return
     */
    public static File mkdir(String dirPath) {
        if (null == dirPath) {
            return null;
        }
        File dir = new File(dirPath);
        dir.mkdirs();
        return dir;
    }

    /**
     * 复制文件<br>
     * 如果目标文件为目录，则将源文件以相同文件名拷贝到目标目录
     *
     * @param src        源文件
     * @param dest       目标文件或目录
     * @param isOverride 是否覆盖目标文件
     * @throws IOException
     */
    public static void copy(File src, File dest, boolean isOverride) throws IOException {
        //check
        if (!src.exists()) {
            throw new FileNotFoundException("File not exist: " + src);
        }
        if (!src.isFile()) {
            throw new IOException("Not a file:" + src);
        }
        if (equals(src, dest)) {
            throw new IOException("Files '" + src + "' and '" + dest + "' are equal");
        }

        if (dest.exists()) {
            if (dest.isDirectory()) {
                dest = new File(dest, src.getName());
            }
            if (dest.exists() && !isOverride) {
                throw new IOException("File already exist: " + dest);
            }
        }

        // do copy file
        FileInputStream input = new FileInputStream(src);
        FileOutputStream output = new FileOutputStream(dest);
        try {
            IoUtil.copy(input, output);
        } finally {
            close(output);
            close(input);
        }

        if (src.length() != dest.length()) {
            throw new IOException("Copy file failed of '" + src + "' to '" + dest + "' due to different sizes");
        }
    }

    /**
     * 移动文件或者目录
     *
     * @param src        源文件或者目录
     * @param dest       目标文件或者目录
     * @param isOverride 是否覆盖目标
     * @throws IOException
     */
    public static void move(File src, File dest, boolean isOverride) throws IOException {
        //check
        if (!src.exists()) {
            throw new FileNotFoundException("File already exist: " + src);
        }
        if (dest.exists()) {
            if (!isOverride) {
                throw new IOException("File already exist: " + dest);
            }
            dest.delete();
        }

        //来源为文件夹，目标为文件
        if (src.isDirectory() && dest.isFile()) {
            throw new IOException(StrUtil.format("Can not move directory [{}] to file [{}]", src, dest));
        }

        //来源为文件，目标为文件夹
        if (src.isFile() && dest.isDirectory()) {
            dest = new File(dest, src.getName());
        }

        if (src.renameTo(dest) == false) {
            //在文件系统不同的情况下，renameTo会失败，此时使用copy，然后删除原文件
            try {
                copy(src, dest, isOverride);
                src.delete();
            } catch (Exception e) {
                throw new IOException(StrUtil.format("Move [{}] to [{}] failed!", src, dest), e);
            }

        }
    }

    /**
     * 检查两个文件是否是同一个文件
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     */
    public static boolean equals(File file1, File file2) {
        try {
            file1 = file1.getCanonicalFile();
            file2 = file2.getCanonicalFile();
        } catch (IOException ignore) {
            return false;
        }
        return file1.equals(file2);
    }

    /**
     * 关闭
     *
     * @param closeable 被关闭的对象
     */
    public static void close(Closeable closeable) throws IOException {
        if (closeable == null) return;
        closeable.close();
    }

    /**
     * 获取绝对路径<br/>
     * 此方法不会判定给定路径是否有效（文件或目录存在）
     *
     * @param path      相对路径
     * @param baseClass 相对路径所相对的类
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path, Class<?> baseClass) {
        if (path == null) {
            path = "";
        }
        if (baseClass == null) {
            return getAbsolutePath(path);
        }
        return baseClass.getResource(path).getPath();
    }

    /**
     * 获取绝对路径，相对于classes的根目录
     *
     * @param pathBaseClassLoader 相对路径
     * @return 绝对路径
     */
    public static String getAbsolutePath(String pathBaseClassLoader) {
        if (pathBaseClassLoader == null) {
            pathBaseClassLoader = "";
        }

        ClassLoader classLoader = ClassUtil.getClassLoader();
        URL url = classLoader.getResource(pathBaseClassLoader);
        if (url == null) {
            return classLoader.getResource("").getPath() + pathBaseClassLoader;
        }
        return url.getPath();
    }

}
