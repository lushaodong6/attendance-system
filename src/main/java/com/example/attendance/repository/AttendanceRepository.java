package com.example.attendance.repository;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByStudentAndDate(Student student, LocalDate date);
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.student.className = :className AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByClassNameAndDateBetween(@Param("className") String className,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}