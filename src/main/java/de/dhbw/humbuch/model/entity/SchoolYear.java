package de.dhbw.humbuch.model.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="schoolYear")
public class SchoolYear implements de.dhbw.humbuch.model.entity.Entity, Serializable {
	private static final long serialVersionUID = -3752454317452902743L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String year;
	private Date fromDate;
	private Date toDate;
	private Date endFirstTerm;
	private Date beginSecondTerm;
	
	public SchoolYear() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date from) {
		this.fromDate = from;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date to) {
		this.toDate = to;
	}

	public Date getEndFirstTerm() {
		return endFirstTerm;
	}

	public void setEndFirstTerm(Date endFirstTerm) {
		this.endFirstTerm = endFirstTerm;
	}

	public Date getBeginSecondTerm() {
		return beginSecondTerm;
	}

	public void setBeginSecondTerm(Date beginSecondTerm) {
		this.beginSecondTerm = beginSecondTerm;
	}
	
	public int getCurrentTerm() {
		if(getEndFirstTerm().after(new Date())) {
			return new Integer(1);
		} else {
			return new Integer(2);
		}
	}
	
	public static class Builder {
		private final String year;
		private final Date fromDate;
		private final Date toDate;
		
		private Date endFirstTerm;
		private Date beginSecondTerm;
		
		public Builder(String year, Date fromDate, Date toDate) {
			this.year = year;
			this.fromDate = fromDate;
			this.toDate = toDate;
		}
		
		public Builder endFirstTerm(Date endFirstTerm) {
			this.endFirstTerm = endFirstTerm;
			return this;
		}
		
		public Builder beginSecondTerm(Date beginSecondTerm) {
			this.beginSecondTerm = beginSecondTerm;
			return this;
		}
		
		public SchoolYear build() {
			return new SchoolYear(this);
		}
	}
	
	private SchoolYear(Builder builder) {
		this.year = builder.year;
		this.fromDate = builder.fromDate;
		this.toDate = builder.toDate;

		this.endFirstTerm = builder.endFirstTerm;
		this.beginSecondTerm = builder.beginSecondTerm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SchoolYear))
			return false;
		SchoolYear other = (SchoolYear) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}
	
}
