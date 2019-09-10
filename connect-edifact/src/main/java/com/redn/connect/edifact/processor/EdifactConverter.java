package com.redn.connect.edifact.processor;

import java.io.IOException;
import com.redn.connect.connectlogger.ConnectLogger;
import com.redn.connect.connectlogger.config.ConnectorConfig;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

import com.redn.connect.edifact.constants.EdifactConstants;
import com.redn.connect.processor.connectconfig.ConnectConfiguration;
import com.redn.connect.util.ConnectUtils;
import com.redn.connect.util.LoggerUtil;
import com.redn.connect.vo.ConnectEnterpriseMessage;
import com.redn.connect.vo.ConnectEnterpriseMessage.EnterpriseBody;
import com.redn.connect.vo.EnterpriseHeader;

/**
 * 
 * @author Prathyusha.V
 *
 *         This class does the following tasks.
 * 
 *         1. Set the validated Edifact code to Connect EnterpriseMessage body.
 *         2. Derive the interface name using UNB and UNH segments
 */

public class EdifactConverter implements Callable {

	private static ConnectUtils connectUtils = new ConnectUtils();

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {

		MuleMessage message = eventContext.getMessage();

		ConnectEnterpriseMessage entepriseMsg = null;
		ConnectConfiguration connectConfig = null;
		String senderIdentification = null;
		ConnectorConfig connectorConfig = new ConnectorConfig();
		connectorConfig.setLoggerName("connect-edifact-logger");
		ConnectLogger logger = LoggerUtil.getDatabaseConenction();
		

		try {
			connectConfig = eventContext.getMuleContext().getRegistry().lookupObject("connectConfigBean");
			entepriseMsg = (ConnectEnterpriseMessage) message.getProperty(EdifactConstants.EDIFACT_ORIGINAL_REQ,
					PropertyScope.INVOCATION);

			// get Flow variables
			String edifactStr = message.getProperty(EdifactConstants.EDIFACT_MODIFIED_MSG, PropertyScope.INVOCATION);

			// get source system id
			int unbStartIndex = edifactStr.indexOf("UNB+");
			int unbEndIndex = StringUtils.indexOf(edifactStr, "'", unbStartIndex);

			String UNBSegment = edifactStr.substring(unbStartIndex, unbEndIndex);
			String[] unbElementsArray = StringUtils.split(UNBSegment, "+");

			if (unbElementsArray.length > 2) {
				String senderSegment = unbElementsArray[2];
				String[] unbCompositeElmts = StringUtils.split(senderSegment, ":");
				if (unbCompositeElmts.length > 1) {
					senderIdentification = unbCompositeElmts[0] + "_" + unbCompositeElmts[1];
					
				//	logger.info(entepriseMsg, null, null, "The senderIdentification key is :" +senderIdentification, connectorConfig);
				}
			}

			// get order type
			int unhStartIndex = edifactStr.indexOf("UNH+");
			int unhEndIndex = StringUtils.indexOf(edifactStr, "'", unhStartIndex);
			String UNHSeg = edifactStr.substring(unhStartIndex, unhEndIndex);
			
			//ConnectLogger logger = LoggerUtil.getDatabaseConenction();
			logger.info(entepriseMsg, null, null, "The UNHSeg key is :" +UNHSeg, connectorConfig);
			logger.info(entepriseMsg, null, null, "The senderIdentification key is :" +senderIdentification, connectorConfig);
			String UNHSegment =null;
			if(UNHSeg.contains("93A:UN")) {
			 UNHSegment = edifactStr.substring(edifactStr.indexOf("UNH+"), edifactStr.indexOf("93A:UN"));
			 message.setProperty("EDI_VERSION", "D93A", PropertyScope.INVOCATION);
			}
			else if(UNHSeg.contains("97A:UN")) {
				 UNHSegment = edifactStr.substring(edifactStr.indexOf("UNH+"), edifactStr.indexOf("97A:UN"));
				 message.setProperty("EDI_VERSION", "D97A", PropertyScope.INVOCATION);
				}
			else if(UNHSeg.contains("03A:UN")) {
				 UNHSegment = edifactStr.substring(edifactStr.indexOf("UNH+"), edifactStr.indexOf("03A:UN"));
				 message.setProperty("EDI_VERSION", "D03A", PropertyScope.INVOCATION);
				}
			else if(UNHSeg.contains("01B:UN")) {
				 UNHSegment = edifactStr.substring(edifactStr.indexOf("UNH+"), edifactStr.indexOf("01B:UN"));
				 message.setProperty("EDI_VERSION", "D01B", PropertyScope.INVOCATION);
				}
			else{
				
				logger.info(entepriseMsg, null, null, "This is not an accepted EDI vesrion. Hence check the EDI version in: " +UNHSeg, connectorConfig);
			}
				String[] split = StringUtils.split(UNHSegment, "+");
				
				logger.info(entepriseMsg, null, null, "The UNHSegment key is :" +UNHSegment, connectorConfig);

				if (split.length > 2) {
					String messageTypeSegment = split[2];
					String[] compositeElmts = StringUtils.split(messageTypeSegment, ":");
					if (compositeElmts.length > 0) {
						String compositeElmtName = compositeElmts[0].toUpperCase();

						if (compositeElmtName != null && senderIdentification != null) {
							String interfaceKey = EdifactConstants.EDIFACT_INTERFACE_PREFIX
									+ entepriseMsg.getEnterpriseHeader().getSourceSystem().toLowerCase() + "."
									+ compositeElmtName + "." + senderIdentification.toUpperCase();
							String interfaceName = connectConfig.get(interfaceKey);
							
							logger.info(entepriseMsg, null, null, "The interfaceName name is :" +interfaceName, connectorConfig);
							System.out.println("interfaceKey==================" + interfaceKey);
							if (interfaceName != null) {
								entepriseMsg.getEnterpriseHeader().setServiceName(interfaceName);
								message.setProperty(EdifactConstants.SERVICE_NAME, interfaceName,
										PropertyScope.INVOCATION);
							} else {
								message.setProperty(EdifactConstants.SERVICE_NAME, EdifactConstants.UNKNOWN_NAME,
										PropertyScope.INVOCATION);
								message.setProperty(EdifactConstants.UNKNOWN_NAME, interfaceKey,
										PropertyScope.INVOCATION);
								// throw new
								// ConnectException(EdifactConstants.INVALID_INTERFACE_CODE,
								// interfaceKey+" is not registered with mule",
								// Category.DATA, "Partner System");
							}
						}
					}
				}

			

			// Set edifact xml to EnterpriseMessage body
			connectUtils.updateConnectEnterprsieMessagePayload(entepriseMsg, edifactStr);
		} catch (Exception e) {
			message.setProperty(EdifactConstants.SERVICE_NAME, EdifactConstants.UNKNOWN_NAME, PropertyScope.INVOCATION);
			message.setProperty(EdifactConstants.UNKNOWN_NAME, "Invalid Edifact Found, error is" + e.getMessage(),
					PropertyScope.INVOCATION);
		}

		return entepriseMsg;
	}

}
