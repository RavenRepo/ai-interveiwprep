package com.interview.platform.repository;

import com.interview.platform.model.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRoleRepository extends JpaRepository<JobRole, Long> {

    List<JobRole> findAllByActiveTrue();
}
