package service;
import entity.Student;
import java.util.List;
public interface StudentService {
    void addStudent(Student student);
    List<Student> findAllStudents();
}
