package com.bjw.bean;

/**
 * 创建人：wxdn
 * 创建时间：2017/12/15
 * 功能描述：
 */

public class Version {
    int versionCode;
    String versionName;
    String apkPath;

    public Version() {
    }

    public Version(int versionCode, String versionName, String apkPath) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.apkPath = apkPath;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }
}
