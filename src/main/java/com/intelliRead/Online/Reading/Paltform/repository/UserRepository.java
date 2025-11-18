
package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.role = 'PUBLISHER' AND u.status = 'ACTIVE'")
    List<User> findActivePublishers();

    long countByRole(Role role);
}
