package RuntimeTester;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Benchmark {
    String name();

    String category() default "Other";

    String expectedEfficiency() default "O(?)";

    String description() default "";

    boolean theoretical() default false;

}
