package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Logger log = LoggerFactory.getLogger(AccountsRepositoryInMemory.class);
	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	public boolean updateAccount(String accountFromId, BigDecimal balanceFrom, String accountToId, BigDecimal balanceTo, double transferAmount) {
		// TODO Auto-generated method stub
		boolean transferSuccess = false;
		double balanceFromD = balanceFrom.doubleValue();		
		double balanceToD = balanceTo.doubleValue();		
		
		transferSuccess = transfer(accountFromId, accountToId, transferAmount, balanceFromD, balanceToD) ;
		
		return transferSuccess;
	}

	public boolean transfer(String accountFromId, String accountToId, double transferAmount, double balanceFrom, double balanceTo) {

		boolean transferSuccess = false;
		if (transferAmount < 0) // withdraw value is negative
		{
			log.error("Error: Withdraw amount is invalid.");
			log.error("Account: " + accountFromId);
			log.error("Requested: " + transferAmount);
		} else if (transferAmount > balanceFrom) // withdraw value exceeds balance
		{
			log.error("Error: Insufficient funds.");
			log.error("Account: " + accountFromId);
			log.error("Requested: " + transferAmount);
			log.error("Available: " + balanceFrom);
		} else {
			transferSuccess = true;
			balanceFrom = balanceFrom - transferAmount;
		}

		if (transferSuccess) {
			if (transferAmount < 0) // deposit value is negative
			{
				transferSuccess = false;
				log.error("Error: Deposit amount is invalid.");
				log.error(accountToId + " " + transferAmount);
			} else {
				transferSuccess = true;
				balanceTo = balanceTo + transferAmount;
			}
		}

		return transferSuccess;
	}

	
}
