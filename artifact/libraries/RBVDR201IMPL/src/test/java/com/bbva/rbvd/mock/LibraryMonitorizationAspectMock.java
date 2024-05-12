package com.bbva.rbvd.mock;

import com.bbva.elara.aspect.LibraryMonitorizationAspect;
import org.aspectj.lang.ProceedingJoinPoint;

public class LibraryMonitorizationAspectMock implements LibraryMonitorizationAspect{

	@Override
	public Object getLibraryInfo(ProceedingJoinPoint arg0) throws Throwable {	
		return arg0.proceed();
	}
	
	@Override
	public Object allMethodsCheck(ProceedingJoinPoint arg0) throws Throwable {
		return arg0.proceed();
	}	

}
