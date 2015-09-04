package com.mmt.dpt.cassandra.component.main;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;

/**
 * Couchbase exception.
 */

public class CassandraException extends CamelExchangeException {

    private static final long serialVersionUID = 1L;

    public CassandraException(String message, Exchange exchange) {
        super(message, exchange);
    }

    public CassandraException(String message, Exchange exchange, Throwable cause) {
        super(message, exchange, cause);
    }
}
