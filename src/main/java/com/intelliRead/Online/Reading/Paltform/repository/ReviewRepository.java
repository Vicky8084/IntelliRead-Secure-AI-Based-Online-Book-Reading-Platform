package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {
}
