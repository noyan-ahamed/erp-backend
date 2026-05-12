package com.erp.services.implemented;

import com.erp.dto.EmployeeDTO;
import com.erp.enities.Employee;
import com.erp.enities.Role;
import com.erp.enities.Users;
import com.erp.enums.UserStatus;
import com.erp.repositories.DesignationRepository;
import com.erp.repositories.EmployeeRepository;
import com.erp.repositories.RoleRepository;
import com.erp.repositories.UsersRepository;
import com.erp.services.EmployeeService;
import com.erp.services.MailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImplement implements EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final DesignationRepository designationRepository;
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    // GET ALL EMPLOYEES
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }


    // GET EMPLOYEE BY ID
    @Override
    public Employee getEmployeeById(Long id) {

        return employeeRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found")
                );
    }


    // CREATE HR
    @Override
    @Transactional
    public Employee createHr(EmployeeDTO dto) {

        return createEmployeeWithRole(dto, "HR");
    }


    // CREATE EMPLOYEE
    @Override
    @Transactional
    public Employee createEmployee(EmployeeDTO dto) {

        return createEmployeeWithRole(dto, "EMPLOYEE");
    }


    // UPDATE EMPLOYEE
    @Override
    @Transactional
    public Employee updateEmployee(Long id, EmployeeDTO dto) {

        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found")
                );

        mapDtoToEntity(dto, employee);

        return employeeRepo.save(employee);
    }

   // DELETE EMPLOYEE
    @Override
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found")
                );

        // delete linked user first
        if (employee.getUser() != null) {
            usersRepository.delete(employee.getUser());
        }

        employeeRepo.delete(employee);
    }


    // COMMON EMPLOYEE CREATION
    private Employee createEmployeeWithRole(
            EmployeeDTO dto,
            String roleName
    ) {

        if (employeeRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new RuntimeException("Role not found")
                );

        String temporaryPassword =
                generateTemporaryPassword();

        Users user = new Users();

        user.setUserName(dto.getEmail());
        user.setName(dto.getName());

        user.setPassWord(
                passwordEncoder.encode(temporaryPassword)
        );

        user.setRoles(Set.of(role));

        user.setStatus(UserStatus.ACTIVE);

        user.setPasswordChanged(false);

        user.setCreated_at(LocalDate.now());

        Users savedUser =
                usersRepository.save(user);

        Employee employee = new Employee();

        mapDtoToEntity(dto, employee);

        employee.setUser(savedUser);

        Employee savedEmployee =
                employeeRepo.save(employee);
        System.out.println("Emp Password: " + temporaryPassword);

        sendCredentialEmail(
                dto.getEmail(),
                temporaryPassword,
                roleName
        );

        return savedEmployee;
    }


    // Dto entity mapping using chat
    private void mapDtoToEntity(
            EmployeeDTO dto,
            Employee employee
    ) {

        employee.setName(dto.getName());

        employee.setEmail(dto.getEmail());

        employee.setMobileNumber(
                dto.getMobileNumber()
        );

        employee.setJoiningDate(
                dto.getJoiningDate()
        );

        employee.setBasicSalary(
                dto.getBasicSalary()
        );

        employee.setBankAccount(
                dto.getBankAccount()
        );

        employee.setAddress(
                dto.getAddress()
        );

        if(dto.getDesignationId() != null){

            employee.setDesignation(

                    designationRepository
                            .findById(dto.getDesignationId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Designation not found"
                                    )
                            )
            );
        }
    }


    // generate password
    private String generateTemporaryPassword() {

        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }


    // SEND LOGIN CREDENTIAL EMAIL
    private void sendCredentialEmail(
            String email,
            String password,
            String role
    ) {

        String subject = "ERP Account Created";

        String body =
                "Your ERP account has been created.\n\n" +
                        "Username: " + email + "\n" +
                        "Temporary Password: " + password + "\n" +
                        "Role: " + role + "\n\n" +
                        "Please login and change your password.";

        mailService.sendSimpleEmail(
                email,
                subject,
                body
        );
    }
}
