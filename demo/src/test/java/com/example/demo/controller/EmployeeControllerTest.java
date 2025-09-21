package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
	@Autowired
    private MockMvc mockMvc;
	
	 @MockBean
	 private EmployeeRepository employeeRepository;
	 
	  @Test
	    void shouldReturnAllEmployees() throws Exception {
		    Employee alice =  new Employee("Alice", "Dupont", "alice@mail.com");
		    alice.setId(1L);
		    Employee bob = new Employee("Bob", "Martin", "bob@mail.com");
		    bob.setId(2L);
	        List<Employee> employees = Arrays.asList(alice, bob);
	        when(employeeRepository.findAll()).thenReturn(employees);

	        mockMvc.perform(get("/api/v1/employees"))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.size()").value(2))
	                .andExpect(jsonPath("$[0].firstName").value("Alice"));
	    }

	    @Test
	    void shouldReturnEmployeeById() throws Exception {
	        Employee employee = new Employee("Alice", "Dupont", "alice@mail.com");
	        employee.setId(1L);

	        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

	        mockMvc.perform(get("/api/v1/employees/1"))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.emailId").value("alice@mail.com"));
	    }

}
