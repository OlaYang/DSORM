package me.springremoting.rmi.impl;

import java.util.Date;

import me.springremoting.rmi.RmiDateService;

import org.springframework.remoting.annotation.RemoteService;
import org.springframework.remoting.annotation.RmiServiceProperty;
import org.springframework.remoting.annotation.ServiceType;

/**
 * 
 */
@RemoteService(serviceInterface = RmiDateService.class, serviceType = ServiceType.RMI)
@RmiServiceProperty(registryPort = 1099)
public class RmiDateServiceImpl implements RmiDateService{
    @Override
    public Date getDate() {
        return new Date();
    }
}
