package com.thesis.exam.controller;

import com.thesis.exam.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "upload-question";
    }

    @PostMapping("/upload-file")
    public String uploadQuestionFile(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload.");
        } else if (!isCsvFile(file)) {
            model.addAttribute("error", "Only .csv files exported as UTF-8 text are supported. Please re-export the file as CSV.");
        } else {
            try {
                questionService.saveQuestionsFromCsv(file);
                model.addAttribute("message", "Questions uploaded successfully from file!");
            } catch (Exception e) {
                model.addAttribute("error", "Error uploading file: " + e.getMessage());
            }
        }
        
        return "upload-question";
    }

    @GetMapping("/download-sample")
    public ResponseEntity<Resource> downloadSampleQuestions() {
        ByteArrayResource resource = new ByteArrayResource(SAMPLE_CSV.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample-questions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/list")
    public String listQuestions(Model model) {
        model.addAttribute("questions", questionService.getAllQuestions());
        return "question-list";
    }

    private boolean isCsvFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        boolean hasCsvExtension = filename != null && filename.toLowerCase().endsWith(".csv");
        boolean looksLikeCsvMime = contentType == null || contentType.contains("csv") || contentType.startsWith("text/");

        return hasCsvExtension && looksLikeCsvMime;
    }

    private static final String SAMPLE_CSV = "Content,Difficulty,Subject,CorrectAnswer\n" +
            "What does CPU stand for?,EASY,Computer Fundamentals,Central Processing Unit\n" +
            "Which of the following is an input device?,EASY,Computer Hardware,Keyboard\n" +
            "What is the binary equivalent of decimal 10?,EASY,Number Systems,1010\n" +
            "Which protocol is used for sending emails?,EASY,Networking,SMTP\n" +
            "What does HTML stand for?,EASY,Web Development,HyperText Markup Language\n" +
            "What is the main function of RAM in a computer?,EASY,Computer Hardware,Temporary data storage\n" +
            "Which company developed the Java programming language?,EASY,Programming,Sun Microsystems\n" +
            "What does URL stand for?,EASY,Internet,Uniform Resource Locator\n" +
            "Which key combination is used to copy text in Windows?,EASY,Operating Systems,Ctrl+C\n" +
            "What is the default port number for HTTP?,EASY,Networking,80\n" +
            "What is the time complexity of binary search?,MEDIUM,Data Structures,O(log n)\n" +
            "Which sorting algorithm has the best average-case time complexity?,MEDIUM,Algorithms,Quick Sort\n" +
            "What is the purpose of a firewall in network security?,MEDIUM,Cybersecurity,To control incoming and outgoing network traffic\n" +
            "Explain the difference between TCP and UDP protocols.,MEDIUM,Networking,TCP is connection-oriented while UDP is connectionless\n" +
            "What is polymorphism in object-oriented programming?,MEDIUM,Programming,Ability of objects to take multiple forms\n" +
            "What does SQL stand for and what is it used for?,MEDIUM,Database,Structured Query Language for managing databases\n" +
            "What is the difference between a compiler and an interpreter?,MEDIUM,Programming,Compiler translates entire code at once; interpreter line by line\n" +
            "What is the purpose of DNS in networking?,MEDIUM,Networking,Translates domain names to IP addresses\n" +
            "Explain what an IP address is and its format.,MEDIUM,Networking,Unique identifier for devices on a network (IPv4: xxx.xxx.xxx.xxx)\n" +
            "What is the difference between HTTP and HTTPS?,MEDIUM,Web Development,HTTPS is secure version of HTTP using SSL/TLS encryption\n" +
            "What is a primary key in a database?,MEDIUM,Database,Unique identifier for each record in a table\n" +
            "Which data structure uses LIFO principle?,MEDIUM,Data Structures,Stack\n" +
            "What is the purpose of CSS in web development?,MEDIUM,Web Development,Styling and layout of web pages\n" +
            "What is an operating system? Name three examples.,MEDIUM,Operating Systems,Software that manages hardware and software resources (Windows Linux macOS)\n" +
            "What is cloud computing?,MEDIUM,Cloud Technology,Delivery of computing services over the internet\n" +
            "Explain the concept of virtual memory.,MEDIUM,Operating Systems,Using hard disk space as extended RAM\n" +
            "What is the difference between IPv4 and IPv6?,MEDIUM,Networking,IPv6 has larger address space (128-bit vs 32-bit)\n" +
            "What is a loop in programming? Give an example.,MEDIUM,Programming,Repeating structure (for while do-while)\n" +
            "What does API stand for and what is its purpose?,MEDIUM,Software Development,Application Programming Interface for software communication\n" +
            "What is the OSI model and how many layers does it have?,MEDIUM,Networking,7-layer model for network communication\n" +
            "Explain the CAP theorem in distributed systems.,HARD,Distributed Systems,Cannot simultaneously guarantee Consistency Availability and Partition tolerance\n" +
            "Implement Dijkstra's shortest path algorithm.,HARD,Algorithms,Graph algorithm for finding shortest paths from source to all vertices\n" +
            "What is the Byzantine Generals Problem in distributed computing?,HARD,Distributed Systems,Problem of achieving consensus with faulty or malicious nodes\n" +
            "Explain how blockchain technology ensures data integrity.,HARD,Blockchain,Using cryptographic hashing and distributed consensus mechanisms\n" +
            "Describe the working of a garbage collector in Java.,HARD,Programming,Automatic memory management that removes unreferenced objects\n" +
            "What is the difference between normalization and denormalization in databases?,HARD,Database,Normalization reduces redundancy; denormalization improves read performance\n" +
            "Explain the concept of thread synchronization and deadlock.,HARD,Operating Systems,Coordinating thread access to prevent race conditions and circular waiting\n" +
            "What is the time and space complexity of merge sort?,HARD,Algorithms,Time: O(n log n) Space: O(n)\n" +
            "Describe the RSA encryption algorithm and its mathematical basis.,HARD,Cryptography,Public-key cryptography based on prime factorization difficulty\n" +
            "Explain how DNS lookup works with recursive and iterative queries.,HARD,Networking,Recursive: DNS server queries on behalf; Iterative: client follows referrals\n" +
            "What is the difference between horizontal and vertical scaling?,HARD,System Design,Horizontal: add more machines; Vertical: add more power to existing machine\n" +
            "Explain the ACID properties in database transactions.,HARD,Database,Atomicity Consistency Isolation Durability\n" +
            "Describe how a hash table handles collisions.,HARD,Data Structures,Chaining (linked lists) or open addressing (linear/quadratic probing)\n" +
            "What is the difference between supervised and unsupervised machine learning?,HARD,Artificial Intelligence,Supervised uses labeled data; unsupervised finds patterns in unlabeled data\n" +
            "Explain how public-key cryptography works.,HARD,Cryptography,Uses pair of keys (public/private) for encryption and decryption\n" +
            "What is Big O notation and why is it important?,HARD,Algorithms,Describes algorithm efficiency and scalability in terms of input size\n" +
            "Describe the working principle of a load balancer.,HARD,System Design,Distributes network traffic across multiple servers for reliability and performance\n" +
            "What is SQL injection and how can it be prevented?,HARD,Cybersecurity,Malicious SQL code injection; prevented by parameterized queries and input validation\n" +
            "Explain the concept of microservices architecture.,HARD,Software Architecture,Application as collection of loosely coupled independently deployable services\n" +
            "Describe how HTTPS establishes a secure connection (TLS handshake).,HARD,Network Security,Uses SSL/TLS certificates and encryption key exchange for secure communication";
}
