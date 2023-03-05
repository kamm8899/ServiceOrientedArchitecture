package edu.stevens.cs548.clinic.service.dto;

public class TreatmentDtoFactory {
	
	public DrugTreatmentDto createDrugTreatmentDto() {
		return new DrugTreatmentDto();
	}
	
	/*
	 * TODOX: Repeat for other treatments.
	 */
	
	public PhysiotherapyTreatmentDto createPhysiotherapyTreatmentDto() {
		return new PhysiotherapyTreatmentDto();
	}
	
	public SurgeryTreatmentDto createSurgeryTreatmentDto() {
		return new SurgeryTreatmentDto();
	}
	
	public RadiologyTreatmentDto createRadiologyTreatmentDto() {
		return new RadiologyTreatmentDto();
	}

}
