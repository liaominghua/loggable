package com.github.seahuang.log.formatter;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;

import com.github.seahuang.log.Level;
import com.github.seahuang.log.Loggable;

public class ThrowableLogFormatter extends LogFormatterSupport<Throwable> {
	@Value("${c.g.s.l.f.SuccessLogFormatter.failureWord:Fail}")
	protected String failureWord = "Fail";

	public String format(Level level, JoinPoint jp, Throwable throwable) {
		MethodSignature methodSignature = (MethodSignature)jp.getSignature();
		Loggable loggable = AnnotationUtils.findAnnotation(methodSignature.getMethod(), Loggable.class);
		
		StringBuilder result = new StringBuilder(loggable.value())
			.append(" ").append(failureWord).append("! ")
			.append(methodSignature.getDeclaringType().getSimpleName())
			.append(".").append(methodSignature.getMethod().getName())
			.append("(");
		
		String[] parameterNames = methodSignature.getParameterNames();
		Annotation[][] parameterAnnotations = methodSignature.getMethod().getParameterAnnotations();
		Object[] arguments = jp.getArgs();
		for(int i = 0; i < parameterNames.length; i++){
			result.append(parameterNames[i]).append("=")
				.append(typeFormatterAdapter.format(parameterAnnotations[i], level, arguments[i]))
				.append(",");
		}
		if(',' == result.charAt(result.length() - 1)){
			result.deleteCharAt(result.length() - 1);
		}
		result.append(")");
		return result.toString();
	}

}
