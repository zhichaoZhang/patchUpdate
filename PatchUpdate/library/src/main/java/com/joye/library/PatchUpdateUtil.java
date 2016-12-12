package com.joye.library;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * 增量更新工具类
 * 1、文件差分
 * 2、文件合并
 *
 * 返回码：
 * 0    正常
 * -1   参数错误
 * -2   缺少权限
 * 注：使用前，客户端需要判断文件读写权限
 * <p>
 * Created by zczhang on 16/12/12.
 */

public class PatchUpdateUtil {
    private static final int RESULT_CODE_SUC = 0;
    private static final int RESULT_CODE_PARAM_ERROR = -1;
    private static final int RESULT_CODE_NO_PERMISSION = -2;

    static {
        System.loadLibrary("bsdiff");
        System.loadLibrary("bspatch");
    }

    public static int fileDiffer(Context context, String oldFile, String newFile, String differFile) {
        int result = RESULT_CODE_SUC;
        if(oldFile == null || newFile == null || differFile == null) {
            result = RESULT_CODE_PARAM_ERROR;
        }
        if(!hasStoragePermission(context)) {
            return RESULT_CODE_NO_PERMISSION;
        }
        result = genDifferFile(oldFile, newFile, differFile);
        return result;
    }

    public static int filePatch(Context context, String oldFile, String newFile, String patchFile) {
        int result = RESULT_CODE_SUC;
        if(oldFile == null || newFile == null || patchFile == null) {
            result = RESULT_CODE_PARAM_ERROR;
        }
        if(!hasStoragePermission(context)) {
            return RESULT_CODE_NO_PERMISSION;
        }
        result = fileCombine(oldFile, newFile, patchFile);
        return result;
    }

    private static native int genDifferFile(String oldFile, String newFile, String differFile);

    private static native int fileCombine(String oldFile, String newFile, String patchFile);

    private static boolean hasStoragePermission(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

}
