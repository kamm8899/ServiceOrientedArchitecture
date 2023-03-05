package edu.stevens.cs548.clinic.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import edu.stevens.cs548.clinic.domain.ITreatmentDao.TreatmentExn;

/**
 * Entity implementation class for Entity: Patient
 *
 */
@NamedQueries({
	@NamedQuery(
		name="SearchProviderByProviderId",
		query="select p from Provider p where p.providerId = :providerId"),
	@NamedQuery(
			name="SearchProviderWithTreatmentsByProviderId",
			query="select p from Provider p left join fetch p.treatments where p.providerId = :providerId"),
	@NamedQuery(
		name="CountProviderByProviderId",
		query="select count(p) from Provider p where p.providerId = :providerId"),
	@NamedQuery(
			name = "SearchAllProviders", 
			query = "select p from Provider p"),
	@NamedQuery(
		name = "RemoveAllProviders", 
		query = "delete from Provider p")
})
// TODOX
@Entity
@Table(indexes = @Index(columnList = "providerId"))
public class Provider implements Serializable, ITreatmentImporter {
		
	private static final long serialVersionUID = -876909316791083094L;

	// TODOX JPA annotations
	@Id
	@GeneratedValue
	private long id;
	
	// TODOX
	@Column(nullable = false, unique = true)
	private UUID providerId;
	
	private String npi;

	private String name;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public UUID getProviderId() {
		return providerId;
	}

	public void setProviderId(UUID providerId) {
		this.providerId = providerId;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// TODOX JPA annotations (propagate deletion of provider to treatments)
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "provider")
	private Collection<Treatment> treatments;

	@Transient
	private TreatmentFactory treatmentFactory;
	
	@Transient
	private ITreatmentDao treatmentDao;
	
	public void setTreatmentDao (ITreatmentDao tdao) {
		this.treatmentDao = tdao;
	}
	
	public Provider() {
		super();
		treatments = new ArrayList<Treatment>();
		treatmentFactory = new TreatmentFactory();
	}
	
	/*
	 * Addition and deletion of treatments should be done here.
	 */
	
	public boolean administers(Treatment t) {
		return treatments.contains(t);
	}
	
	/**
	 * TODOX complete this operation (see patient entity)
	 *
	 * Export a treatment without violating Aggregate pattern.
	 * Check that the exported treatment is a treatment for this provider.
	 * 
	 */
	public <T> T exportTreatment(UUID tid, ITreatmentExporter<T> visitor) throws TreatmentExn {
		Treatment t = treatmentDao.getTreatment(tid);
		
		if (t.getProvider() != this) {
			throw new TreatmentExn("Inappropriate treatment access: provider = " + id + ", treatment = " + tid);
		}

		return t.export(visitor);
	}
	
	/**
	 * Map the treatment exporter over all of the treatments for this provider
	 */
	public <T> List<T> exportTreatments(ITreatmentExporter<T> visitor) throws TreatmentExn {
		List<T> exports = new ArrayList<T>();
		
		for (Treatment t : treatments) {
			exports.add(t.export(visitor));
		}
		
		return exports;
	}

	private void addTreatment (Treatment t) {
		/*
		 * TODOX complete this operation (see patient entity)
		 */
		treatments.add(t);
		t.setProvider(this);
	}
	
	@Override
	public Consumer<Treatment> importDrugTreatment(UUID tid, Patient patient, Provider provider, String diagnosis, String drug,
			float dosage, LocalDate start, LocalDate end, int frequency, Consumer<Treatment> consumer) {
		/*
		 * Create the entity object.
		 */
		DrugTreatment treatment = treatmentFactory.createDrugTreatment();
		treatment.setTreatmentId(tid);
		treatment.setDiagnosis(diagnosis);
		treatment.setDrug(drug);
		treatment.setDosage(dosage);
		treatment.setStartDate(start);
		treatment.setEndDate(end);
		treatment.setFrequency(frequency);
		/*
		 * NB for follow-up treatments, this may not be the current provider
		 */
		provider.addTreatment(treatment);
		/*
		 * NB for follow-up treatments, the patient will always be the same
		 */
		patient.addTreatment(treatment);
		/*
		 * Add to the database.
		 */
		treatmentDao.addTreatment(treatment);
		/*
		 * Add to parent follow-up treatments, if appropriate
		 */
		if (consumer != null) {
			consumer.accept(treatment);
		}
		/*
		 * Return our own consumer who will add follow-up treatments
		 */
		return (followUp) -> { treatment.addFollowupTreatment(followUp); };
	}

	@Override
	public Consumer<Treatment>  importRadiology(UUID tid, Patient patient, Provider provider, String diagnosis,
												List<LocalDate> dates,
												Consumer<Treatment> consumer) {
		RadiologyTreatment treatment = treatmentFactory.createRadiologyTreatment();
		treatment.setTreatmentId(tid);
		treatment.setDiagnosis(diagnosis);
		// add all the dates to the treatment
		for (int i = 0; i < dates.size(); i++) {
			treatment.addTreatmentDate(dates.get(i));
		}
		// Why don't they have this above?
		// treatment.setPatient(patient);
		// treatment.setProvider(provider);
		/*
		 * NB for follow-up treatments, this may not be the current provider
		 */
		provider.addTreatment(treatment);
		/*
		 * NB for follow-up treatments, the patient will always be the same
		 */
		patient.addTreatment(treatment);
		/*
		 * Add to the database.
		 */
		treatmentDao.addTreatment(treatment);
		/*
		 * Add to parent follow-up treatments, if appropriate
		 */
		if (consumer != null) {
			consumer.accept(treatment);
		}
		/*
		 * Return our own consumer who will add follow-up treatments
		 */
		return (followUp) -> {
			treatment.addFollowupTreatment(followUp);
		};

	}
	

	@Override
	public Consumer<Treatment> importSurgery(UUID tid, Patient patient, Provider provider, String diagnosis, LocalDate date,
			String dischargeInstructions, Consumer<Treatment> consumer) {
		SurgeryTreatment treatment = treatmentFactory.createSurgeryTreatment();
		treatment.setTreatmentId(tid);
		treatment.setDiagnosis(diagnosis);
		treatment.setSurgeryDate(date);
		treatment.setDischargeInstructions(dischargeInstructions);
		// Why don't they have this above?
		// treatment.setPatient(patient);
		// treatment.setProvider(provider);
		/*
		 * NB for follow-up treatments, this may not be the current provider
		 */
		provider.addTreatment(treatment);
		/*
		 * NB for follow-up treatments, the patient will always be the same
		 */
		patient.addTreatment(treatment);
		/*
		 * Add to the database.
		 */
		treatmentDao.addTreatment(treatment);
		/*
		 * Add to parent follow-up treatments, if appropriate
		 */
		if (consumer != null) {
			consumer.accept(treatment);
		}
		/*
		 * Return our own consumer who will add follow-up treatments
		 */
		return (followUp) -> {
			treatment.addFollowupTreatment(followUp);
		};
	}

	@Override
	public Consumer<Treatment> importPhysiotherapy(UUID tid, Patient patient, Provider provider, String diagnosis,
			List<LocalDate> dates, Consumer<Treatment> consumer) {
		// TODOX finish this
		PhysiotherapyTreatment treatment = treatmentFactory.createPhysiotherapyTreatment();
		treatment.setTreatmentId(tid);
		treatment.setDiagnosis(diagnosis);
		// add all the dates to the treatment
		for (int i = 0; i < dates.size(); i++) {
			treatment.addTreatmentDate(dates.get(i));
		}
		provider.addTreatment(treatment);
		patient.addTreatment(treatment);
		treatmentDao.addTreatment(treatment);
		if (consumer != null) {
			consumer.accept(treatment);
		}
		return (followUp) -> {
			treatment.addFollowupTreatment(followUp);
		};
	}
		
}
