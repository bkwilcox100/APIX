package com.heb.liquidsky.spring.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.pubsub.GooglePubSubImpl;
import com.heb.liquidsky.pubsub.HEBPubSub;
import com.heb.liquidsky.pubsub.HebPubSubMessage;

/**
 * Receive a push message from the pub/sub service - based on
 * com.google.cloud.pubsub.client.demos.appengine.servlet.ReceiveMessageServlet
 */
@Controller
public class PubSubServlet {

	private static final Logger logger = Logger.getLogger(PubSubServlet.class.getName());

	@PostMapping("/pubSubServlet")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Validating unique subscription token before processing the message
		String subscriptionToken = CloudUtil.getProperty(HEBPubSub.PUBSUB_SECURITY_TOKEN);
		if (!StringUtils.equals(subscriptionToken, req.getParameter(GooglePubSubImpl.PUBSUB_TOKEN_PARAM))) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().close();
			return;
		}
		HebPubSubMessage message = new HebPubSubMessage(req.getInputStream());
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Processing pub/sub message " + message.toPrettyString());
		}
		try {
			HEBPubSub.getInstance().processMessage(message);
		} catch (DataStoreException e) {
			// TODO - do not ack if there is an error
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure processing message " + message.toPrettyString(), e);
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Successfully received pub/sub message from application " + message.getData().getSourceApplication());
		}
		// Acknowledge the message by returning a success code
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().close();
	}
}
