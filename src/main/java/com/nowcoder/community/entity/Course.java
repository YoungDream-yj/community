package com.nowcoder.community.entity;

public class Course {

    private int id;
    private String courseName;
    private String courseTitle;
    private String courseImg;
    private String courseDetail;
    private Double coursePrice;
    private Integer courseStock;
    private Integer courseSales;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseImg() {
        return courseImg;
    }

    public void setCourseImg(String courseImg) {
        this.courseImg = courseImg;
    }

    public String getCourseDetail() {
        return courseDetail;
    }

    public void setCourseDetail(String courseDetail) {
        this.courseDetail = courseDetail;
    }

    public Double getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(Double coursePrice) {
        this.coursePrice = coursePrice;
    }

    public Integer getCourseStock() {
        return courseStock;
    }

    public void setCourseStock(Integer courseStock) {
        this.courseStock = courseStock;
    }

    public Integer getCourseSales() {
        return courseSales;
    }

    public void setCourseSales(Integer courseSales) {
        this.courseSales = courseSales;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", courseTitle='" + courseTitle + '\'' +
                ", courseImg='" + courseImg + '\'' +
                ", courseDetail='" + courseDetail + '\'' +
                ", coursePrice=" + coursePrice +
                ", courseStock=" + courseStock +
                ", courseSales=" + courseSales +
                '}';
    }
}
