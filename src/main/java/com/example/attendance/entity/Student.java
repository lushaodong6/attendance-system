package com.example.attendance.entity;

import java.time.LocalDate;
import java.time.Period;

/**
 * 学生实体类 - 增强版
 */
public class Student {

    private Integer id;           // 学生ID
    private String studentNo;     // 学号（新增）
    private String name;          // 姓名
    private String gender;        // 性别
    private LocalDate birthDate;  // 出生日期（新增，替代年龄）
    private String phone;         // 联系方式
    private String email;         // 邮箱
    private String address;       // 地址

    // 无参构造
    public Student() {}

    // 有参构造
    public Student(String studentNo, String name, String gender,
                   LocalDate birthDate, String phone, String email, String address) {
        this.studentNo = studentNo;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // ==================== Getter 和 Setter ====================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // 计算年龄（根据出生日期自动计算）
    public int getAge() {
        if (birthDate != null) {
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentNo='" + studentNo + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}