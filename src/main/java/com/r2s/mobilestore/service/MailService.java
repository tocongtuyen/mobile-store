package com.r2s.mobilestore.service;

import com.r2s.mobilestore.common.MailResponse;
import com.r2s.mobilestore.common.MessageResponse;

public interface MailService {
	void send(MailResponse mailResponse);

	MessageResponse sendMailActive(String email);

	MessageResponse sendMailForgotPassword(String email);
}
