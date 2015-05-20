package cmabreu.sagitarii.core;



import java.util.ArrayList;
import java.util.List;

import cmabreu.sagitarii.persistence.entity.Domain;

public class DomainStorage {
	private static List<Domain> domains;
	private static DomainStorage instance;
	
	public static DomainStorage getInstance() {
		if ( instance == null ) {
			instance = new DomainStorage();
		}
		return instance;
	}
	
	private DomainStorage() {
		domains = new ArrayList<Domain>();
	}

	
	public synchronized void setDomains(List<Domain> newDomains) {
		domains = newDomains;
	}

	public synchronized Domain getDomain( String domainName ) {
		for ( Domain domain : domains  ) {
			if ( domain.getDomainName().equals( domainName ) ) {
				return domain;
			}
		}
		return null;
	}
	
}
