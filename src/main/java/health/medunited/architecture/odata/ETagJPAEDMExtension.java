package health.medunited.architecture.odata;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.Version;

import org.apache.olingo.odata2.api.edm.EdmConcurrencyMode;
import org.apache.olingo.odata2.api.edm.EdmFacets;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Facets;
import org.apache.olingo.odata2.api.edm.provider.Mapping;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmExtension;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmSchemaView;
import org.apache.olingo.odata2.jpa.processor.core.model.JPAEdmMappingImpl;

public class ETagJPAEDMExtension implements JPAEdmExtension {
	
	private static final Logger log = Logger.getLogger(ETagJPAEDMExtension.class.getName());

	@Override
	public void extendJPAEdmSchema(JPAEdmSchemaView view) {
		Schema edmSchema = view.getEdmSchema();
		for(EntityType entityType : edmSchema.getEntityTypes()) {
			Mapping entityMapping = entityType.getMapping();
			if(entityMapping instanceof JPAEdmMappingImpl) {
				JPAEdmMappingImpl entityJPAEdmMappingImpl = (JPAEdmMappingImpl) entityMapping;
				
			Class<?> entityClass = entityJPAEdmMappingImpl.getJPAType();
			for(Property property : entityType.getProperties()) {
				if(property instanceof SimpleProperty) {
					SimpleProperty simpleProperty = (SimpleProperty) property;
					Mapping mapping = simpleProperty.getMapping();
					if(mapping instanceof JPAEdmMappingImpl) {
						try {
							Field field = entityClass.getDeclaredField(mapping.getInternalName());
							Version version = field.getAnnotation(Version.class);
							if(version != null) {
								Facets facets = new Facets();
								EdmFacets oldFacet = simpleProperty.getFacets();
								if(oldFacet != null) {
									facets.setCollation(oldFacet.getCollation());
									facets.setDefaultValue(oldFacet.getDefaultValue());
									facets.setMaxLength(oldFacet.getMaxLength());
									facets.setFixedLength(oldFacet.isFixedLength());
									facets.setNullable(oldFacet.isNullable());
									facets.setPrecision(oldFacet.getPrecision());
									facets.setScale(oldFacet.getScale());
									facets.setUnicode(oldFacet.isUnicode());
								}
								facets.setConcurrencyMode(EdmConcurrencyMode.Fixed);
								simpleProperty.setFacets(facets);
							}
						} catch (NoSuchFieldException | SecurityException e) {
							log.log(Level.SEVERE, "Could not get field ", e);
						}
					}
				}
			}
			}
		}

	}

	@Override
	public void extendWithOperation(JPAEdmSchemaView view) {

	}

	@Override
	public InputStream getJPAEdmMappingModelStream() {
		return null;
	}

}
