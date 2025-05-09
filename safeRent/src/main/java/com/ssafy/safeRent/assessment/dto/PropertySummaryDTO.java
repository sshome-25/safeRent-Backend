package com.ssafy.safeRent.assessment.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertySummaryDTO {
  @JsonProperty("owner")
  private OwnerDTO owner;

  @JsonProperty("ownership_history")
  private List<OwnershipHistoryDTO> ownershipHistory;

  @JsonProperty("mortgage")
  private MortgageDTO mortgage;

  @JsonProperty("summary")
  private SummaryDTO summary;

  @Getter
  @Setter
  public static class OwnerDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("registration_date")
    private String registrationDate;

    @JsonProperty("registration_number")
    private String registrationNumber;
  }

  @Getter
  @Setter
  public static class OwnershipHistoryDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("registration_date")
    private String registrationDate;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("note")
    private String note;
  }

  @Getter
  @Setter
  public static class MortgageDTO {
    @JsonProperty("bank")
    private String bank;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("date")
    private String date;

    @JsonProperty("purpose")
    private String purpose;

    @JsonProperty("registration_number")
    private String registrationNumber;
  }

  @Getter
  @Setter
  public static class SummaryDTO {
    @JsonProperty("current_owner")
    private String currentOwner;

    @JsonProperty("past_owners")
    private List<String> pastOwners;

    @JsonProperty("mortgage_status")
    private String mortgageStatus;
  }
}
