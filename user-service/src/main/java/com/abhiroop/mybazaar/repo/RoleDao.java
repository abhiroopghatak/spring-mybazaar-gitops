package com.abhiroop.mybazaar.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.abhiroop.mybazaar.pojo.Role;


	@Repository
	public interface RoleDao extends JpaRepository<Role, Long> {
	    
	    @Query(value = "SELECT r FROM Role r where r.userid= :id")
	    List<Role> findByUserID(@Param("id")long id);
	    
	}
