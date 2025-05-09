package com.ssafy.safeRent.assessment.dto;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatCompletionDTO {

  @JsonProperty("id")
  private String id;

  @JsonProperty("object")
  private String object;

  @JsonProperty("created")
  private long created;

  @JsonProperty("model")
  private String model;

  @JsonProperty("choices")
  private List<ChoiceDTO> choices;

  @Getter
  @Setter
  public static class ChoiceDTO {
    @JsonProperty("index")
    private int index;

    @JsonProperty("message")
    private MessageDTO message;

    @JsonProperty("finish_reason")
    private String finishReason;
  }

  @Getter
  @Setter
  public static class MessageDTO {
    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private String content;

    @JsonProperty("refusal")
    private Object refusal;

    public PropertySummaryDTO getContentAsPropertySummary() {
      ObjectMapper mapper = new ObjectMapper();
      try {
        String jsonContent = content
            .replaceAll("```json\\s*", "")
            .replaceAll("\\s*```", "")
            .trim();
        return mapper.readValue(jsonContent, PropertySummaryDTO.class);
      } catch (IOException e) {
        throw new RuntimeException("Failed to parse content as PropertySummaryDTO", e);
      }
    }
  }
}
