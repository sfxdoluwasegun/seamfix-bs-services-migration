package com.sf.biocapture.ws.notification;

import javax.inject.Inject;
import javax.ws.rs.Path;

import com.sf.biocapture.app.BsClazz;
import javax.ws.rs.core.HttpHeaders;

/**
 * 
 * @author Nnanna
 * @since 10/11/2016
 *
 */
@Path("/notifications")
public class NotificationService  extends BsClazz implements INotificationService {
	@Inject
	NotificationDS notificationDs;

	@Override
	public NotificationResponse load(HttpHeaders headers,NotificationRequest request) {
		return notificationDs.getUserNotifications(request, headers);
	}

}
