package com.bjw.bean;

/**
 * 创建人：zxj
 * 创建时间：2017/11/21
 * 功能描述：
 */

public class StudentCard {
    String student_name;
    String student_number;
    String card_no;
    int lab_room_id;
    String course_number;
    int start_class;
    int end_class;

    public StudentCard(String student_name, String student_number, String card_no, int lab_room_id, String course_number) {
        this.student_name = student_name;
        this.student_number = student_number;
        this.card_no = card_no;
        this.lab_room_id = lab_room_id;
        this.course_number = course_number;
    }

    public StudentCard(String student_name, String student_number, String card_no, int lab_room_id, String course_number, int start_class, int end_class) {
        this.student_name = student_name;
        this.student_number = student_number;
        this.card_no = card_no;
        this.lab_room_id = lab_room_id;
        this.course_number = course_number;
        this.start_class = start_class;
        this.end_class = end_class;
    }

    public int getStart_class() {
        return start_class;
    }

    public void setStart_class(int start_class) {
        this.start_class = start_class;
    }

    public int getEnd_class() {
        return end_class;
    }

    public void setEnd_class(int end_class) {
        this.end_class = end_class;
    }

    public StudentCard() {
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_number() {
        return student_number;
    }

    public void setStudent_number(String student_number) {
        this.student_number = student_number;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public int getLab_room_id() {
        return lab_room_id;
    }

    public void setLab_room_id(int lab_room_id) {
        this.lab_room_id = lab_room_id;
    }

    public String getCourse_number() {
        return course_number;
    }

    public void setCourse_number(String course_number) {
        this.course_number = course_number;
    }
}
