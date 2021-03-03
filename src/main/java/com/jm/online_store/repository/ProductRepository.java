package com.jm.online_store.repository;

import com.jm.online_store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query(value = "From Product where deleted = :isDeleted")
	List<Product> findProductsByDelete(@Param("isDeleted") boolean isDeleted);

	Optional<Product> findByProduct(String productName);

	@Query(value = "SELECT * FROM product LIMIT :num", nativeQuery = true)
	List<Product> findNumProducts(@Param("num") Integer num);

	List<Product> findProductByProductContains(String searchString);

	@Query("FROM Product p WHERE p.descriptions.information LIKE %:searchString%")
	List<Product> findProductByDescriptionsContains(@Param("searchString") String searchString);

    boolean existsProductByProduct(String productName);

    List<Product> findProductByPriceChangeSubscribersEquals(String email);

    @Modifying
    @Query(value = "delete from product_subscribers_mails where product_id =:id and email =:email", nativeQuery = true)
    void deletePriceChangeSubscriber(String email, long id);

    @Query("FROM Product p order by p.rating")
    List<Product> findAllOrderByRatingAsc();

    @Query("FROM Product p order by p.rating DESC")
    List<Product> findAllOrderByRatingDesc();

    @Query(value = "SELECT product_picture_names FROM product_picture_names n1 where n1.id =:id", nativeQuery = true)
    String findProductPictureNamesById(Long id);

    @Modifying
    @Query(value = "delete from product_picture_names p where p.id =:id", nativeQuery = true)
    void deleteProductPictureNameById(Long id);

    @Query(value = "SELECT COUNT (product_picture_names)FROM product_picture_names WHERE product_id = :id", nativeQuery = true)
    Long countAllPictureProductById(Long id);

    @Query(value = "SELECT product_picture_names.product_id FROM product_picture_names WHERE product_picture_names.id = :idPicture", nativeQuery = true)
    Long findProductIdByIdPicture(Long idPicture);

    @Query(value = "SELECT id FROM product_picture_names  WHERE product_picture_names.product_id = :productId", nativeQuery = true)
    List<Long> findAllPictureIdByProductId(Long productId);
}