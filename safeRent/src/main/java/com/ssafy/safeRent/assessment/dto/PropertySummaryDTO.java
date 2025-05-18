package com.ssafy.safeRent.assessment.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertySummaryDTO {
  @JsonProperty("property")
  private PropertyDTO property;

  @JsonProperty("owners")
  private List<OwnerDTO> owners;

  @JsonProperty("ownership_history")
  private List<OwnershipHistoryDTO> ownershipHistory;

  @JsonProperty("mortgages")
  private List<MortgageDTO> mortgages;

  @JsonProperty("other_rights")
  private List<OtherRightDTO> otherRights;

  @JsonProperty("special_notes")
  private List<String> specialNotes;

  @JsonProperty("summary")
  private SummaryDTO summary;

  @Getter
  @Setter
  public static class PropertyDTO {
    @JsonProperty("address")
    private String address;

    @JsonProperty("type")
    private String type;

    @JsonProperty("parcel_number")
    private String parcelNumber;

    @JsonProperty("land_number")
    private String landNumber;

    @JsonProperty("building_area")
    private String buildingArea;

    @JsonProperty("land_area")
    private String landArea;

    @JsonProperty("purpose")
    private String purpose;

    @JsonProperty("floor")
    private String floor;
  }

  @Getter
  @Setter
  public static class OwnerDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("acquisition_date")
    private String acquisitionDate;

    @JsonProperty("acquisition_reason")
    private String acquisitionReason;

    @JsonProperty("share")
    private String share;
  }

  @Getter
  @Setter
  public static class OwnershipHistoryDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("acquisition_date")
    private String acquisitionDate;

    @JsonProperty("acquisition_reason")
    private String acquisitionReason;

    @JsonProperty("share")
    private String share;
  }

  @Getter
  @Setter
  public static class MortgageDTO {
    @JsonProperty("holder")
    private String holder;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("registration_date")
    private String registrationDate;

    @JsonProperty("purpose")
    private String purpose;

    @JsonProperty("registration_number")
    private String registrationNumber;
  }

  @Getter
  @Setter
  public static class OtherRightDTO {
    @JsonProperty("type")
    private String type;

    @JsonProperty("holder")
    private String holder;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("registration_date")
    private String registrationDate;

    @JsonProperty("expiration_date")
    private String expirationDate;
  }

  @Getter
  @Setter
  public static class SummaryDTO {
    @JsonProperty("current_owner")
    private String currentOwner;

    @JsonProperty("mortgage_status")
    private String mortgageStatus;

    @JsonProperty("other_rights_status")
    private String otherRightsStatus;
  }
}