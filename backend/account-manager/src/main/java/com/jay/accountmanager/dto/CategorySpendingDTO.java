package com.jay.accountmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySpendingDTO {
    private String categoryName;
    private BigDecimal amount;
    private double percentage;
    
	public CategorySpendingDTO(String categoryName, BigDecimal amount) {
		super();
		this.categoryName = categoryName;
		this.amount = amount;
	}
    
    
}
