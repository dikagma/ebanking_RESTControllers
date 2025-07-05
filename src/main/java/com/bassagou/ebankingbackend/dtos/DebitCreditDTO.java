package com.bassagou.ebankingbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DebitCreditDTO {
    @NotBlank(message="The  account is required.")
    String accountId;
    @Positive(message = "The amount must be strictly positive.")
    double amount;
    @NotBlank(message="Mandatory description.")
    String description;
}
