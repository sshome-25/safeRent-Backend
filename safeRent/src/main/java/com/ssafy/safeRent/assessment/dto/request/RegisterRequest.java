package com.ssafy.safeRent.assessment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterRequest {
	private Long assessmentId;
	// private File registerFile;
}
