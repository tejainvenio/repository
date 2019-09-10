package com.redn.connect.edifact.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.redn.connect.connectlogger.ConnectLogger;
import com.redn.connect.edifact.constants.EdifactConstants;
import com.redn.connect.util.LoggerUtil;

import net.sf.saxon.functions.IndexOf;

/**
 * 
 * @author Prathyusha.V
 *
 *         This class is used to write utility classes for Connect-Edifact
 *         application
 */
public class EdifactUtils {

	/*
	 * This method will use to set the time stamp
	 */
	public static String getDateAsString() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmssZZZ");
		Date date = new Date();
		return formatter.format(date);
	}

	/*
	 * This will remove UNG, UNE segments to validate edifact code through mule
	 */
	public static String removeSpecialSegments(String edifactCode) {

		// remove UNE segment
		String substring ;
		substring = edifactCode.substring(0, StringUtils.lastIndexOf(edifactCode, "'"));
		String secondSubstring = substring.substring(0, StringUtils.lastIndexOf(substring, "'"));
		String thirdSubstring =secondSubstring.substring(StringUtils.lastIndexOf(secondSubstring, "'"), StringUtils.lastIndexOf(substring, "'"));
		if(thirdSubstring.contains(EdifactConstants.UNE_SEGMENT)){
		edifactCode = StringUtils.remove(edifactCode, thirdSubstring);
		}

		// remove UNG segment

		substring = edifactCode.substring(StringUtils.indexOf(edifactCode, "'")+1);
		substring = substring.substring(StringUtils.indexOf(substring, "'")+1);
		substring = substring.substring(0,StringUtils.indexOf(substring, "'")+1);
		if(substring.contains(EdifactConstants.UNG_SEGMENT)){
			edifactCode = StringUtils.remove(edifactCode, substring);
		}
		return edifactCode;
	}
}
