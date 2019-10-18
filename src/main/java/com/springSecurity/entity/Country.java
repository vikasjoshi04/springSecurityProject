package com.springSecurity.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Country {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(length=50, nullable = false)
	private String country;

	@Column(length=2, nullable = false)
	private String code;

	@Column(length=3, nullable = false)
	private String descriptor;

	@Column(length=3, nullable = false)
	private Integer uncode;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public Integer getUncode() {
		return uncode;
	}

	public void setUncode(Integer uncode) {
		this.uncode = uncode;
	}

	@Override
	public String toString() {
		return "Country [id=" + id + ", country=" + country + ", code=" + code + ", descriptor=" + descriptor
				+ ", uncode=" + uncode + "]";
	}
	
	
	
}
