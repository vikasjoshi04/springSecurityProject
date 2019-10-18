package com.springSecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.springSecurity.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

}
