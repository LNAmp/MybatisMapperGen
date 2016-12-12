package cn.david.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {
	public String name() ;//表示映射的java interface的类全名
	public String paramId() default "";
	public String paramBean() default "";
}
