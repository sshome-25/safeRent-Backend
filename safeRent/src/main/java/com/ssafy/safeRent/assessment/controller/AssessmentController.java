package com.ssafy.safeRent.assessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.assessment.service.AssessmentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/houses")
@RequiredArgsConstructor
public class AssessmentController {
  private final AssessmentService assessmentService;

  @PostMapping("/register")
  public Mono<ResponseEntity<?>> analyzeProperty(@RequestParam("id") String id,
      @RequestParam("register_file") MultipartFile pdfFile) {
    System.out.println("register 컨트롤러");
    return assessmentService.analyzeProperty(pdfFile);
  }
}
