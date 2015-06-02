package com.verisign.iot.discovery.utils;

import com.verisign.iot.discovery.commons.Constants;

/**
 * Created by tjmurphy on 6/2/15.
 */
public class RDataUtil {

	/**
	 * Extract the service-type dns-label prefix from a PTR record's rdata. If label is not found, return <code>null</code>.
	 * @param rData
	 * @return dnsLabel
	 */
	public static String getDnsLabelFromRData(String rData){
		if(rData.contains( Constants.LABEL )){
			return rData.substring(0, rData.indexOf(Constants.LABEL));
		}

		String[] allDnsLabelsInRData = rData.split( "\\." );
		if(allDnsLabelsInRData.length < 2){
			return null;
		}

		String transportProtocolLabel = allDnsLabelsInRData[1];
		if(transportProtocolLabel.equals( Constants.TCP ) || transportProtocolLabel.equals( Constants.UDP )){
			return allDnsLabelsInRData[0] + Constants.DNS_LABEL_DELIMITER + transportProtocolLabel;
		}

		return null;
	}

	public static String getServiceTypeNameFromRData(String rData){
		if(rData.contains( Constants.NAME )){
			return rData.substring(0, rData.indexOf(Constants.NAME));
		}
		return null;
	}

}
