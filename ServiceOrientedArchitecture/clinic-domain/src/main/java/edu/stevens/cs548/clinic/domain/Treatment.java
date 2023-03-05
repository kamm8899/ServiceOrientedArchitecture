package edu.stevens.cs548.clinic.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;



/**
 * Entity implementation class for Entity: Treatment
 *
 */
@NamedQueries({
	@NamedQuery(
		name="SearchTreatmentByTreatmentId",
		query="select t from Treatment t where t.treatmentId = :treatmentId"),
	@NamedQuery(
			name="SearchTreatmentWithFollowupsByTreatmentId",
			query="select t from Treatment t left join fetch t.followupTreatments where t.treatmentId = :treatmentId"),
	@NamedQuery(
			name="CountTreatmentByTreatmentId",
			query="select count(t) from Treatment t where t.treatmentId = :treatmentId"),
	@NamedQuery(
		name = "RemoveAllTreatments", 
		query = "delete from Treatment t")
})

// TODOX
@Entity
@Table(indexes = @Index(columnList = "treatmentId"))
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Treatment implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// TODOX PK
	@Id
	@GeneratedValue
	protected long id;
	
	// TODOX
	@Column(nullable = false, unique = true)
	protected UUID treatmentId;
	
	protected String diagnosis;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public UUID getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(UUID treatmentId) {
		this.treatmentId = treatmentId;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	/*
	 * TODOX
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	protected Patient patient;

	public Patient getPatient() {
		return patient;
	}

	
	void setPatient(Patient patient) {
		this.patient = patient;
	}

	/*
	 * TODOX
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	protected Provider provider;

	public Provider getProvider() {
		return provider;
	}	
	
	public void setProvider(Provider provider) {
		this.provider = provider;
	}	
	
	/*
	 * TODOX
	 */
	@OneToMany(cascade = CascadeType.PERSIST)
	protected Collection<Treatment> followupTreatments;
	
	public void addFollowupTreatment(Treatment t) {
		followupTreatments.add(t);
	}


	/*
	 * We use the visitor pattern to access a treatment.
	 */
	public abstract <T> T export(ITreatmentExporter<T> visitor);
	
	protected final <T> List<T> exportFollowupTreatments(ITreatmentExporter<T> visitor) {
		List<T> exports = new ArrayList<T>();
		for (Treatment t : followupTreatments) {
			exports.add(t.export(visitor));
		}
		return exports;
	}

	
	public Treatment() {
		super();
		/*
		 * TODOX initialize lists
		 */
		followupTreatments = new ArrayList<Treatment>();
	}   
}
