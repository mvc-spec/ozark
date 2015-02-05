package com.oracle.ozark.mapper;

import com.oracle.ozark.cdi.CdiUtil;
import org.glassfish.jersey.spi.ExtendedExceptionMapper;

import javax.inject.Inject;
import javax.mvc.mapper.OnConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;

/**
 * Class ConstraintViolationMapper.
 * <p/>
 * Uses a Jersey SPI to check for the @OnConstraintViolation annotation on the resource
 * method. If not defined, it returns <code>false</code> in <code>isMappable</code>,
 * falling back to the default exception mapping mechanism defined in JAX-RS.
 *
 * @author Santiago Pericas-Geertsen
 */
public class ConstraintViolationMapper implements ExtendedExceptionMapper<ConstraintViolationException> {

    @Inject
    private CdiUtil cdiUtil;

    @Context
    private ResourceInfo resourceInfo;

    private javax.mvc.mapper.ConstraintViolationMapper mapper;

    @Override
    public boolean isMappable(ConstraintViolationException exception) {
        final Method method = resourceInfo.getResourceMethod();
        if (method != null) {
            OnConstraintViolation an = method.getAnnotation(OnConstraintViolation.class);
            if (an == null) {
                final Class<?> resourceClass = resourceInfo.getResourceClass();
                an = resourceClass.getAnnotation(OnConstraintViolation.class);
            }
            if (an != null) {
                final Class<? extends javax.mvc.mapper.ConstraintViolationMapper> mapperClass = an.value();
                mapper = cdiUtil.newBean(mapperClass);
                if (mapper != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Response toResponse(ConstraintViolationException e) {
        return mapper.toResponse(e);
    }
}
