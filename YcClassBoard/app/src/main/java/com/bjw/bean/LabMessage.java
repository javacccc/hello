package com.bjw.bean;

/**
 * 创建人：wxdn
 * 创建时间：2017/12/16
 * 功能描述：
 */

public class LabMessage {
    int lab_id;
    String lab_name;
    String title;
    String content;
    String submit_by;
    String submit_time;
//    String view_count;
//    String info;


    public LabMessage(int lab_id, String lab_name, String title, String content) {
        this.lab_id = lab_id;
        this.lab_name = lab_name;
        this.title = title;
        this.content = content;
    }

    public LabMessage(int lab_id, String lab_name, String title, String content, String submit_by, String submit_time) {
        this.lab_id = lab_id;
        this.lab_name = lab_name;
        this.title = title;
        this.content = content;
        this.submit_by = submit_by;
        this.submit_time = submit_time;
    }

    public LabMessage() {
    }

    public String getSubmit_by() {
        return submit_by;
    }

    public void setSubmit_by(String submit_by) {
        this.submit_by = submit_by;
    }

    public String getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(String submit_time) {
        this.submit_time = submit_time;
    }

    public int getLab_id() {
        return lab_id;
    }

    public void setLab_id(int lab_id) {
        this.lab_id = lab_id;
    }

    public String getLab_name() {
        return lab_name;
    }

    public void setLab_name(String lab_name) {
        this.lab_name = lab_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



}
