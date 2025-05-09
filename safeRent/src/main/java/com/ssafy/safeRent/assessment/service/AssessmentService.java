package com.ssafy.safeRent.assessment.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.util.GrokApiClient;
import com.ssafy.safeRent.util.PDFConverter;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AssessmentService {
  private final GrokApiClient grokApiClient;
  private final PDFConverter pdfConverter;

  public Mono<ResponseEntity<?>> analyzeProperty(MultipartFile pdfFile) {
    return Mono.fromCallable(() -> pdfConverter.convertPDFToImages(pdfFile))
        .flatMap(base64Images -> {
          if (base64Images.size() > 8) {
            return Mono.just(ResponseEntity.badRequest()
                .body("PDF cannot exceed 8 pages"));
          }
          if (base64Images.isEmpty()) {
            return Mono.just(ResponseEntity.notFound().build());
          }
          return grokApiClient.analyzePropertyImages(base64Images)
              .map(response -> ResponseEntity.ok(response.getChoices().stream()
                  .map(choice -> choice.getMessage().getContentAsPropertySummary())
                  .toList()))
              .defaultIfEmpty(ResponseEntity.notFound().build());
        })
        .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.badRequest()
            .body(e.getMessage())))
        .onErrorResume(Exception.class, e -> Mono.just(ResponseEntity.badRequest()
            .body("Failed to process PDF file: " + e.getMessage())));
  }

  // public Mono<ResponseEntity<?>> analyzeProperty(MultipartFile pdfFile) {
  // return Mono.fromCallable(() -> pdfConverter.convertPDFToImages(pdfFile))
  // .flatMap(base64Images -> {
  //
  // if (base64Images.size() > 8) {
  // return Mono.just(ResponseEntity.badRequest()
  // .body("PDF cannot exceed 8 pages"));
  // }
  // if (base64Images.isEmpty()) {
  // return Mono.just(ResponseEntity.notFound().build());
  // }
  // return grokApiClient.analyzePropertyImages(base64Images)
  // .map(grokResponse -> {
  // // Grok 응답 텍스트를 그대로 반환
  // return ResponseEntity.ok(Map.of(
  // "imageValidationResponse", grokResponse));
  // })
  // .defaultIfEmpty(ResponseEntity.notFound().build());
  // })
  // .onErrorResume(IllegalArgumentException.class, e ->
  // Mono.just(ResponseEntity.badRequest()
  // .body(e.getMessage())))
  // .onErrorResume(Exception.class, e -> Mono.just(ResponseEntity.badRequest()
  // .body("Failed to process PDF file: " + e.getMessage())));
  // }

}
