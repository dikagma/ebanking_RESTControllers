package com.bassagou.ebankingbackend.services;

import com.bassagou.ebankingbackend.dtos.*;
import com.bassagou.ebankingbackend.entities.*;
import com.bassagou.ebankingbackend.enums.OperationType;
import com.bassagou.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.bassagou.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.bassagou.ebankingbackend.exceptions.CustomerNotFoundException;
import com.bassagou.ebankingbackend.mappers.BankAccountMapperImpl;
import com.bassagou.ebankingbackend.repositories.AccountOperationRepository;
import com.bassagou.ebankingbackend.repositories.BankAccountRepository;
import com.bassagou.ebankingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountMapperImpl bankAccountMapperImpl;
    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl bankAccountMapper;
//    Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerdto) {
        log.info("Saving new customer");
       Customer customer= bankAccountMapper.fromCustomerDTO(customerdto);
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(savedCustomer) ;
    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        log.info("Saving current bank account");
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) throw new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        CurrentAccount saveBankAccount=  bankAccountRepository.save(currentAccount);
        return bankAccountMapper.fromCurrentAccount(saveBankAccount);
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        log.info("Saving saving bank account");
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) throw new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        SavingAccount saveBankAccount=  bankAccountRepository.save(savingAccount);
        return bankAccountMapperImpl.fromSavingAccount(saveBankAccount);
    }



//   public List<Customer> listCustomerss() {
//        return customerRepository.findAll();
//    }
    @Override
    public List<CustomerDTO> listCustomers(){
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream().map(customer ->bankAccountMapper.fromCustomer(customer)).collect(Collectors.toList());

//        List<CustomerDTO> customerDTOS = new ArrayList<>();
//        for (Customer customer : customers) {
//            CustomerDTO customerDTO = bankAccountMapper.fromCustomer(customer);
//            customerDTOS.add(customerDTO);
//        }
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("Account not found"));

        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return bankAccountMapperImpl.fromSavingAccount(savingAccount);
        }else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapperImpl.fromCurrentAccount(currentAccount);
        }

    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        log.info("debit bank account");
        BankAccount bankAccount = bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("Account not found"));
        if(bankAccount.getBalance() < amount) throw  new BalanceNotSufficientException("Not enough balance");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        log.info("credit bank account");
        BankAccount bankAccount = bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("Account not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
       debit(accountIdSource,amount, "Trasfert to " + accountIdDestination);
       credit(accountIdDestination,amount, "Transfer from " + accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountsList() {
       List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS=   bankAccounts.stream().map(bankAccount -> {
            if(bankAccount instanceof SavingAccount){
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return bankAccountMapper.fromSavingAccount(savingAccount);
            }else{
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return bankAccountMapper.fromCurrentAccount(currentAccount);
            }

        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }


@Override
public CustomerDTO getCustomer(Long id) throws CustomerNotFoundException {
      Customer customer= customerRepository.findById(id).
              orElseThrow(()->new CustomerNotFoundException("Customer not found"));
        return bankAccountMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerdto) {
        log.info("Saving new customer");
        Customer customer= bankAccountMapper.fromCustomerDTO(customerdto);
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(savedCustomer) ;
    }
    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccount_Id(accountId);
        List<AccountOperationDTO> accountOperationDTOs=
                accountOperations.stream().map(accountOperation -> bankAccountMapper.fromAccountOperation(accountOperation))
                        .collect(Collectors.toList());
        return accountOperationDTOs;
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElseThrow(()-> new BankAccountNotFoundException("Account not Found"));
        Page<AccountOperation> accountOperations=accountOperationRepository.findByBankAccount_Id(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO=new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> bankAccountMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPage(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }
@Override
public void virement(VirementDTO virementDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        transfer(virementDTO.getSourceCountId(), virementDTO.getDestinationCountId(), virementDTO.getAmount());
    }
}
