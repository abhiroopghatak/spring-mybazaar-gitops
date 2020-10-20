package com.abhiroop.mybazaar.repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.abhiroop.mybazaar.pojo.ConfirmationToken;

@Repository
public interface  ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {

	Optional<ConfirmationToken> findConfirmationTokenByConfirmationToken(String token);
}
