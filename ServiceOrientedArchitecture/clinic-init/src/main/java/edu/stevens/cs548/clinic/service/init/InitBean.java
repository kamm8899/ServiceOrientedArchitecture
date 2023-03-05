package edu.stevens.cs548.clinic.service.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDtoFactory;

@Singleton
@LocalBean
@Startup
// @ApplicationScoped
// @Transactional
public class InitBean {

	private static final Logger logger = Logger.getLogger(InitBean.class.getCanonicalName());

	private static final ZoneId ZONE_ID = ZoneOffset.UTC;

	private PatientDtoFactory patientFactory = new PatientDtoFactory();

	private ProviderDtoFactory providerFactory = new ProviderDtoFactory();

	private TreatmentDtoFactory treatmentFactory = new TreatmentDtoFactory();

	// TODOX
	@Inject
	private IPatientService patientService;

	// TODOX
	@Inject
	private IProviderService providerService;

	/*
	 * Initialize using EJB logic
	 */
	@PostConstruct
	/*
	 * This should work to initialize with CDI bean, but there is a bug in
	 * Payara.....
	 */
	// public void init(@Observes @Initialized(ApplicationScoped.class)
	// ServletContext init) {
	public void init() {
		/*
		 * Put your testing logic here. Use the logger to display testing output in the
		 * server logs.
		 */
		logger.info("Jessica Kamman: ");
		System.err.println("Jessica Kamman");

		try {

			/*
			 * Clear the database and populate with fresh data.
			 * 
			 * Note that the service generates the external ids, when adding the entities.
			 */

			providerService.removeAll();
			patientService.removeAll();

			PatientDto john = patientFactory.createPatientDto();
			john.setName("John Doe");
			john.setDob(LocalDate.parse("1995-08-15"));
			john.setId(patientService.addPatient(john));
			
			PatientDto bob = patientFactory.createPatientDto();
			bob.setName("Bob Oldsman");
			bob.setDob(LocalDate.parse("1960-02-15"));
			bob.setId(patientService.addPatient(bob));
			
			PatientDto sally = patientFactory.createPatientDto();
			sally.setName("Sally Doe");
			sally.setDob(LocalDate.parse("2000-11-05"));
			sally.setId(patientService.addPatient(sally));

			ProviderDto jane = providerFactory.createProviderDto();
			jane.setName("Jane Doe");
			jane.setNpi("1234");
			jane.setId(providerService.addProvider(jane));
			
			ProviderDto betty = providerFactory.createProviderDto();
			betty.setName("Betty Bet");
			betty.setNpi("4567");
			betty.setId(providerService.addProvider(betty));
			
			ProviderDto sam = providerFactory.createProviderDto();
			sam.setName("Sam");
			sam.setNpi("8901");
			sam.setId(providerService.addProvider(sam));
			
//--JOHN's 2 TREATMENTS with Jane ---
			DrugTreatmentDto drug01 = treatmentFactory.createDrugTreatmentDto();
			drug01.setPatientId(john.getId());
			drug01.setPatientName(john.getName());
			drug01.setProviderId(jane.getId());
			drug01.setProviderName(jane.getName());
			drug01.setDiagnosis("Headache");
			drug01.setDrug("Aspirin");
			drug01.setDosage(20);
			drug01.setFrequency(7);
			drug01.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			drug01.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			providerService.addTreatment(drug01);

			// TODOX add more testing, including treatments and providers
			RadiologyTreatmentDto rad01 = treatmentFactory.createRadiologyTreatmentDto();
			rad01.setPatientId(john.getId());
			rad01.setPatientName(john.getName());
			rad01.setProviderId(jane.getId());
			rad01.setProviderName(jane.getName());
			rad01.setDiagnosis("radiology needed");
			ArrayList<LocalDate> radiologyDates = new ArrayList<LocalDate>();
			radiologyDates.add(LocalDate.parse("2023-01-08"));
			radiologyDates.add(LocalDate.parse("2023-01-18"));
			radiologyDates.add(LocalDate.parse("2023-01-24"));
			rad01.setTreatmentDates(radiologyDates);
			
			providerService.addTreatment(rad01);
			
			//-------BOBS 2 TREATMENTS with Betty

			SurgeryTreatmentDto surg01 = treatmentFactory.createSurgeryTreatmentDto();
			surg01.setPatientId(bob.getId());
			surg01.setPatientName(bob.getName());
			surg01.setProviderId(betty.getId());
			surg01.setProviderName(betty.getName());
			surg01.setDiagnosis("broken leg");
			// Now show in the logs what has been added
			surg01.setDischargeInstructions("rest");
			surg01.setSurgeryDate(LocalDate.parse("2023-02-02"));
			
			providerService.addTreatment(surg01);
			
			PhysiotherapyTreatmentDto phys01 = treatmentFactory.createPhysiotherapyTreatmentDto();
			phys01.setPatientId(bob.getId());
			phys01.setPatientName(bob.getName());
			phys01.setProviderId(betty.getId());
			phys01.setProviderName(betty.getName());
			phys01.setDiagnosis("need physiotherapy");
			ArrayList<LocalDate> physiotherapyDates = new ArrayList<LocalDate>();
			physiotherapyDates.add(LocalDate.parse("2023-02-08"));
			physiotherapyDates.add(LocalDate.parse("2023-02-23"));
			physiotherapyDates.add(LocalDate.parse("2023-02-28"));
			phys01.setTreatmentDates(physiotherapyDates);
			
			providerService.addTreatment(phys01);
			
			
			//--Sally 2 treatments with sam
			DrugTreatmentDto drug02 = treatmentFactory.createDrugTreatmentDto();
			drug02.setPatientId(sally.getId());
			drug02.setPatientName(sally.getName());
			drug02.setProviderId(sam.getId());
			drug02.setProviderName(sam.getName());
			drug02.setDiagnosis("Headache");
			drug02.setDrug("Aspirin");
			drug02.setDosage(10);
			drug02.setFrequency(3);
			drug02.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			drug02.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			providerService.addTreatment(drug02);
			
			SurgeryTreatmentDto surg02 = treatmentFactory.createSurgeryTreatmentDto();
			surg02.setPatientId(sally.getId());
			surg02.setPatientName(sally.getName());
			surg02.setProviderId(sam.getId());
			surg02.setProviderName(sam.getName());
			surg02.setDiagnosis("broken leg");
			// Now show in the logs what has been added
			surg02.setDischargeInstructions("rest");
			surg02.setSurgeryDate(LocalDate.parse("2023-04-02"));
			//add physiotherapy follow up dates for surgery

			DrugTreatmentDto followUpDrug = treatmentFactory.createDrugTreatmentDto();
			followUpDrug.setPatientId(sally.getId());
			followUpDrug.setPatientName(sally.getName());
			followUpDrug.setProviderId(sam.getId());
			followUpDrug.setProviderName(sam.getName());
			followUpDrug.setDiagnosis("Stomach");
			followUpDrug.setDrug("Aspirin");
			followUpDrug.setDosage(10);
			followUpDrug.setFrequency(3);
			followUpDrug.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			followUpDrug.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			DrugTreatmentDto followUpDrug2 = treatmentFactory.createDrugTreatmentDto();
			followUpDrug2.setPatientId(sally.getId());
			followUpDrug2.setPatientName(sally.getName());
			followUpDrug2.setProviderId(sam.getId());
			followUpDrug2.setProviderName(sam.getName());
			followUpDrug2.setDiagnosis("Sore");
			followUpDrug2.setDrug("Aspirin");
			followUpDrug2.setDosage(3);
			followUpDrug2.setFrequency(2);
			followUpDrug2.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			followUpDrug2.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			//add physiotherapy follow up dates for surgery
			ArrayList<TreatmentDto> followUpTreatments = new ArrayList<>();
			followUpTreatments.add(followUpDrug);
			followUpTreatments.add(followUpDrug2);
			surg02.setFollowupTreatments(followUpTreatments);


			providerService.addTreatment(surg02);
			
			
			
			Collection<PatientDto> patients = patientService.getPatients();
			for (PatientDto p : patients) {
				logger.info(String.format("Patient %s, ID %s, DOB %s", p.getName(), p.getId().toString(),
						p.getDob().toString()));
				logTreatments(p.getTreatments());
			}

			Collection<ProviderDto> providers = providerService.getProviders();
			for (ProviderDto p : providers) {
				logger.info(String.format("Provider %s, ID %s, NPI %s", p.getName(), p.getId().toString(), p.getNpi()));
				logTreatments(p.getTreatments());
			}

		} catch (Exception e) {

			throw new IllegalStateException("Failed to add record.", e);

		}

	}

	public void shutdown(@Observes @Destroyed(ApplicationScoped.class) ServletContext init) {
		logger.info("App shutting down....");
	}

	private void logTreatments(Collection<TreatmentDto> treatments) {
		for (TreatmentDto treatment : treatments) {
			if (treatment instanceof DrugTreatmentDto) {
				logTreatment((DrugTreatmentDto) treatment);
			} else if (treatment instanceof SurgeryTreatmentDto) {
				logTreatment((SurgeryTreatmentDto) treatment);
			} else if (treatment instanceof RadiologyTreatmentDto) {
				logTreatment((RadiologyTreatmentDto) treatment);
			} else if (treatment instanceof PhysiotherapyTreatmentDto) {
				logTreatment((PhysiotherapyTreatmentDto) treatment);
			}
			if (!treatment.getFollowupTreatments().isEmpty()) {
				logger.info("============= Follow-up Treatments");
				logTreatments(treatment.getFollowupTreatments());
				logger.info("============= End Follow-up Treatments");
			}
		}
	}

	private void logTreatment(DrugTreatmentDto t) {
		logger.info(String.format("...Drug treatment for %s, drug %s", t.getPatientName(), t.getDrug()));
	}

	private void logTreatment(RadiologyTreatmentDto t) {
		logger.info(String.format("...Radiology treatment for %s", t.getPatientName()));
	}

	private void logTreatment(SurgeryTreatmentDto t) {
		logger.info(String.format("...Surgery treatment for %s", t.getPatientName()));
	}

	private void logTreatment(PhysiotherapyTreatmentDto t) {
		logger.info(String.format("...Physiotherapy treatment for %s", t.getPatientName()));
	}

}
