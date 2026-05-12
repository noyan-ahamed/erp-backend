package com.erp.services.implemented;

import com.erp.enities.Department;
import com.erp.repositories.DepartmentRepository;
import com.erp.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImplement implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    @Override
    public List<Department> getAllDept() {
        return departmentRepository.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

    @Override
    public Department createDept(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDept(Long id, Department department) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        existing.setName(department.getName());

        return departmentRepository.save(existing);
    }

    @Override
    public void deleteDept(Long id) {
        departmentRepository.deleteById(id);
    }
}
