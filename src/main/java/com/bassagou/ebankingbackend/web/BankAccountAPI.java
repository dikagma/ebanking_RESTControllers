package com.bassagou.ebankingbackend.web;

import com.bassagou.ebankingbackend.dtos.*;
import com.bassagou.ebankingbackend.entities.AccountOperation;
import com.bassagou.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.bassagou.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.bassagou.ebankingbackend.services.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BankAccountAPI {

    private BankAccountService bankAccountService;
    public BankAccountAPI(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }
    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
      return bankAccountService.getBankAccount(accountId);
    }
    @GetMapping("/accounts")
    public List<BankAccountDTO> getAllBankAccounts() {
        return  bankAccountService.bankAccountsList();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId) {
       return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(@PathVariable String accountId,
                                               @RequestParam(name="page", defaultValue = "0") int page,
                                               @RequestParam(name="size", defaultValue = "5") int size) throws BankAccountNotFoundException {

        return bankAccountService.getAccountHistory(accountId, page, size);
    }
    @PostMapping("/accounts/transfer")
    public void virememt(@Valid @RequestBody VirementDTO virementDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.virement(virementDTO);
    }
    @PostMapping("/accounts/depot")
    public void depot(@Valid DebitCreditDTO debitCreditDTO) throws BankAccountNotFoundException {
        bankAccountService.credit(debitCreditDTO.getAccountId(),debitCreditDTO.getAmount(),debitCreditDTO.getDescription());
    }

    @PostMapping("/accounts/retrait")
    public void retrait(@Valid DebitCreditDTO debitCreditDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.debit(debitCreditDTO.getAccountId(),debitCreditDTO.getAmount(),debitCreditDTO.getDescription());
    }
}
