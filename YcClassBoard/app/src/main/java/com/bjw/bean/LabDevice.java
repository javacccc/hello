package com.bjw.bean;

/**
 * 创建人：wxdn
 * 创建时间：2017/12/20
 * 功能描述：
 */

public class LabDevice {
    int lab_room_id;
    int lab_device_id;
    String lab_device_name;
    String lab_device_image_url;
    String lab_device_supplier;
    String lab_device_introduct;
    String lab_device_video_url;

    public LabDevice(int lab_room_id, int lab_device_id, String lab_device_name, String lab_device_image_url, String lab_device_supplier, String lab_device_introduct, String lab_device_video_url) {
        this.lab_room_id = lab_room_id;
        this.lab_device_id = lab_device_id;
        this.lab_device_name = lab_device_name;
        this.lab_device_image_url = lab_device_image_url;
        this.lab_device_supplier = lab_device_supplier;
        this.lab_device_introduct = lab_device_introduct;
        this.lab_device_video_url = lab_device_video_url;
    }

    public String getLab_device_supplier() {
        return lab_device_supplier;
    }

    public void setLab_device_supplier(String lab_device_supplier) {
        this.lab_device_supplier = lab_device_supplier;
    }

    public String getLab_device_introduct() {
        return lab_device_introduct;
    }

    public void setLab_device_introduct(String lab_device_introduct) {
        this.lab_device_introduct = lab_device_introduct;
    }

    public String getLab_device_video_url() {
        return lab_device_video_url;
    }

    public void setLab_device_video_url(String lab_device_video_url) {
        this.lab_device_video_url = lab_device_video_url;
    }

    public LabDevice(int lab_room_id, int lab_device_id, String lab_device_name, String lab_device_image_url) {
        this.lab_room_id = lab_room_id;
        this.lab_device_id = lab_device_id;
        this.lab_device_name = lab_device_name;
        this.lab_device_image_url = lab_device_image_url;
    }

    public LabDevice() {
    }

    public int getLab_room_id() {
        return lab_room_id;
    }

    public void setLab_room_id(int lab_room_id) {
        this.lab_room_id = lab_room_id;
    }

    public int getLab_device_id() {
        return lab_device_id;
    }

    public void setLab_device_id(int lab_device_id) {
        this.lab_device_id = lab_device_id;
    }

    public String getLab_device_name() {
        return lab_device_name;
    }

    public void setLab_device_name(String lab_device_name) {
        this.lab_device_name = lab_device_name;
    }

    public String getLab_device_image_url() {
        return lab_device_image_url;
    }

    public void setLab_device_image_url(String lab_device_image_url) {
        this.lab_device_image_url = lab_device_image_url;
    }
}
