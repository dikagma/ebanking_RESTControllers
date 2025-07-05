package com.bassagou.ebankingbackend.dtos;

import com.bassagou.ebankingbackend.enums.AccountStatus;
import lombok.Data;

import java.util.Date;

@Data
public class CurrentAccountDTO extends BankAccountDTO  {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private String currency;
    private CustomerDTO customerDTO;
    private double overDraft;
}
