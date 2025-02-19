package com.vti.lab7.model;

import java.sql.Date;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long employeeId;
	private String firstName;
	private String lastName;
	private Date dateOfBirth;
	private String phoneNumber;
	private String address;
	private Date hireDate;
	private float salary;
	private String status;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId")
	private User user;

	@ManyToOne
	@JoinColumn(name = "position_id", nullable = false)
	private Position position;

	@ManyToOne
	@JoinColumn(name = "department_id", nullable = false)
	private Department department;

}
