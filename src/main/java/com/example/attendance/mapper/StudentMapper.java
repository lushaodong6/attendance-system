package com.example.attendance.mapper;

import com.example.attendance.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {

    /**
     * 新增学生
     */
    @Insert("INSERT INTO student(student_no, name, gender, birth_date, phone, email, address) " +
            "VALUES(#{studentNo}, #{name}, #{gender}, #{birthDate}, #{phone}, #{email}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Student student);

    /**
     * 修改学生信息
     */
    @Update("UPDATE student SET student_no=#{studentNo}, name=#{name}, gender=#{gender}, " +
            "birth_date=#{birthDate}, phone=#{phone}, email=#{email}, address=#{address} " +
            "WHERE id=#{id}")
    int update(Student student);

    /**
     * 根据ID删除学生
     */
    @Delete("DELETE FROM student WHERE id=#{id}")
    int delete(Integer id);

    /**
     * 批量删除学生
     */
    @Delete("<script>" +
            "DELETE FROM student WHERE id IN " +
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDelete(@Param("list") List<Integer> ids);

    /**
     * 根据ID查询学生
     */
    @Select("SELECT * FROM student WHERE id=#{id}")
    Student findById(Integer id);

    /**
     * 查询所有学生
     */
    @Select("SELECT * FROM student ORDER BY id DESC")
    List<Student> findAll();

    /**
     * 按学号搜索学生
     */
    @Select("SELECT * FROM student WHERE student_no LIKE CONCAT('%', #{keyword}, '%') " +
            "OR name LIKE CONCAT('%', #{keyword}, '%') ORDER BY id DESC")
    List<Student> search(@Param("keyword") String keyword);

    /**
     * 按学号排序
     */
    @Select("SELECT * FROM student ORDER BY student_no ASC")
    List<Student> findAllOrderByStudentNo();

    /**
     * 按姓名排序
     */
    @Select("SELECT * FROM student ORDER BY CONVERT(name USING GBK) ASC")
    List<Student> findAllOrderByName();
}