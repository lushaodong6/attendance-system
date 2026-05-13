package com.example.attendance.service;

import com.example.attendance.entity.Student;
import com.example.attendance.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 新增学生
     */
    public boolean addStudent(Student student) {
        try {
            int result = studentMapper.insert(student);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改学生信息
     */
    public boolean updateStudent(Student student) {
        try {
            int result = studentMapper.update(student);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除学生
     */
    public boolean deleteStudent(Integer id) {
        try {
            int result = studentMapper.delete(id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批量删除学生
     */
    public boolean batchDeleteStudents(List<Integer> ids) {
        try {
            int result = studentMapper.batchDelete(ids);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID查询学生
     */
    public Student getStudentById(Integer id) {
        return studentMapper.findById(id);
    }

    /**
     * 查询所有学生
     */
    public List<Student> getAllStudents() {
        return studentMapper.findAll();
    }

    /**
     * 按关键词搜索学生
     */
    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return studentMapper.findAll();
        }
        return studentMapper.search(keyword.trim());
    }

    /**
     * 按学号排序
     */
    public List<Student> getAllStudentsOrderByStudentNo() {
        return studentMapper.findAllOrderByStudentNo();
    }

    /**
     * 按姓名排序
     */
    public List<Student> getAllStudentsOrderByName() {
        return studentMapper.findAllOrderByName();
    }
}
