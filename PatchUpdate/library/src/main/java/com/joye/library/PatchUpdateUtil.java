package com.joye.library;

/**
 * 增量更新工具类
 * 1、文件差分
 * 2、文件合并
 *
 * 注：使用前，客户端需要判断文件读写权限
 * <p>
 * Created by zczhang on 16/12/12.
 */

public class PatchUpdateUtil {

    static {
        System.loadLibrary("bsdiff");
        System.loadLibrary("bspatch");
    }

    public static native int genDifferFile(String oldFile, String newFile, String differFile);

    public static native int fileCombine(String oldFile, String newFile, String patchFile);

}
