package cmabreu.sagitarii.persistence.repository;

import java.util.List;

import cmabreu.sagitarii.core.DomainStorage;
import cmabreu.sagitarii.core.TableAttribute;
import cmabreu.sagitarii.core.TableAttribute.AttributeType;
import cmabreu.sagitarii.persistence.entity.Domain;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.infra.DaoFactory;
import cmabreu.sagitarii.persistence.infra.IDao;

public class RelationRepository extends BasicRepository {

	public RelationRepository() throws DatabaseConnectException {
		super();
		logger.debug("init");
	}

	
	public List<Relation> getList() throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		List<Relation> relacoes = null;
		try {
			relacoes = fm.getList("select * from tables");
		} catch (Exception e) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + relacoes.size() + " tables.");
		return relacoes;
	}

	public void updateTable( Relation table ) throws UpdateException {
		logger.debug("update");
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		try {
			fm.updateDO(table);
			commit();
		} catch (UpdateException e) {
			rollBack();
			closeSession();
			logger.error( e.getMessage() );
			throw e;
		}
		closeSession();
		logger.debug("done");
	}
	

	public List<?> genericFetchList( String query ) throws Exception {
		logger.debug("generic query");
		logger.debug( query );
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		List<?> retorno = null;
		try {
			retorno = fm.genericAccess( query );
		} catch (Exception e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done");
		return retorno;
	}

	
	public int getCount( String tableName, String criteria ) throws Exception {
		logger.debug("get count " + tableName );
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		int retorno = fm.getCount(tableName,criteria);
		closeSession();
		logger.debug("done");
		return retorno;
	}
	
	
	/**
	 * Executa um acesso generico no banco de dados
	 * Não deve ser utilizado para SELECT.
	 * 
	 * @param query (update ou delete)
	 * 
	 * @throws Exception
	 */
	public void executeQuery( String query ) throws Exception {
		logger.debug("execute query");
		logger.debug( query );
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		try {
			fm.executeQuery( query, false );
			commit();
		} catch (Exception e) {
			logger.error( e.getMessage() );
			logger.error( " > " + query );
			rollBack();
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done");
	}
	
	public void executeQueryAndKeepOpen( String query ) throws Exception {
		logger.debug("execute query keeping session opened");
		logger.debug( query );
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		try {
			fm.executeQuery( query, false );
		} catch (Exception e) {
			logger.error( e.getMessage() );
			logger.error( " > " + query );
			rollBack();
			closeSession();
			throw e;
		}
		logger.debug("done");
	}

	public int insertDomain( Domain domain ) throws InsertException {
		logger.debug("insert domain " + domain.getDomainName() );
		DaoFactory<Domain> factory = new DaoFactory<Domain>();
		IDao<Domain> domainDao = factory.getDao(this.session, Domain.class);
		return domainDao.insertDO( domain );
	}
	

	public List<Domain> getDomains() throws NotFoundException {
		logger.debug("get domains list" );
		DaoFactory<Domain> factory = new DaoFactory<Domain>();
		IDao<Domain> domainDao = factory.getDao(this.session, Domain.class);
		List<Domain> domains = null;
		try {
			domains = domainDao.getList("select * from domains");
		} catch (Exception e) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + domains.size() + " domains.");
		return domains;
	}

	
	public Relation insertTable(Relation table, List<TableAttribute> attributes) throws InsertException {
		logger.debug("insert table " + table.getName() );
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		
		DaoFactory<Domain> factory = new DaoFactory<Domain>();
		IDao<Domain> domainDao = factory.getDao(this.session, Domain.class);

		try {
			fm.insertDO(table);
			
			for( TableAttribute attr : attributes ) {
				if ( attr.getType() == AttributeType.FILE ) {
					logger.debug("file type attribute " + attr.getName() + " detected. creating domain...");
					Domain dom = new Domain();
					dom.setDomainName( table.getName() + "." + attr.getName() );
					dom.setTable(table);
					domainDao.insertDO( dom );
					logger.debug("done creating domain for " + attr.getName() );
				}
			}
			commit();
			try {
				DomainStorage.getInstance().setDomains( getDomains() );
			} catch ( NotFoundException nfe ) { 
				logger.debug("no domains found in database");
			}
		} catch (Exception e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new InsertException(e.getMessage());
		}
		logger.debug("done");
		closeSession();
		return table;
	}
	
	
	public void createInternalIndex( String tableName ) throws InsertException {
		logger.debug("create internal index for table " + tableName);

		String sql = "CREATE INDEX " + tableName + "_indx ON "+tableName + 
				" (index_id, id_experiment, id_activity, id_pipeline)";
		
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		try {
			fm.executeQuery( sql, false );
			commit();
		} catch (Exception e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new InsertException(e.getMessage());
		}
		closeSession();
		logger.debug("done");
	}
	
	public void createDatabaseTable(String schema) throws InsertException {
		logger.debug("create custom table");
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		try {
			fm.executeQuery( schema, false );
			commit();
		} catch (Exception e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new InsertException(e.getMessage());
		}
		closeSession();
		logger.debug("done");
	}
	
	public Relation getTable(String name) throws NotFoundException {
		logger.debug("retrieve table by name " + name + "..." );
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		Relation table = null;
		try {
			table = fm.getList("select * from tables where name = '" + name + "'").get(0);
		} catch ( Exception e ) {
			closeSession();
			logger.error( e.getMessage() );
			throw e;
		}
		closeSession();
		logger.debug("done");
		return table;
	}

	
	public Relation getTable(int idRelacao) throws NotFoundException {
		logger.debug("retrieve");
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		Relation table = null;
		try {
			table = fm.getDO(idRelacao);
		} catch ( Exception e ) {
			closeSession();
			logger.error( e.getMessage() );
			throw e;
		}
		closeSession();
		logger.debug("done: " + table.getName() );
		return table;
	}
	
	public void deleteTable(Relation table) throws DeleteException {
		logger.debug("delete" );
		DaoFactory<Relation> df = new DaoFactory<Relation>();
		IDao<Relation> fm = df.getDao(this.session, Relation.class);
		try {
			fm.executeQuery("drop table " + table.getName(), false );
			fm.deleteDO(table);
			commit();
		} catch (Exception e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new DeleteException( e.getMessage() );			
		}
		logger.debug("done");
		closeSession();
	}	
}
