package com.joye.patchupdate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private static final String NEW_VERSION_PATH = SDCARD_ROOT_DIR + File.separator + "new_version.apk";

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
            if(!patchFile.exists()) {
                Toast.makeText(getBaseContext(), "差分包不存在...", Toast.LENGTH_LONG).show();
                return;
            }
            applyPatch(sourceDir, NEW_VERSION_PATH, PATCHER_PATH);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
            System.out.println("---合并增量包结束！开始安装----");
            installApk(getBaseContext(), NEW_VERSION_PATH);
        }

        @Override
        protected Integer doInBackground(String... params) {
            return PatchUpdateUtil.fileCombine(params[0], params[1], params[2]);
        }
    }

    public void installApk(Context context, String path) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        // 确保更新后提示打开？
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        context.startActivity(i);
    }
}
