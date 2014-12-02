package javax.mvc;

/**
 * Created by sp106478 on 12/2/14.
 */
public interface Models {

    Object get(String name);

    void set(String name, Object model);
}
