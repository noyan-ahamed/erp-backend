package com.erp.services;

import com.erp.enities.Designation;

import java.util.List;

public interface DesignationService {
    List<Designation> getAllDesignations();
    Designation createDesignation(Designation designation);
    List<Designation> designationByDeptId(Long id);
    Designation updateDesignation(Long id, Designation designation);
    void deleteDesignation(Long id);
}
