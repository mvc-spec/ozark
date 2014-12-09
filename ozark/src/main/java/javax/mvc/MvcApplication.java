package javax.mvc;

import com.oracle.ozark.core.StringWriterInterceptor;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Class MvcApplication.
 *
 * @author Santiago Pericas-Geertsen
 */
public class MvcApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> set = new HashSet<>();
        set.add(StringWriterInterceptor.class);
        return set;
    }
}
