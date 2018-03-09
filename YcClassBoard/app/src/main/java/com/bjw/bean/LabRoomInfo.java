package com.bjw.bean;

/**
 * 创建人：wxdn
 * 创建时间：2017/11/13
 * 功能描述：
 */

public class LabRoomInfo {
    int id;
    String lab_name;
    double lab_room_area;
    int lab_room_capacity;
    String manager_name;
    String lab_image_url;
    String emergency_telephone;

    public LabRoomInfo(int id, String lab_name, double lab_room_area, int lab_room_capacity, String manager_name, String lab_image_url, String emergency_telephone) {
        this.id = id;
        this.lab_name = lab_name;
        this.lab_room_area = lab_room_area;
        this.lab_room_capacity = lab_room_capacity;
        this.manager_name = manager_name;
        this.lab_image_url = lab_image_url;
        this.emergency_telephone = emergency_telephone;
    }

    public LabRoomInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLab_name() {
        return lab_name;
    }

    public void setLab_name(String lab_name) {
        this.lab_name = lab_name;
    }

    public double getLab_room_area() {
        return lab_room_area;
    }

    public void setLab_room_area(double lab_room_area) {
        this.lab_room_area = lab_room_area;
    }

    public int getLab_room_capacity() {
        return lab_room_capacity;
    }

    public void setLab_room_capacity(int lab_room_capacity) {
        this.lab_room_capacity = lab_room_capacity;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getLab_image_url() {
        return lab_image_url;
    }

    public void setLab_image_url(String lab_image_url) {
        this.lab_image_url = lab_image_url;
    }

    public String getEmergency_telephone() {
        return emergency_telephone;
    }

    public void setEmergency_telephone(String emergency_telephone) {
        this.emergency_telephone = emergency_telephone;
    }
}
