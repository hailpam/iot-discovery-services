package com.verisign.iot.discovery.exceptions;

import com.verisign.iot.discovery.commons.StatusCode;

/**
 * A specific <code>DnsServiceException</code> raised whenever a configuration error raises.
 *
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @since 2015/05/02
 */
// TODO Booleans to be put into class variables
public class ConfigurationException extends DnsServiceException {

    private static final long serialVersionUID = -570005026279195680L;

    public ConfigurationException() {
        this("", null, false, true);
    }

    public ConfigurationException(String message) {
        this(message, null, false, true);
    }

    public ConfigurationException(Throwable cause) {
        this("", cause, false, true);
    }

    public ConfigurationException(String message, Throwable cause) {
        this(message, cause, false, true);
    }

    public ConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(StatusCode.CONFIGURATION_ERROR.toString() + message, cause, enableSuppression, writableStackTrace);
        this.raisingError = StatusCode.CONFIGURATION_ERROR;
    }

}
