package com.thesis.exam.service;

import com.thesis.exam.model.Student;
import com.thesis.exam.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public Student getStudent(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student registerStudent(Student student) {
        // In a real app, password should be hashed here
        return studentRepository.save(student);
    }

    public Student authenticate(String email, String password) {
        return studentRepository.findByEmail(email)
                .filter(s -> s.getPassword().equals(password))
                .orElse(null);
    }
}
