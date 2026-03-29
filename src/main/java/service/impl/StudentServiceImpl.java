package service.impl;

import dao.StudentDao;
import entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.StudentService;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentDao studentDao;

    @Override
    public void addStudent(Student student) {

        if (student.getStuNo() == null || student.getStuNo().trim().isEmpty()) {
            throw new RuntimeException("学号不能为空");
        }
        studentDao.insertStudent(student);
    }


    @Override
    public List<Student> findAllStudents() {

        return studentDao.selectAllStudent();
    }
}
