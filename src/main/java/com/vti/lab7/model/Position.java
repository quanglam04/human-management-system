package com.vti.lab7.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "positions")
public class Position {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
	@Column(name = "position_id")
    private Long positionId;

    @Column(name = "position_name", nullable = false)
    private String positionName;
    
	public Position(String positionName) {
		this.positionName = positionName; 
	}

	@OneToMany(mappedBy = "position", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JsonIgnore
	List<Employee> employees = new ArrayList<>();

}
