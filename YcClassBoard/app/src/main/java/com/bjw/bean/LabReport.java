package com.bjw.bean;

/**
 * 创建人：wxdn
 * 创建时间：2018/2/28
 * 功能描述：
 */

public class LabReport {
    String Week;
    int WeekStudentNum;
    int LabId;

    public LabReport(String week, int weekStudentNum, int labId) {
        Week = week;
        WeekStudentNum = weekStudentNum;
        LabId = labId;
    }

    public LabReport() {
    }

    public String getWeek() {
        return Week;
    }

    public void setWeek(String week) {
        Week = week;
    }

    public int getWeekStudentNum() {
        return WeekStudentNum;
    }

    public void setWeekStudentNum(int weekStudentNum) {
        WeekStudentNum = weekStudentNum;
    }

    public int getLabId() {
        return LabId;
    }

    public void setLabId(int labId) {
        LabId = labId;
    }
}
