package com.crms.placement.model;

/**
 * Builder Pattern implementation for Company object construction.
 * Ensures consistent creation of Company entities with validation.
 */
public class CompanyBuilder {

    private String name;

    /**
     * Set the company name.
     * @param name The company name
     * @return This builder instance for method chaining
     */
    public CompanyBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Builds the Company object with the configured fields.
     * @return Constructed Company object
     * @throws IllegalArgumentException if required fields are missing
     */
    public Company build() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }

        Company company = new Company();
        company.setName(name);

        return company;
    }
}
