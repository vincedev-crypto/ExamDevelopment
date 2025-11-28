package com.thesis.exam.service;

import com.thesis.exam.model.Subject;
import com.thesis.exam.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
    
    public Subject getSubject(Long id) {
        return subjectRepository.findById(id).orElse(null);
    }
}
