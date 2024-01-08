package health.medunited.architecture.provider;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;

@RequestScoped
public class ContextTypeProducer {

  @Inject
  HttpServletRequest httpServletRequest;

  @Produces
  @RequestScoped
  public ContextType produce() {
    ContextType contextType = new ContextType();
    contextType.setMandantId(httpServletRequest.getHeader("x-mandant-id"));
    contextType.setClientSystemId(httpServletRequest.getHeader("x-client-system-id"));
    contextType.setWorkplaceId(httpServletRequest.getHeader("x-workplace-id"));
    contextType.setUserId(httpServletRequest.getHeader("x-user-id"));
    return contextType;
  }

  public static ContextType copyValuesFromProxyIntoContextType(ContextType contextType) {
    ContextType context = new ContextType();
    context.setMandantId(contextType.getMandantId());
    context.setWorkplaceId(contextType.getWorkplaceId());
    context.setClientSystemId(contextType.getClientSystemId());
    context.setUserId(contextType.getUserId());
    return context;
  }
}
