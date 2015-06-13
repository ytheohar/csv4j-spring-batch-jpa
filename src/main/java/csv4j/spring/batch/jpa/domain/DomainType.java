package csv4j.spring.batch.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import csv4j.annotations.CsvFields;

@Entity
@Table(name = "csvdata")
public class DomainType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// no annotation, so it will be match with "field0" csv field
	private int field0;

	// match att1 with both "field1" and "field3" csv fields
	@CsvFields({ "field1", "field3" })
	private String att1;

	// match att2 with "field2" csv field
	@CsvFields({ "field2" })
	private double att2;

	public DomainType() {
	}

	private DomainType(int field0, String att1, double att2) {
		this.field0 = field0;
		this.att1 = att1;
		this.att2 = att2;
	}

	public static DomainType of(int field0, String att1, double att2) {
		return new DomainType(field0, att1, att2);
	}

	public void setField0(int field0) {
		this.field0 = field0;
	}

	public void setAtt1(String att1) {
		this.att1 = att1;
	}

	public void setAtt2(double att2) {
		this.att2 = att2;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append("field0: ");
		sb.append(field0);
		sb.append(", ");
		sb.append("att1: ");
		sb.append(att1);
		sb.append(", ");
		sb.append("att2: ");
		sb.append(att2);
		sb.append(')');
		return sb.toString();
	}
}