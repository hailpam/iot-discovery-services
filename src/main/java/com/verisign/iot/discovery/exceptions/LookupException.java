package com.verisign.iot.discovery.exceptions;

import com.verisign.iot.discovery.commons.StatusCode;

/**
 * A specific <code>DnsServiceException</code> raised whenever a runtime lookup error raises.
 *
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @since 2015/05/02
 */
// TODO Booleans to be put into class variables
public class LookupException extends DnsServiceException {

    private static final long serialVersionUID = -7221387354272680529L;

    public LookupException(StatusCode error, String message) {
        this(error, message, null, false, false);
    }

    public LookupException(StatusCode error, Throwable cause) {
        this(error, "", cause, false, false);
    }

    public LookupException(StatusCode error, String message, Throwable cause) {
        this(error, message, cause, false, false);
    }

    public LookupException(StatusCode error, String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(error.toString() + message, cause, enableSuppression, writableStackTrace);
        this.raisingError = error;
    }

}
