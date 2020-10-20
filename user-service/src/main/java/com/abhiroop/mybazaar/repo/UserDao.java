package com.abhiroop.mybazaar.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.abhiroop.mybazaar.pojo.User;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

	@Query("SELECT c FROM User c where c.username = :username")
    User findByUsername(@Param("username") String username);
	
	@Query("SELECT c FROM User c where c.email = :email")
    User findByEmail(@Param("email") String email);
	
	@Modifying
	@Query("update User u set u.active = 'TRUE' where u.username = :username")
	int activateUser(@Param("username") String username);
}


