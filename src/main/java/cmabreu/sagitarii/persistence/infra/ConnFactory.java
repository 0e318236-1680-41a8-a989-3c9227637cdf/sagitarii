package cmabreu.sagitarii.persistence.infra;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class ConnFactory {
	private static SessionFactory factory;
	private static String myClass = "cmabreu.infra.database.ConnFactory";
	
	private static void doLog( String s ) {
		System.out.println(myClass + " " + s);
	}
	
	public static Session getSession() {
		if ( factory == null ) {
			
			try { 
				doLog("starting Hibernate");  
				Configuration configuration = new Configuration().configure();
				doLog( configuration.getProperty("hibernate.connection.url") );
				
				StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
				serviceRegistryBuilder.applySettings(configuration.getProperties());

				ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();				
				
				factory = configuration.buildSessionFactory(serviceRegistry);

			} catch (Throwable ex) { 
				doLog("sessionFactory fail: " + ex.getMessage() );  
			}
		} 
		Session session = factory.openSession();
		return session;
	}

	
}
