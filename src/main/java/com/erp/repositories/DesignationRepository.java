package com.erp.repositories;

import com.erp.enities.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignationRepository extends JpaRepository<Designation, Long> {
    List<Designation> findByDepartmentId(long departmentId);
}
