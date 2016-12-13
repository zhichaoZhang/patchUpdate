# patchUpdate
Apk patch update for android

# Add to your project
```
<dependency>
  <groupId>com.joye</groupId>
  <artifactId>patchupdate</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

# How to use
```
    PatchUpdateUtil patchUpdateUtil =  new PatchUpdateUtil();
    patchUpdateUtil.filePatch(Context context, String oldFile, String newFile, String patchFile);
    patchUpdateUtil.fileDiffer(Context context, String oldFile, String newFile, String differFile);

you must check the operate result code.

    public static final int RESULT_CODE_SUC = 0;
    public static final int RESULT_CODE_PARAM_ERROR = -1;
    public static final int RESULT_CODE_NO_PERMISSION = -2;
```
