package com.oracle.ozark.cdi;

import java.lang.annotation.Annotation;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.mvc.HttpHeader;
import javax.servlet.http.HttpServletRequest;

/**
 * Producer class.
 *
 * @author Santiago.Pericas-Geertsen@oracle.com
 */
public class CdiProducer {

    @Inject
    private Instance<HttpServletRequest> request;

    @Produces @HttpHeader
    public String acceptHeader(InjectionPoint ip) {
        for (Annotation a : ip.getQualifiers()) {
            if (a instanceof HttpHeader) {
                final HttpHeader header = (HttpHeader) a;
                // Use annotation's value or member name if not provided
                return request.get().getHeader(header.value().length() > 0
                        ? header.value() : ip.getMember().getName());
            }
        }
        throw new InternalError("Oops");
    }
}
