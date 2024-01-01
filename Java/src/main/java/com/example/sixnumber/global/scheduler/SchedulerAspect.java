package com.example.sixnumber.global.scheduler;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SchedulerAspect {

	@Value("${SCHEDULER_ENABLED}")
	private boolean schedulerEnabled;

	@Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
	public void scheduledMethods() {}

	@Before("scheduledMethods()")
	public void beforeScheduledMethod(JoinPoint joinPoint) {
		if (!schedulerEnabled) throw new IllegalArgumentException("스케줄러 미동작 컨테이너");
	}
}
