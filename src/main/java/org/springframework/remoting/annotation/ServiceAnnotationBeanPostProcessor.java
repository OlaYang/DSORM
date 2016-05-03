package org.springframework.remoting.annotation;

import java.rmi.RemoteException;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.rmi.RmiServiceExporter;

public class ServiceAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements PriorityOrdered {
	private int order = Ordered.LOWEST_PRECEDENCE - 1;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		RemoteService service = AnnotationUtils.findAnnotation(bean.getClass(), RemoteService.class);
		Object resultBean = bean;
		if (null != service) {
			// HttpInvoker 服务发布
			if (ServiceType.HTTP.equals(service.serviceType())) {
				if (!beanName.startsWith("/")) {
//					throw new FatalBeanException("Exception initializing  HttpInvokerService for " + beanName
//							+ ",beanName should bean start with \"/\".");
				}else{
					HttpInvokerServiceExporter httpInvokerServiceExporter = new HttpInvokerServiceExporter();
					httpInvokerServiceExporter.setServiceInterface(service.serviceInterface());
					httpInvokerServiceExporter.setService(bean);
					httpInvokerServiceExporter.afterPropertiesSet();
					resultBean = httpInvokerServiceExporter;
				}

				// Hessian 服务发布
			} else if (ServiceType.HESSIAN.equals(service.serviceType())) {
				if (!beanName.startsWith("/")) {
					throw new FatalBeanException("Exception initializing  HessianService for " + beanName + ",beanName should bean start with \"/\".");
				}
				HessianServiceExporter hessianServiceExporter = new HessianServiceExporter();
				hessianServiceExporter.setServiceInterface(service.serviceInterface());
				hessianServiceExporter.setService(bean);
				hessianServiceExporter.afterPropertiesSet();
				resultBean = hessianServiceExporter;
				// Burlap 服务发布
			} else if (ServiceType.BURLAP.equals(service.serviceType())) {

				// RMI 服务发布
			} else if (ServiceType.RMI.equals(service.serviceType())) {
				RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
				rmiServiceExporter.setServiceInterface(service.serviceInterface());
				rmiServiceExporter.setService(bean);
				RmiServiceProperty rmiServiceProperty = bean.getClass().getAnnotation(RmiServiceProperty.class);
				if (rmiServiceProperty != null) {
					rmiServiceExporter.setRegistryPort(rmiServiceProperty.registryPort());
				}
				String serviceName = beanName;
				if (serviceName.startsWith("/")) {
					serviceName = serviceName.substring(1);
				}
				rmiServiceExporter.setServiceName(serviceName);
				try {
					rmiServiceExporter.afterPropertiesSet();
				} catch (RemoteException remoteException) {
					throw new FatalBeanException("Exception initializing RmiServiceExporter", remoteException);
				}
				resultBean = rmiServiceExporter;
			}
		}
		return resultBean;
	}

	@Override
	public int getOrder() {
		return order;
	}
}
