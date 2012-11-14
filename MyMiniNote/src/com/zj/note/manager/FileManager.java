package com.zj.note.manager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.zj.note.ConstantValue;

/**
 * IO处理类 simple introduction
 * 
 * <p>
 * detailed comment
 * 
 * @author zhoujian 2012-6-12
 * @see
 * @since 1.0
 */
public class FileManager implements Serializable {

    /**
     * UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * TAG
     */
    private static final String TAG = "FileUtil";

    /**
     * 目录路径
     */
    private String dirPath;

    /**
     * 目录在sd卡的位置
     */
    private static final String SD_DIR_PATH = ConstantValue.SD_DIR_PATH;



    public FileManager(String pathID) {
        try {
            dirPath = getPathByPathId(pathID);
            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        } catch (Throwable e) {
            Log.e(TAG, "FileManager init error", e);
        }
    }



    /**
     * 根据路径id取附件
     * 
     * @param pathID
     * @return
     */
    public static File[] queryAttachByPathID(String pathID) {
        File[] files = null;
        String dirPath = null;
        dirPath = getPathByPathId(pathID);
        File dirFile = new File(dirPath);
        if (dirFile == null || !dirFile.exists() || !dirFile.isDirectory()) {
            return null;
        }
        files = dirFile.listFiles();
        return files;
    }



    /**
     * 根据路径id取指定类型的附件
     * 
     * @param pathID
     * @param filter
     * @return
     */
    public static File[] queryAttachByPathID(String pathID, FileFilter filter) {
        File[] files = null;
        String dirPath = null;
        dirPath = getPathByPathId(pathID);
        File dirFile = new File(dirPath);
        if (dirFile == null || !dirFile.exists() || !dirFile.isDirectory()) {
            return null;
        }
        files = dirFile.listFiles(filter);
        return files;
    }



    /**
     * bitmap转PNG
     * 
     * @param bitmap
     * @param dirPath
     */
    public boolean saveBitmap2PNG(Bitmap bitmap, String fileName) {
        FileOutputStream stream = null;
        boolean flag = true;
        try {
            File bitmapFile = new File(dirPath + fileName);
            if (bitmapFile.exists())
                bitmapFile.delete();
            bitmapFile.createNewFile();
            stream = new FileOutputStream(bitmapFile);
            bitmap.compress(CompressFormat.PNG, 100, stream);
        } catch (Throwable e) {
            Log.e(TAG, "bitmap to file exception", e);
            flag = false;
        } finally {
            try {
                stream.close();
            } catch (Throwable e) {
                Log.e(TAG, "bitmap to file stream not close exception", e);
                flag = false;
            }
        }
        return flag;
    }



    /**
     * bitmap转JPEG
     * 
     * @param bitmap
     * @param dirPath
     */
    public boolean saveBitmap2JPEG(Bitmap bitmap, String fileName) {
        long time = System.currentTimeMillis();
        boolean flag = true;
        FileOutputStream stream = null;
        try {
            File bitmapFile = new File(dirPath + time + ".JPEG");
            if (bitmapFile.exists())
                bitmapFile.delete();
            bitmapFile.createNewFile();
            stream = new FileOutputStream(bitmapFile);
            bitmap.compress(CompressFormat.JPEG, 100, stream);
        } catch (Throwable e) {
            Log.e(TAG, "bitmap to file exception", e);
            flag = false;
        } finally {
            try {
                stream.close();
            } catch (Throwable e) {
                Log.e(TAG, "bitmap to file stream not close exception", e);
                flag = false;
            }
        }
        return flag;
    }



    /**
     * bitmap转png 允许压缩图像质量
     * 
     * @param bitmap
     * @param dirPath
     * @param quality
     */
    public void saveBitmap2PNG(Bitmap bitmap, String fileName, int quality) {
        long time = System.currentTimeMillis();
        FileOutputStream stream = null;
        try {
            File bitmapFile = new File(dirPath + time + ".png");
            if (bitmapFile.exists())
                bitmapFile.delete();
            bitmapFile.createNewFile();
            stream = new FileOutputStream(bitmapFile);
            bitmap.compress(CompressFormat.PNG, quality, stream);
        } catch (Throwable e) {
            Log.e(TAG, "bitmap to file exception", e);
        } finally {
            try {
                stream.close();
            } catch (Throwable e) {
                Log.e(TAG, "bitmap to file stream not close exception", e);
            }
        }
    }



    /**
     * bitmap转jpeg 允许压缩图像质量
     * 
     * @param bitmap
     * @param dirPath
     * @param quality
     */
    public void saveBitmap2JPEG(Bitmap bitmap, String fileName, int quality) {
        long time = System.currentTimeMillis();
        FileOutputStream stream = null;
        try {
            File bitmapFile = new File(dirPath + time + ".JPEG");
            if (bitmapFile.exists())
                bitmapFile.delete();
            bitmapFile.createNewFile();
            stream = new FileOutputStream(bitmapFile);
            bitmap.compress(CompressFormat.JPEG, quality, stream);
        } catch (Throwable e) {
            Log.e(TAG, "bitmap to file exception", e);
        } finally {
            try {
                stream.close();
            } catch (Throwable e) {
                Log.e(TAG, "bitmap to file stream not close exception", e);
            }
        }
    }



    /**
     * 按指定标题和内容保存文本文件
     * 
     * @param title
     * @param content
     */
    public void saveTxtFile(String title, String content) {
        FileWriter writer = null;
        try {
            File txtFile = new File(dirPath + title + ".txt");
            if (txtFile.exists())
                txtFile.delete();
            txtFile.createNewFile();
            writer = new FileWriter(txtFile);
            writer.write(content);
        } catch (Throwable tr) {
            Log.e(TAG, "save txt file exception", tr);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 根据路径id取对应的路径
     * 
     * @param pathID
     * @return
     */
    private static String getPathByPathId(String pathID) {
        if (pathID.startsWith(ConstantValue.SD_DIR_PATH)) {
            return pathID;
        }
        String dirPath = null;
        if (pathID.endsWith("/")) {
            dirPath = SD_DIR_PATH + pathID;
        } else {
            dirPath = SD_DIR_PATH + pathID + "/";
        }
        return dirPath;
    }



    /**
     * 按路径删除文件
     * 
     * @param filePath
     */
    public void deleteFile(String filePath) {
        File file = new File(dirPath + filePath);
        Log.d(TAG, dirPath);
        if (file.isDirectory()) {
            deleteDir(file);
            return;
        }
        if (!file.exists() || file == null) {
            throw new IllegalArgumentException("file not found");
        }
        file.delete();

    }



    /**
     * 删除指定目录
     * 
     * @param file
     */
    public static void deleteDir(File file) {
        try {
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("file must be directory");
            }

            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                file.delete();
                return;
            }
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
            file.delete();
        } catch (Throwable e) {
            Log.e(TAG, "delete dir exception", e);
        }
    }



    /**
     * 删除指定目录
     * 
     * @param file
     */
    public static void deleteDir(String pathID) {
        String path = getPathByPathId(pathID);
        File file = new File(path);
        try {
            if (!file.isDirectory()) {
                file.delete();
                return;
            }

            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                file.delete();
                return;
            }
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
            file.delete();
        } catch (Throwable e) {
            Log.e(TAG, "delete dir exception", e);
        }
    }



    public void renameFile(String oldName, String newName) {
        File oldFile = new File(oldName);
        oldFile.renameTo(new File(newName));
    }



    /**
     * 压缩文件,文件夹
     * 
     * @param srcFilePath
     *            要压缩的文件/文件夹名字
     * @param zipFilePath
     *            指定压缩的目的和名字
     * @throws Exception
     */
    public static boolean zipFolder(String srcFilePath, String zipFilePath) {
        // 创建Zip包
        java.util.zip.ZipOutputStream outZip = null;
        try {
            srcFilePath = getPathByPathId(srcFilePath);
            zipFilePath = getPathByPathId(zipFilePath);
            outZip = new java.util.zip.ZipOutputStream(
                new java.io.FileOutputStream(zipFilePath));

            // 打开要输出的文件
            java.io.File file = new java.io.File(srcFilePath);

            // 压缩
            zipFiles(file.getParent() + java.io.File.separator, file.getName(),
                outZip);
            return true;
        } catch (Throwable e) {
            Log.e(TAG, "zip exception ", e);
            return false;
        } finally {
            // 完成,关闭
            try {
                outZip.finish();
                outZip.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }

    }



    /**
     * 压缩文件
     * 
     * @param folderPath
     * @param filePath
     * @param zipOut
     * @throws Exception
     */
    private static void zipFiles(String folderPath, String filePath,
        java.util.zip.ZipOutputStream zipOut) throws Exception {
        if (zipOut == null) {
            return;
        }

        java.io.File file = new java.io.File(folderPath + filePath);

        // 判断是不是文件
        if (file.isFile()) {
            java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(
                filePath);
            java.io.FileInputStream inputStream = new java.io.FileInputStream(
                file);
            zipOut.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];

            while ((len = inputStream.read(buffer)) != -1) {
                zipOut.write(buffer, 0, len);
            }

            zipOut.closeEntry();
        } else {
            // 文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            // 如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(
                    filePath + java.io.File.separator);
                zipOut.putNextEntry(zipEntry);
                zipOut.closeEntry();
            }

            // 如果有子文件, 遍历子文件
            for (int i = 0; i < fileList.length; i++) {
                zipFiles(folderPath, filePath + java.io.File.separator
                    + fileList[i], zipOut);
            }

        }

    }



    /**
     * 判断是否为png图片
     * 
     * @param fileName
     * @return
     */
    public static boolean isPNGFile(String fileName) {
        return fileName.substring(fileName.length() - 4, fileName.length())
            .toLowerCase().equals(ConstantValue.FILE_EX_NAME_PNG);
    }



    /**
     * 判断是否为jpeg图片
     * 
     * @param fileName
     * @return
     */
    public static boolean isJPEGFile(String fileName) {
        return fileName.substring(fileName.length() - 5, fileName.length())
            .toLowerCase().equals(ConstantValue.FILE_EX_NAME_JPEG)
            || fileName.substring(fileName.length() - 4, fileName.length())
                .toLowerCase().equals(ConstantValue.FILE_EX_NAME_JPG);
    }



    /**
     * 根据路径id和文件名来取对应的File对象
     * 
     * @param dirID
     * @param name
     * @return
     */
    public static File getFileInstance(String dirID, String name) {
        File file = null;
        String absPath = null;
        if (!dirID.endsWith("/")) {
            dirID = dirID + "/";
        }
        if (dirID.startsWith(ConstantValue.SD_DIR_PATH)) {
            absPath = dirID + name;
        } else {
            absPath = ConstantValue.SD_DIR_PATH + dirID + name;
        }
        file = new File(absPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }
}
