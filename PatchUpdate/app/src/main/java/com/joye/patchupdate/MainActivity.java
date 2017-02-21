package com.joye.patchupdate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.joye.library.PatchUpdateUtil;

import java.io.File;

/**
 * Apk增量更新测试
 * 注：
 * 1、省去了差分包下载过程，直接将其放置sdcard根目录
 * 2、差分包名称必须为apk.patch
 * 3、可修改点击button弹出不同的版本提示，进行更新结果确认
 */
public class MainActivity extends AppCompatActivity {

    private static final String SDCARD_ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String PATCHER_PATH = SDCARD_ROOT_DIR + File.separator + "apk.patch";
    private static final String NEW_VERSION_PATH = SDCARD_ROOT_DIR + File.separator +  "new_version.apk";
    private static final String NEW_APK_PATH = SDCARD_ROOT_DIR + File.separator  + "app-debug-new.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onCheckVersionBtnClick(View view) {
        Toast.makeText(getBaseContext(), "Version 1.0", Toast.LENGTH_LONG).show();
    }

    //升级版本
    public void onUpdateVersionBtnClick(View view) {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
            String sourceDir = applicationInfo.sourceDir;
            System.out.println("sourceDir : " + sourceDir);
            System.out.println("newPath : " + NEW_VERSION_PATH);
            System.out.println("patchPath : " + PATCHER_PATH);
            File patchFile = new File(PATCHER_PATH);
            if (!patchFile.exists()) {
                Toast.makeText(getBaseContext(), "差分包不存在...", Toast.LENGTH_LONG).show();
                return;
            }
            applyPatch(sourceDir, NEW_VERSION_PATH, PATCHER_PATH);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //生成差分文件
    public void onFileDifferBtnClick(View view) throws PackageManager.NameNotFoundException {
        File filePatch = new File(PATCHER_PATH);
        if (filePatch.exists()) {
            filePatch.delete();
        }
        ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
        String sourceDir = applicationInfo.sourceDir;

        new FileDifferTask().execute(sourceDir, NEW_APK_PATH, PATCHER_PATH);

    }

    private void applyPatch(String oldPath, String newPath, String patchPath) {

        new LoadPatchFileTask().execute(oldPath, newPath, patchPath);
    }

    class LoadPatchFileTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("---开始合并增量包！----");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            System.out.println("---合并增量包结果--- " + integer);
            if (integer == PatchUpdateUtil.RESULT_CODE_SUC) {
                installApk(getBaseContext(), NEW_VERSION_PATH);
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            return new PatchUpdateUtil().filePatch(getBaseContext(), params[0], params[1], params[2]);
        }
    }

    class FileDifferTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("---开始生成差分包！----");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            System.out.println("---生成差分包结果--- " + integer);
        }

        @Override
        protected Integer doInBackground(String... params) {
            return new PatchUpdateUtil().fileDiffer(getBaseContext(), params[0], params[1], params[2]);
        }
    }

    public void installApk(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        File apkFile = new File(path);
//        // 确保更新后提示打开？
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(context, "com.joye.patchupdate.fileProvider", apkFile);
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//        } else {
//            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
        intent.setDataAndType(Uri.parse("content://" + path), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
