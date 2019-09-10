package com.redn.connect.edifact.processor;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

import com.redn.connect.edifact.constants.EdifactConstants;
import com.redn.connect.util.ConnectUtils;
import com.redn.connect.vo.ConnectEnterpriseMessage;
import com.redn.connect.vo.ConnectEnterpriseMessage.EnterpriseBody;
import com.redn.connect.vo.EnterpriseHeader;

/**
 * 
 * @author Prathyusha.V
 *
 * This class is used to save required details from CEM object
 */
public class EdifactParser implements Callable{

	private static ConnectUtils connectUtils = new ConnectUtils();
	
	@SuppressWarnings("unchecked")
	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		
		MuleMessage message = eventContext.getMessage();
		ConnectEnterpriseMessage entepriseMsg = (ConnectEnterpriseMessage) eventContext.getMessage().getPayload();
		
		EnterpriseBody enterpriseBody = entepriseMsg.getEnterpriseBody();
		EnterpriseHeader enterpriseHeader = entepriseMsg.getEnterpriseHeader();
		
		String edifactText = connectUtils.getPayloadTextContentFromCEM(entepriseMsg);
		
		message.setProperty(EdifactConstants.EDIFACT_MSG, edifactText, PropertyScope.INVOCATION);
		message.setProperty(EdifactConstants.EDIFACT_ORIGINAL_REQ, entepriseMsg, PropertyScope.INVOCATION);
		message.setProperty(EdifactConstants.EDIFACT_MSG_ID, enterpriseHeader.getMessageId(), PropertyScope.INVOCATION);
		
		String newEdifactText = EdifactUtils.removeSpecialSegments(edifactText);
		message.setProperty(EdifactConstants.EDIFACT_MODIFIED_MSG, newEdifactText, PropertyScope.INVOCATION);
		message.setProperty(EdifactConstants.EDIFACT_TARGET_SYSTEM_FLOW, enterpriseHeader.getSourceSystem().toLowerCase()+EdifactConstants.SOURCE_SYSTEM_EDI_VALIDATE_FLOW_SUFFIX, PropertyScope.INVOCATION);
		
		return message;
	}

}
