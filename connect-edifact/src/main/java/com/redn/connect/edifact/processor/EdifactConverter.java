package com.redn.connect.edifact.processor;

import java.io.IOException;

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
import com.redn.connect.vo.ConnectEnterpriseMessage;
import com.redn.connect.vo.ConnectEnterpriseMessage.EnterpriseBody;
import com.redn.connect.vo.EnterpriseHeader;

/**
 * 
 * @author Prathyusha.V
 *
 * This class does the following tasks.
 * 
 * 1. Set the validated Edifact code to Connect EnterpriseMessage body.
 * 2. Derive the interface name using UNB and UNH segments
 */

public class EdifactConverter implements Callable{

	private static ConnectUtils connectUtils = new ConnectUtils();

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {

		MuleMessage message = eventContext.getMessage();	
				
		 ConnectEnterpriseMessage entepriseMsg = null;		 
		 ConnectConfiguration connectConfig = null;
		 String senderIdentification = null;
		 
		 try {
			connectConfig = eventContext.getMuleContext().getRegistry().lookupObject("connectConfigBean");
			entepriseMsg = (ConnectEnterpriseMessage)message.getProperty(EdifactConstants.EDIFACT_ORIGINAL_REQ, PropertyScope.INVOCATION);
			
			//get Flow variables
			String edifactStr = message.getProperty(EdifactConstants.EDIFACT_MODIFIED_MSG, PropertyScope.INVOCATION); 
			
			//get source system id
			int unbStartIndex = edifactStr.indexOf("UNB+");
			int unbEndIndex = StringUtils.indexOf(edifactStr, "'",unbStartIndex);
			
			 String UNBSegment = edifactStr.substring(unbStartIndex, unbEndIndex);
	         String[] unbElementsArray = StringUtils.split(UNBSegment, "+");
	         
	         if( unbElementsArray.length > 2) {
	        	 String senderSegment = unbElementsArray[2];
	        	String[] unbCompositeElmts = StringUtils.split(senderSegment, ":");
	        	if( unbCompositeElmts.length > 1) {
	        		senderIdentification = unbCompositeElmts[0]+"_"+unbCompositeElmts[1];
	        		
	        	}
	         }
			
			//get order type
            String UNHSegment = edifactStr.substring(edifactStr.indexOf("UNH+"), edifactStr.indexOf("97A:UN'"));
            String[] split = StringUtils.split(UNHSegment, "+");
            
            if( split.length > 2) {
            	String messageTypeSegment = split[2];
            	String[] compositeElmts = StringUtils.split(messageTypeSegment, ":");
            	if( compositeElmts.length > 0) {
	            	String compositeElmtName = compositeElmts[0].toUpperCase();
	            	
	            	if( compositeElmtName != null && senderIdentification != null) {
	            		String interfaceKey = EdifactConstants.EDIFACT_INTERFACE_PREFIX + entepriseMsg.getEnterpriseHeader().getSourceSystem().toLowerCase()+"."+ compositeElmtName+"."+senderIdentification.toUpperCase();
	            		String interfaceName = connectConfig.get(interfaceKey);     
	            		System.out.println("interfaceKey=================="+interfaceKey);
	            		if( interfaceName != null) {
	            			 entepriseMsg.getEnterpriseHeader().setServiceName(interfaceName);
	            			message.setProperty(EdifactConstants.SERVICE_NAME, interfaceName, PropertyScope.INVOCATION);
	            		}
	            		else {
	            			message.setProperty(EdifactConstants.SERVICE_NAME, EdifactConstants.UNKNOWN_NAME, PropertyScope.INVOCATION);
	            			message.setProperty(EdifactConstants.UNKNOWN_NAME, interfaceKey, PropertyScope.INVOCATION);
	            			//throw new ConnectException(EdifactConstants.INVALID_INTERFACE_CODE, interfaceKey+" is not registered with mule", Category.DATA, "Partner System");
	            		}
	            	}     
            	}
            }
            
            //Set edifact xml to EnterpriseMessage body    
            connectUtils.updateConnectEnterprsieMessagePayload(entepriseMsg, edifactStr);
		 }
		 catch(Exception e) {
			 message.setProperty(EdifactConstants.SERVICE_NAME, EdifactConstants.UNKNOWN_NAME, PropertyScope.INVOCATION);
 			message.setProperty(EdifactConstants.UNKNOWN_NAME, "Invalid Edifact Found, error is"+e.getMessage(), PropertyScope.INVOCATION);
		 }
           
        return entepriseMsg;
	}	
	

}


