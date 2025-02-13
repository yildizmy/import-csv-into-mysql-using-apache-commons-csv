package com.github.yildizmy.service;

import com.github.yildizmy.dto.mapper.EmployeeRequestMapper;
import com.github.yildizmy.dto.response.EmployeeResponse;
import com.github.yildizmy.domain.Employee;
import com.github.yildizmy.repository.EmployeeRepository;
import com.github.yildizmy.util.CsvHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.github.yildizmy.common.Constants.ENTITY_NOT_FOUND;
import static com.github.yildizmy.common.Constants.NO_RECORD;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeResponse findByEmail(String email) {
        return employeeRepository.findBySimpleNaturalId(email)
                .map(EmployeeResponse::new)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::new)
                .toList();
    }

    @SneakyThrows
    public void create(MultipartFile file) throws EntityNotFoundException {
        final List<Employee> employees = CsvHelper.csvToEmployees(file.getInputStream()).stream()
                .map(EmployeeRequestMapper::mapToEntity)
                .toList();
        if (employees.isEmpty()) {
            throw new EntityNotFoundException(NO_RECORD);
        }
        employeeRepository.saveAll(employees);
    }

    public void deleteAll() {
        employeeRepository.deleteAll();
    }
}
