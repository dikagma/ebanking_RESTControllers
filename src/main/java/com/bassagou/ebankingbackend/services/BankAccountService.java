package com.bassagou.ebankingbackend.services;

import com.bassagou.ebankingbackend.dtos.*;
import com.bassagou.ebankingbackend.entities.BankAccount;
import com.bassagou.ebankingbackend.entities.CurrentAccount;
import com.bassagou.ebankingbackend.entities.Customer;
import com.bassagou.ebankingbackend.entities.SavingAccount;
import com.bassagou.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.bassagou.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.bassagou.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customer);
    CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
   // List<Customer> listCustomers();

    List<CustomerDTO> listCustomers();

    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;


    List<BankAccountDTO> bankAccountsList();

    CustomerDTO getCustomer(Long id) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerdto);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    void virement(VirementDTO virementDTO) throws BankAccountNotFoundException, BalanceNotSufficientException;
}
