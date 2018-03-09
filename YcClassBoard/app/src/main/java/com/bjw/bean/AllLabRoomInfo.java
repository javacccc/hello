package com.bjw.bean;

/**
 * 创建人：wxdn
 * 创建时间：2017/12/18
 * 功能描述：
 */

public class AllLabRoomInfo {
    int lab_room_id;
    int lab_room_local_image;
    String lab_room_name;
    String lab_room_image_url;
    String lab_room_location;

    public AllLabRoomInfo() {
    }

    public AllLabRoomInfo(int lab_room_id, int lab_room_local_image, String lab_room_name, String lab_room_image_url, String lab_room_location) {
        this.lab_room_id = lab_room_id;
        this.lab_room_local_image = lab_room_local_image;
        this.lab_room_name = lab_room_name;
        this.lab_room_image_url = lab_room_image_url;
        this.lab_room_location = lab_room_location;
    }

    public AllLabRoomInfo(int lab_room_local_image, String lab_room_location) {
        this.lab_room_local_image = lab_room_local_image;
        this.lab_room_location = lab_room_location;
    }

    public AllLabRoomInfo(int lab_room_id, int lab_room_local_image, String lab_room_location) {
        this.lab_room_id = lab_room_id;
        this.lab_room_local_image = lab_room_local_image;
        this.lab_room_location = lab_room_location;
    }

    public String getLab_room_location() {
        return lab_room_location;
    }

    public void setLab_room_location(String lab_room_location) {
        this.lab_room_location = lab_room_location;
    }

    public int getLab_room_id() {
        return lab_room_id;
    }

    public void setLab_room_id(int lab_room_id) {
        this.lab_room_id = lab_room_id;
    }

    public int getLab_room_local_image() {
        return lab_room_local_image;
    }

    public void setLab_room_local_image(int lab_room_local_image) {
        this.lab_room_local_image = lab_room_local_image;
    }

    public String getLab_room_name() {
        return lab_room_name;
    }

    public void setLab_room_name(String lab_room_name) {
        this.lab_room_name = lab_room_name;
    }

    public String getLab_room_image_url() {
        return lab_room_image_url;
    }

    public void setLab_room_image_url(String lab_room_image_url) {
        this.lab_room_image_url = lab_room_image_url;
    }
}
