
package com.sf.biocapture.ws.access;

import com.sf.biocapture.ws.ResponseData;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sf.biocapture.ws.tags.ClientRefRequest;
import javax.ws.rs.core.HttpHeaders;

public interface IAccessService {

        public static final String TAG = "tag";
        public static final String EMAIL = "email";
        public static final String MSISDN = "msisdn";
        
	@GET
	@Path("/blacklist/{tag}")
	public Response blacklist(@PathParam(TAG) String tag);

	@GET
	@Path("/blacklist/{tag}/{agentEmail}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response blacklist(@PathParam(TAG) String tag, @PathParam("agentEmail") String email);

	@POST
	@Path("/blacklist")
	@Produces(MediaType.APPLICATION_JSON)
	public Response blacklist(@FormParam(TAG) String tag, @FormParam("agentEmail") String email, @FormParam("mac") String mac, @Context HttpHeaders headers);

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public AccessResponse login(@FormParam(EMAIL) String email, @FormParam("pw") String password, @FormParam(TAG) String tag);

	/**
	 * Only used for kits running legacy versions
	 * (windows <= v2.0; Droid <= v1.34)
	 * @param email
	 * @param password
	 * @param msisdn
	 * @param tag
	 * @return
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public AccessResponse login(@FormParam(EMAIL) String email, @FormParam("pw") String password, @FormParam(MSISDN) String msisdn, @FormParam(TAG) String tag);

	@POST
	@Path("/ctlogin")
	@Produces(MediaType.APPLICATION_JSON)
	public AccessResponse loginClientTime(@FormParam(value = EMAIL)
                String email, @FormParam(value = "pw")
                        String password, @FormParam(value = TAG)
                                String tag, @FormParam(value = "clientTime")
    String clientTime);

	@POST
	@Path("/slogin")
	@Produces(MediaType.APPLICATION_JSON)
	public AccessResponse login(@Context HttpServletRequest req);

	@POST
	@Path("/reset")
	@Produces(MediaType.APPLICATION_JSON)
	public AccessResponse changePassword(@FormParam(EMAIL) String email, @FormParam("pw") String password);

	@POST
	@Path("/settings/{ref}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response settings(@Context HttpHeaders headers, @PathParam("ref") String ref, @PathParam("type") String type, ClientRefRequest req);

	@POST
	@Path("/otplogin")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseData otpLogin(@Context HttpHeaders headers, @FormParam(EMAIL) String email, @FormParam("pw") String password, @FormParam(TAG) String tag, @FormParam("clientTime") String clientTime);

        @POST
	@Path("/otplogin/verify")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseData verifyOtpLogin(@FormParam(MSISDN) String mobile, @FormParam("otp") String otp);
        
	@POST
	@Path("/onboarding/validate/email")
	@Produces(MediaType.APPLICATION_JSON)
	public AgentOnboardingResponseData validateAgentEmail(@FormParam(EMAIL) String emailAddress);
        
	@POST
	@Path("/onboarding/validate/otp")
	@Produces(MediaType.APPLICATION_JSON)
	public AgentOnboardingResponseData validateAgentOtp(@FormParam(MSISDN) String mobile, @FormParam("otp") String otp);

	@POST
	@Path("/fetch/fp")
	@Produces(MediaType.APPLICATION_JSON)
	public FingerLoginResponse fetchAgentFingerprints(@FormParam(EMAIL) String agentEmail);
	
	@POST
	@Path("/privileges")
	@Produces(MediaType.APPLICATION_JSON)
	public FetchPrivilegesResponse getUserPrivileges(@FormParam(EMAIL) String email);

	@POST
	@Path("/test-user")
	@Produces(MediaType.APPLICATION_JSON)
	public AccessResponse testUsers(@FormParam(EMAIL) String email, @FormParam("phone") String phone);
         @POST
        @Path("/forgotpassword")
        @Produces(MediaType.APPLICATION_JSON)
        public ForgotPasswordResponse sendTemporaryPassword(@FormParam(EMAIL) String email);
        
        
        @POST
        @Path("/update/password")
        @Produces(MediaType.APPLICATION_JSON)
        public ResponseData changeLoggedInPassword(@FormParam(EMAIL) String agentEmail, @FormParam("old_pw") String oldPassword, @FormParam("new_pw") String newPassword);


}

