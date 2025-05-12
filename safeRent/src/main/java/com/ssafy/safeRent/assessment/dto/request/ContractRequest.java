package com.ssafy.safeRent.assessment.dto.request;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ContractRequest {
	private Integer assessmentId;
	private File contractFile;
}
