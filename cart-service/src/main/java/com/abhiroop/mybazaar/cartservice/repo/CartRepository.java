package com.abhiroop.mybazaar.cartservice.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.abhiroop.mybazaar.cartservice.pojo.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	@Query("SELECT c FROM Cart c where c.userid = :id")
	List<Cart> findCartByUserId(@Param("id") String id);
	
	@Modifying
	@Query("update Cart c set c.cartstate= :cartstate where c.cartId = :cartId")
	int updateCartState(@Param("cartId") Long cartId , @Param("cartstate") String cartstate);
}
