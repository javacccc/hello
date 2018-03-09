package com.bjw.bean;

/**
 * 创建人：wxdn
 * 创建时间：2017/11/17
 * 功能描述：
 */

public class LessonTable {
    int lab_room_id;
    int course_present_people;
    int start_class;
    int end_class;
    int total_hour;
    String course_name;
    String course_number;
    String course_image_url;
    String course_teacher_name;
    String course_beginning_time;
    String course_ending_time;
    String classes_number;
    String course_program;
    String course_date;

    public LessonTable(int lab_room_id, int course_present_people, int start_class, int end_class, int total_hour, String course_name, String course_number, String course_image_url, String course_teacher_name, String course_beginning_time, String course_ending_time, String classes_number, String course_program, String course_date) {
        this.lab_room_id = lab_room_id;
        this.course_present_people = course_present_people;
        this.start_class = start_class;
        this.end_class = end_class;
        this.total_hour = total_hour;
        this.course_name = course_name;
        this.course_number = course_number;
        this.course_image_url = course_image_url;
        this.course_teacher_name = course_teacher_name;
        this.course_beginning_time = course_beginning_time;
        this.course_ending_time = course_ending_time;
        this.classes_number = classes_number;
        this.course_program = course_program;
        this.course_date = course_date;
    }

    public LessonTable() {
    }

    public int getLab_room_id() {
        return lab_room_id;
    }

    public void setLab_room_id(int lab_room_id) {
        this.lab_room_id = lab_room_id;
    }

    public int getCourse_present_people() {
        return course_present_people;
    }

    public void setCourse_present_people(int course_present_people) {
        this.course_present_people = course_present_people;
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

    public int getTotal_hour() {
        return total_hour;
    }

    public void setTotal_hour(int total_hour) {
        this.total_hour = total_hour;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getCourse_number() {
        return course_number;
    }

    public void setCourse_number(String course_number) {
        this.course_number = course_number;
    }

    public String getCourse_image_url() {
        return course_image_url;
    }

    public void setCourse_image_url(String course_image_url) {
        this.course_image_url = course_image_url;
    }

    public String getCourse_teacher_name() {
        return course_teacher_name;
    }

    public void setCourse_teacher_name(String course_teacher_name) {
        this.course_teacher_name = course_teacher_name;
    }

    public String getCourse_beginning_time() {
        return course_beginning_time;
    }

    public void setCourse_beginning_time(String course_beginning_time) {
        this.course_beginning_time = course_beginning_time;
    }

    public String getCourse_ending_time() {
        return course_ending_time;
    }

    public void setCourse_ending_time(String course_ending_time) {
        this.course_ending_time = course_ending_time;
    }

    public String getClasses_number() {
        return classes_number;
    }

    public void setClasses_number(String classes_number) {
        this.classes_number = classes_number;
    }

    public String getCourse_program() {
        return course_program;
    }

    public void setCourse_program(String course_program) {
        this.course_program = course_program;
    }

    public String getCourse_date() {
        return course_date;
    }

    public void setCourse_date(String course_date) {
        this.course_date = course_date;
    }
}
