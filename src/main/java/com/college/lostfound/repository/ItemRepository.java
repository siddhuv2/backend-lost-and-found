package com.college.lostfound.repository;

import com.college.lostfound.entity.Item;
import com.college.lostfound.entity.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByReportedByIdOrderByCreatedAtDesc(Long userId);

    List<Item> findTop6ByOrderByCreatedAtDesc();

    @Query("SELECT i FROM Item i WHERE " +
           "(:type IS NULL OR i.type = :type) AND " +
           "(:category IS NULL OR :category = '' OR LOWER(i.category) = LOWER(:category)) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.campusLocation) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY i.createdAt DESC")
    Page<Item> searchItems(
            @Param("type") ItemType type,
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    long countByType(ItemType type);
}
