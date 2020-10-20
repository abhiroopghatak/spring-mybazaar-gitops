package com.abhiroop.mybazaar.svc.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abhiroop.mybazaar.pojo.ConfirmationToken;
import com.abhiroop.mybazaar.repo.ConfirmationTokenRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	void saveConfirmationToken(ConfirmationToken confirmationToken) {

		confirmationTokenRepository.save(confirmationToken);
	}

	ConfirmationToken getConfirmationTokenObjectByToken(String token) {
		ConfirmationToken c = null;
		Optional<ConfirmationToken> optionalObject = confirmationTokenRepository
				.findConfirmationTokenByConfirmationToken(token);
		if (optionalObject.isPresent()) {
			c = optionalObject.get();
		}

		return c;
	}

	void deleteConfirmationToken(Long id) {

		confirmationTokenRepository.deleteById(id);
	}

	Optional<ConfirmationToken> findConfirmationTokenByToken(String token) {

		return confirmationTokenRepository.findConfirmationTokenByConfirmationToken(token);
	}
}
