package jk_5.nailed.api.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * No description given
 *
 * @author jk-5
 */
public interface DataOwner {

    public void saveData();
    public void loadData();
    public String getId();
    public DataObject getData();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DataType{
        public String value();
    }
}
