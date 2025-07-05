package com.bassagou.ebankingbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VirementDTO {
    @NotBlank(message="The source account is required.")
    String sourceCountId;
    @NotBlank(message="The destination account is required.")
    String destinationCountId;
    @Positive(message = "The amount must be strictly positive.")
    double amount;
}
