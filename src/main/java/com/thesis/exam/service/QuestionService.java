package com.thesis.exam.service;

import com.thesis.exam.model.Question;
import com.thesis.exam.model.Difficulty;
import com.thesis.exam.model.Subject;
import com.thesis.exam.repository.QuestionRepository;
import com.thesis.exam.repository.SubjectRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void saveQuestionsFromCsv(MultipartFile file) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setTrim(true)
                .setIgnoreSurroundingSpaces(true)
                .setIgnoreEmptyLines(true)
                .build();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {

            for (CSVRecord record : parser) {
                if (record.size() == 0) {
                    continue;
                }

                if (record.getRecordNumber() == 1 && looksLikeHeader(record)) {
                    continue;
                }

                if (record.size() < 4) {
                    continue; // skip malformed rows
                }

                Question question = new Question();
                question.setContent(record.get(0).trim());

                try {
                    question.setDifficulty(Difficulty.valueOf(record.get(1).trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    question.setDifficulty(Difficulty.MEDIUM);
                }

                String subjectName = record.get(2).trim();
                Subject subject = subjectRepository.findByName(subjectName);
                if (subject == null) {
                    subject = new Subject();
                    subject.setName(subjectName);
                    subjectRepository.save(subject);
                }
                question.setSubject(subject);

                question.setCorrectAnswer(record.get(3).trim());
                questionRepository.save(question);
            }
        }
    }

    private boolean looksLikeHeader(CSVRecord record) {
        if (record.size() < 4) {
            return false;
        }

        return record.get(0).equalsIgnoreCase("content")
                && record.get(1).toLowerCase().contains("difficulty")
                && record.get(2).toLowerCase().contains("subject")
                && record.get(3).toLowerCase().contains("answer");
    }
}
