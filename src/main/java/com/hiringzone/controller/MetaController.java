package com.hiringzone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meta")
public class MetaController {

    public static final List<String> JOB_TYPES = List.of(
            "Full-time", "Part-time", "Contract", "Internship", "Remote");

    public static final List<String> EXPERIENCE_LEVELS = List.of(
            "Entry Level", "Mid Level", "Senior Level", "Lead", "Manager", "Director");

    public static final List<String> CATEGORIES = List.of(
            "Technology", "Design", "Marketing", "Finance", "Healthcare",
            "Sales", "Education", "Engineering", "Operations", "Other");

    public static final List<String> POPULAR_TAGS = List.of(
            "Remote", "Python", "React", "Full-Stack", "JavaScript",
            "Java", "Design", "Marketing", "Node.js", "DevOps");

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMeta() {
        return ResponseEntity.ok(Map.of(
                "jobTypes", JOB_TYPES,
                "experienceLevels", EXPERIENCE_LEVELS,
                "categories", CATEGORIES,
                "popularTags", POPULAR_TAGS
        ));
    }
}
