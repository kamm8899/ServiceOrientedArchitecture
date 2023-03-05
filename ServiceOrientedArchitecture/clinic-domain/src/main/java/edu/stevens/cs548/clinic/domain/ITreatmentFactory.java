package edu.stevens.cs548.clinic.domain;


public interface ITreatmentFactory {
	
	public DrugTreatment createDrugTreatment ();
	
	/*
	 * TODOX add methods for Radiology, Surgery, Physiotherapy
	 */
	public RadiologyTreatment createRadiologyTreatment();

	public PhysiotherapyTreatment createPhysiotherapyTreatment();

	public SurgeryTreatment createSurgeryTreatment();
}
