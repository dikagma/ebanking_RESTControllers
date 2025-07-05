package com.bassagou.ebankingbackend;

import com.bassagou.ebankingbackend.dtos.BankAccountDTO;
import com.bassagou.ebankingbackend.dtos.CurrentAccountDTO;
import com.bassagou.ebankingbackend.dtos.CustomerDTO;
import com.bassagou.ebankingbackend.dtos.SavingAccountDTO;
import com.bassagou.ebankingbackend.entities.*;
import com.bassagou.ebankingbackend.enums.AccountStatus;
import com.bassagou.ebankingbackend.enums.OperationType;
import com.bassagou.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.bassagou.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.bassagou.ebankingbackend.exceptions.CustomerNotFoundException;
import com.bassagou.ebankingbackend.repositories.AccountOperationRepository;
import com.bassagou.ebankingbackend.repositories.BankAccountRepository;
import com.bassagou.ebankingbackend.repositories.CustomerRepository;
import com.bassagou.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }
    @Bean
    CommandLineRunner started(BankAccountService bankAccountService) {
        return args -> {
        Stream.of("Hassan", "Yassine", "Aicha").forEach(name -> {
            CustomerDTO customer = new CustomerDTO();
            customer.setName(name);
            customer.setEmail(name + "@gmail.com");
            bankAccountService.saveCustomer(customer);

        });

            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());


                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountsList();

            for (BankAccountDTO bankAccount : bankAccounts) {
                for(int i=0; i<10; i++)
                {
                    String accountId;
                    if(bankAccount instanceof SavingAccountDTO){
                        accountId=((SavingAccountDTO)bankAccount).getId();
                    }else{
                        accountId=((CurrentAccountDTO)bankAccount).getId();

                    }
                    bankAccountService.credit(accountId,10000*Math.random()*120000,"Credit");
                    bankAccountService.debit(accountId,1000*Math.random()*9000,"Debit");

                }
            }



        };
    };


   //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository) {

        return args -> {
            Stream.of("Hassan", "Yassine", "Aicha").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                customerRepository.save(customer);

            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount= new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setOverDraft(9000);
                currentAccount.setCurrency("ero");
                currentAccount.setCustomer(customer);
                bankAccountRepository.save(currentAccount);


                SavingAccount savingAccount= new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCurrency("ero");
                savingAccount.setInterestRate(5.5);
                savingAccount.setCustomer(customer);
                bankAccountRepository.save(savingAccount);

                    });
                bankAccountRepository.findAll().forEach(account -> {
                    for(int i=0; i<10; i++){
                        AccountOperation accountOperation = new AccountOperation();
                        accountOperation.setOperationDate(new Date());
                        accountOperation.setAmount(Math.random()*12000);
                        accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                        accountOperation.setBankAccount(account);
                        accountOperationRepository.save(accountOperation);


                    }

                });


        };
    }

}
