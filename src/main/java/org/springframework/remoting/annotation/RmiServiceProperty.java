package org.springframework.remoting.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.rmi.registry.Registry;

import org.springframework.stereotype.Component;

/**
 * RemoteService辅助标签，在发布RMI服务时，用来指定RMI服务发布的端口。
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RmiServiceProperty {
	int registryPort() default Registry.REGISTRY_PORT;
}
