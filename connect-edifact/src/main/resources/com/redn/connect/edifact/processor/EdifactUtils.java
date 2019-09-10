package com.redn.connect.edifact.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Prathyusha.V
 *
 * This class is used to write utility classes for Connect-Edifact application
 */
public class EdifactUtils {

	/*
	 *  This method will use to set the time stamp
	 */
	public static String getDateAsString() {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmssZZZ");
		Date date = new Date();
		return formatter.format(date);
	}
	
	/*
	 *  This will remove UNG, UNE segments to validate 
	 *  edifact code through mule
	 */
	public static String removeSpecialSegments(String edifactCode){
		
		//remove UNE segment
		int strtInx = StringUtils.indexOf(edifactCode,"UNE+");
		
		if( strtInx != -1) {
			int endIndx = StringUtils.indexOf(edifactCode, "'",strtInx);
			String UNESegment = edifactCode.substring(strtInx,endIndx +1);
			edifactCode = StringUtils.remove(edifactCode, UNESegment);
		}
		
		//remove UNG segment
		 int ungStrtInx = StringUtils.indexOf(edifactCode,"UNG+");
		 if( ungStrtInx != -1) {
			 int ungEndIndx = StringUtils.indexOf(edifactCode, "'",ungStrtInx);
			 String UNGSegment = edifactCode.substring(ungStrtInx,ungEndIndx +1);
			 edifactCode = StringUtils.remove(edifactCode, UNGSegment);
		 }
		
		return edifactCode;
	}
}
