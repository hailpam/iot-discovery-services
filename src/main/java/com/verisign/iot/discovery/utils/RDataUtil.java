package com.verisign.iot.discovery.utils;

import com.verisign.iot.discovery.commons.Constants;
import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;

/**
 * Created by tjmurphy on 6/2/15.
 */
public class RDataUtil
{

	/**
	 * Extract the service-type dns-label prefix from a PTR record's rdata. If label is not found, return <code>null</code>.
	 * @param rData
	 * @return dnsLabel
	 */
	public static String getDnsLabelFromRData(String rData)
    {
		if(rData == null || rData.trim().isEmpty()){
			throw new IllegalArgumentException( "rData cannot be null, empty, or blank" );
		}

		if(rData.contains( Constants.LABEL )){
			return rData.substring(0, rData.indexOf(Constants.LABEL));
		}

		Name nameInRData = null;
		try{
			nameInRData = new Name( rData.trim() );
		} catch(TextParseException tpe){
			throw new IllegalArgumentException( "rData must be valid domain name" );
		}

		if(nameInRData.labels() < 2){
			throw new IllegalArgumentException( "rData does not have enough labels to return dns label for a service type" );
		}

		String transportProtocolLabel = nameInRData.getLabelString( 1 );
		if(transportProtocolLabel.equals( Constants.TCP ) || transportProtocolLabel.equals( Constants.UDP )){
			return nameInRData.getLabelString( 0 ) + Constants.DNS_LABEL_DELIMITER + transportProtocolLabel;
		}

		throw new IllegalArgumentException( "could not extract dns label from rData" );
	}

	public static String getServiceTypeNameFromRData(String rData)
    {
		if(rData == null || rData.trim().isEmpty()){
			throw new IllegalArgumentException( "rData cannot be null, empty, or blank" );
		}

		if(rData.contains( Constants.NAME )){
			String serviceTypeName = rData.substring(0, rData.indexOf(Constants.NAME)).trim();
			if(!serviceTypeName.isEmpty()){
				return serviceTypeName;
			}
		}
		throw new IllegalArgumentException( "could not extract service type name from rData" );
	}

}
