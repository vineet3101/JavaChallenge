package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public AccountsRepository getAccountsRepository() {
		return accountsRepository;
	}

	public boolean updateAccountRepository(String accountFromId, BigDecimal balanceFrom, String accountToId,
			BigDecimal balanceTo, double transferAmount) {

		boolean repoResponse = false;
		repoResponse = accountsRepository.updateAccount(accountFromId, balanceFrom, accountToId, balanceTo,
				transferAmount);

		return repoResponse;
	}

}
