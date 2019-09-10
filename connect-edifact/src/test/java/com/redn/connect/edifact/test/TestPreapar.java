package com.redn.connect.edifact.test;

import com.redn.connect.util.ConnectUtils;
import com.redn.connect.vo.ConnectEnterpriseMessage;
import com.redn.connect.vo.EnterpriseHeader;

public class TestPreapar {
	public static Object getCEM(String str) throws Exception {
		ConnectUtils connectUtils = new ConnectUtils();

		ConnectEnterpriseMessage connectEnterpriseMsg = connectUtils.buildConnectEnterprsieMessage(str);
		EnterpriseHeader head = connectEnterpriseMsg.getEnterpriseHeader();
		head.setAction("Async");
		head.setMessageId("8f910ee0-566a-11e7-9318-c61d20524153");
		head.setServiceName("redhat003");
		head.setSourceSystem("redhat");
		return connectEnterpriseMsg;
	}

}
