package com.crms.placement.model;

import jakarta.persistence.*;

/**
 * ORIGINAL CODE - Before Builder Pattern Implementation
 * =====================================================
 *
 * VIOLATIONS:
 * ❌ GRASP Creator Pattern
 *    - Public constructor creates objects without validation
 *    - Multiple constructors (telescoping constructor problem)
 *
 * ❌ Open/Closed Principle (OCP)
 *    - Adding new fields requires modifying constructors
 *    - Not flexible for optional fields
 *
 * PROBLEMS:
 * - Company(String name) constructor is public
 * - No validation of required fields
 * - Can't easily add optional fields
 * - No clear factory method for creation
 */
@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long company_id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "company_id")
    private User user;
    
    private String name;

    // Constructors - Problem: Public constructors, no validation
    public Company() {}

    /**
     * ❌ PROBLEMATIC - Public constructor with minimal validation
     *
     * Issues:
     * - Can create Company with null/empty name
     * - Only name field, but what if we need more?
     * - No way to validate at construction time
     */
    public Company(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Integer getCompany_id() { return company_id; }
    public void setCompany_id(Integer company_id) { this.company_id = company_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
