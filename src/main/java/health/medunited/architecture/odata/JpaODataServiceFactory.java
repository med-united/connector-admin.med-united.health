package health.medunited.architecture.odata;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPATransaction;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;

public class JpaODataServiceFactory extends ODataJPAServiceFactory {

	private static Logger log = Logger.getLogger(JpaODataServiceFactory.class.getName());

	@Override
	public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException {
		ODataJPAContext oDataJPAContext = getODataJPAContext();
		setDetailErrors(true);

		InitialContext initialContext;
		try {
			initialContext = new InitialContext();
			// BeanManager beanManager = (BeanManager)
			// initialContext.lookup("java:comp/BeanManager");
			EntityManager entityManager = (EntityManager) initialContext.lookup("java:/blueprintEntityManager");
			oDataJPAContext.setEntityManager(entityManager);
			EntityManagerFactory entityManagerFactory = (EntityManagerFactory) initialContext
					.lookup("java:/blueprintEntityManagerFactory");
			oDataJPAContext.setEntityManagerFactory(entityManagerFactory);
			oDataJPAContext.setPersistenceUnitName("dashboard");
			oDataJPAContext.setContainerManaged(true);
			oDataJPAContext.setODataProcessor(new ETagCacheControlODataJPAProcessor(oDataJPAContext));
			oDataJPAContext.setJPAEdmExtension(new ETagJPAEDMExtension());
			final UserTransaction userTransaction = (UserTransaction) initialContext
					.lookup("java:comp/UserTransaction");
			setODataJPATransaction(new ODataJPATransaction() {

				@Override
				public void rollback() {
					try {
						userTransaction.rollback();
					} catch (IllegalStateException | SecurityException | SystemException e) {
						log.log(Level.SEVERE, "Problem with rollback", e);
					}
				}

				@Override
				public boolean isActive() {
					try {
						return userTransaction.getStatus() == Status.STATUS_ACTIVE;
					} catch (SystemException e) {
						log.log(Level.SEVERE, "Problem with isActive", e);
						return false;
					}
				}

				@Override
				public void commit() {
					try {
						userTransaction.commit();
					} catch (SecurityException | IllegalStateException | RollbackException | HeuristicMixedException
							| HeuristicRollbackException | SystemException e) {
						log.log(Level.SEVERE, "Problem with commit", e);
					}
				}

				@Override
				public void begin() {
					try {
						userTransaction.begin();
					} catch (NotSupportedException | SystemException e) {
						log.log(Level.SEVERE, "Problem with begin", e);
					}
				}
			});
			return oDataJPAContext;
		} catch (NamingException e) {
			throw ODataJPARuntimeException.throwException(ODataJPARuntimeException.ENTITY_MANAGER_NOT_INITIALIZED, e);
		}
	}

	@Override
	public ODataSingleProcessor createCustomODataProcessor(ODataJPAContext oDataJPAContext) {
	    return (ODataSingleProcessor) oDataJPAContext.getODataProcessor();
	  }

}
