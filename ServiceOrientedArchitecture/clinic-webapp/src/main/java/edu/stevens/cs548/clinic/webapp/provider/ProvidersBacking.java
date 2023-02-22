package edu.stevens.cs548.clinic.webapp.provider;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.List;

import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.webapp.BaseBacking;

@Named("providersBacking")
@RequestScoped
public class ProvidersBacking extends BaseBacking {

	private static final long serialVersionUID = -733113325524128462L;
	
	// TODO
	IProviderService providerService;

	/*
	 * The list of providers, from which the cursor is selected.
	 */
	private List<ProviderDto> providers;

	public List<ProviderDto> getProviders() {
		return providers;
	}

	// TODO
	@PostConstruct
	private void load() {
		try {
			providers = providerService.getProviders();
		} catch (ProviderServiceExn e) {
			throw new IllegalStateException("Failed to get list of providers.", e);
		}
	}


}
