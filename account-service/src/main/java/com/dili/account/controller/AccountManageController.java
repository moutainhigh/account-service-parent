package com.dili.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dili.account.dto.CardRequestDto;
import com.dili.account.service.IAccountManageService;
import com.dili.account.validator.AccountValidator;
import com.dili.ss.domain.BaseOutput;

/**
 * 卡账户管理操作
 * @Auther: miaoguoxin
 * @Date: 2020/6/29 14:39
 */
@RestController
@RequestMapping("/api/account")
public class AccountManageController {

	@Autowired
	private IAccountManageService accountManageService;
	
	/**
	 * 冻结账户
	 */
	@PostMapping("/frozen")
	public BaseOutput<Boolean> frozen(@RequestBody @Validated(value = {AccountValidator.DisabledState.class}) CardRequestDto cardRequestDto) {
		accountManageService.frozen(cardRequestDto);
        return BaseOutput.success();
	}
	
	/**
	 * 解冻账户
	 */
	@PostMapping("/unfrozen")
	public BaseOutput<Boolean> unfroze(@RequestBody @Validated(value = {AccountValidator.DisabledState.class}) CardRequestDto cardRequestDto) {
		accountManageService.unfrozen(cardRequestDto);
        return BaseOutput.success();
	}
}
