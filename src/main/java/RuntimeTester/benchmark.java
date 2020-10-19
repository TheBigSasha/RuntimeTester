package RuntimeTester;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface benchmark {
    public String name();
    public String category() default "Custom Methods";
    public String expectedEfficiency() default "O(?)";
    public String description() default "";

}