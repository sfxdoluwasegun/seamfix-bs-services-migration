package com.sf.biocapture.ws;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethodInvoker;

import com.sf.biocapture.analyzer.IntrusionAnalyzer;
import com.sf.biocapture.app.BsClazz;

@Provider
@ServerInterceptor
public class KsSecurityFilter extends BsClazz implements ContainerRequestFilter{

	protected final String AUTH_PROPERTY_NAME = "sc-auth-key";
	protected final String AUTH_HEADER_KEY = "#D8CK>HIGH<LOW>#";

	@Context
	protected HttpServletRequest req;

	@Inject
	protected IntrusionAnalyzer analyzer;

	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		ResourceMethodInvoker mi = (ResourceMethodInvoker) request.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");


		// bypass all permit all request
		if(mi.getMethod().isAnnotationPresent(PermitAll.class)){
			logger.debug("PermitAll Request");
			return;
		}
                
		if(!analyzer.analyze(req, request)){
			request.abortWith(Response.status(Status.UNAUTHORIZED).entity("Not Authorized").build());
			return;
		}

		String scUuid = request.getHeaderString("User-UUID");
		String client = request.getHeaderString("Client-ID");
		KsPrincipal user = getPrincipal(scUuid, client);
		if(user == null){
			request.abortWith(Response.status(Status.UNAUTHORIZED).entity("Authentication Required").build());
		}

		request.setSecurityContext(new KsSecurityContext(user));

		if(mi.getMethod().isAnnotationPresent(DenyAll.class)){
			request.abortWith(Response.status(Status.FORBIDDEN).entity("Forbidden").build());
			return;
		}else if(mi.getMethod().isAnnotationPresent(RolesAllowed.class)){

			RolesAllowed ra = mi.getMethod().getAnnotation(RolesAllowed.class);
			Set<String> rolesSet = new HashSet<String>(Arrays.asList(ra.value()));
			if(!isUserInRoles(rolesSet, user)){
				request.abortWith(Response.status(Status.FORBIDDEN).entity("Forbidden").build());
				return;
			}
		}

	}

	protected boolean isUserInRoles(Set<String> rolesSet, KsPrincipal user){
		for(String role: user.getRoles()){
			if(rolesSet.contains(role)){
				return true;
			}
		}

		return false;
	}
        
        

	protected KsPrincipal getPrincipal(String uuid, String client){
		if(uuid != null){
			KsPrincipal kp = new KsPrincipal(uuid);
			kp.addRole("smartclient");
			return kp;
		}else if(client != null){
			KsPrincipal kp = new KsPrincipal(client.toLowerCase());
			kp.addRole(client.toLowerCase());
			return kp;
		}else{
			KsPrincipal kp = new KsPrincipal("guest");
			kp.addRole("guest");
			return kp;
		}
	}

}
