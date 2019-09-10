package com.redn.connect.edifact.constants;

/**
 * 
 * @author Prathyusha.V
 * 
 * This class contains constants used for this project
 */
public class EdifactConstants {

	
	public static final String EDIFACT_MSG = "edifactMessage";
	public static final String EDIFACT_CONFIG_FILE_PATH = "edifactFileLocation";
	public static final String EDIFACT_FILE_NAME = "EdifactXML";	
	public static final String EDIFACT_INTERFACE_PREFIX = "connect.edifact.interface.name.";	
	public static final String EDIFACT_MSG_ID = "messageId";
	public static final String EDIFACT_ORIGINAL_REQ = "enterpriseMessage";
	public static final String EDIFACT_REPLACE_CHARACTER  = "#";
	public static final String EDIFACT_TARGET_SYSTEM_FLOW = "targetSystemFlow";
	public static final String EDIFACT_MODIFIED_MSG  = "ModifiedEdifactMessage";
	public static final String VAR_RESOURCE_ID  = "resourceId";
	public static final String VAR_MESSAGE_SOURCE  = "messageSource";
	public static final String VAR_MESSAGE_ACTION  = "messageAction";
	public static final String VAR_TARGET_SYSTEM  = "targetSystem";
	public static final String SERVICE_NAME = "serviceName";
	public static final String UNKNOWN_NAME = "Unknown";
	
	public static final String UNE_SEGMENT = "UNE+";
	public static final String UNG_SEGMENT = "UNG+";
	public static final String SOURCE_SYSTEM_EDI_VALIDATE_FLOW_SUFFIX  = "-edi-validate-flow";

	public static final String INVALID_EDIFACT_CODE = "500003130";
	
	public static final String INVALID_INTERFACE_CODE = "500003140";
	public static final String VAR_ERROR_CODE = "errorCode";
	public static final String VAR_ERROR_DESCRIPTION = "errorDescription";	
	public static final String ERROR_CODE_FILTER_UNACCEPTED="400003100";
	public static final String ERROR_CODE_JMS="400003110";
	public static final String ERROR_CODE_OTHER="500003120";
}
