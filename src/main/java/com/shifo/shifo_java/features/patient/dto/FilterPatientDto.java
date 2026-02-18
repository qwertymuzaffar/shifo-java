package com.shifo.shifo_java.features.patient.dto;

import com.shifo.shifo_java.common.dto.PaginationDto;
import com.shifo.shifo_java.features.patient.enums.PatientRegistrationStatus;
import com.shifo.shifo_java.features.patient.enums.PatientSortField;
import com.shifo.shifo_java.features.patient.enums.PatientSource;
import com.shifo.shifo_java.common.enums.SortOrder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class FilterPatientDto extends PaginationDto {

    private String search;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDateTo;

    private Boolean isActive;

    private PatientRegistrationStatus registrationStatus;

    private PatientSource source;

    private Boolean positiveBalance;

    private PatientSortField sort;

    private SortOrder order = SortOrder.DESC;

    // ---------- SEARCH ----------
    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    // ---------- DATES ----------
    public LocalDate getBirthDateFrom() {
        return birthDateFrom;
    }

    public void setBirthDateFrom(LocalDate birthDateFrom) {
        this.birthDateFrom = birthDateFrom;
    }

    public LocalDate getBirthDateTo() {
        return birthDateTo;
    }

    public void setBirthDateTo(LocalDate birthDateTo) {
        this.birthDateTo = birthDateTo;
    }

    // ---------- BOOLEAN TRANSFORM (NestJS @Transform replacement) ----------

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(String value) {
        this.isActive = parseBoolean(value);
    }

    public Boolean getPositiveBalance() {
        return positiveBalance;
    }

    public void setPositiveBalance(String value) {
        this.positiveBalance = parseBoolean(value);
    }

    private Boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) return null;

        String v = value.trim().toLowerCase();

        if (v.equals("true") || v.equals("1")) return true;
        if (v.equals("false") || v.equals("0")) return false;

        throw new IllegalArgumentException("Boolean value must be true/false/1/0");
    }

    // ---------- ENUMS ----------

    public PatientRegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(PatientRegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public PatientSource getSource() {
        return source;
    }

    public void setSource(PatientSource source) {
        this.source = source;
    }

    public PatientSortField getSort() {
        return sort;
    }

    public void setSort(PatientSortField sort) {
        this.sort = sort;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }
}

