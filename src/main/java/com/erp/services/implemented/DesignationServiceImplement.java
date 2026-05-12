package com.erp.services.implemented;

import com.erp.enities.Designation;
import com.erp.repositories.DepartmentRepository;
import com.erp.repositories.DesignationRepository;
import com.erp.services.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DesignationServiceImplement implements DesignationService {
    private final DesignationRepository designationRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<Designation> getAllDesignations() {
        return designationRepository.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

    @Override
    public Designation createDesignation(Designation designation) {
        return designationRepository.save(designation);
    }

    @Override
    public List<Designation> designationByDeptId(Long id) {
        return designationRepository.findByDepartmentId(id);
    }

    @Override
    public Designation updateDesignation(Long id, Designation designation) {

        Designation existing = designationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        existing.setName(designation.getName());
        existing.setDepartment(designation.getDepartment());

        return designationRepository.save(existing);
    }

    @Override
    public void deleteDesignation(Long id) {
        designationRepository.deleteById(id);
    }
}
