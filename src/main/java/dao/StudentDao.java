package dao;

import entity.Student;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentDao {
    private List<Student> studentList = new ArrayList<>();

    public void insertStudent(Student student) {
        studentList.add(student);
    }


    public List<Student> selectAllStudent() {
        return studentList;
    }
}