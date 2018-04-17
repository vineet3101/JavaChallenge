package com.db.awmd.challenge.web;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.util.APINames;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

	private final Logger log = LoggerFactory.getLogger(AccountsController.class);

	@Autowired(required = true)
	private AccountsService accountsService;
	@Autowired(required = false)
	private NotificationService notificationService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
		log.info("Creating account {}", account);

		try {
			this.accountsService.createAccount(account);
		} catch (DuplicateAccountIdException daie) {
			return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(path = "/{accountId}")
	public Account getAccount(@PathVariable String accountId) {
		log.info("Retrieving account for id {}", accountId);
		return this.accountsService.getAccount(accountId);
	}

	@RequestMapping(value = { APINames.TRANSFER_MONEY }, method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public String updateAccount(@PathVariable String accountFromId, @PathVariable String accountToId,
			@PathVariable double amountToTransfer) {
		log.info("Updating account for id {}", accountToId);
		String returnResponse = "";
		// get detail account from
		Account accFromDetails = accountsService.getAccount(accountFromId);
		BigDecimal balanceFrom = accFromDetails.getBalance();
		accountFromId = accFromDetails.getAccountId();

		// get details account to
		Account accToDetails = accountsService.getAccount(accountFromId);
		BigDecimal balanceTo = accToDetails.getBalance();
		accountToId = accToDetails.getAccountId();

		// update the amount FROM to TO
		boolean accountUpdateResponse = accountsService.updateAccountRepository(accountFromId, balanceFrom, accountToId,
				balanceTo, amountToTransfer);
		if (accountUpdateResponse) {
			returnResponse = "Amount transfered succesfully.";

			String transferDescriptionTo = "Amount : " + amountToTransfer + " added into your account";
			String transferDescriptionFrom = "Amount : " + amountToTransfer + " deducted from your account";

			notificationService.notifyAboutTransfer(accToDetails, transferDescriptionTo);
			notificationService.notifyAboutTransfer(accFromDetails, transferDescriptionFrom);

		} else {
			returnResponse = "Amount transfered unsuccesfull.";
		}

		return returnResponse;
	}

}
