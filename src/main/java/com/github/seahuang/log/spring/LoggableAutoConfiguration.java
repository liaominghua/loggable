package com.github.seahuang.log.spring;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.Scope;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

import com.github.seahuang.log.LoggableAspect;
import com.github.seahuang.log.Success;
import com.github.seahuang.log.duration.DefaultDurationFormatter;
import com.github.seahuang.log.duration.DefaultDurationRecorder;
import com.github.seahuang.log.duration.DurationFormatter;
import com.github.seahuang.log.duration.DurationRecorder;
import com.github.seahuang.log.formatter.ConstraintViolationExceptionLogFormatter;
import com.github.seahuang.log.formatter.LogFormatter;
import com.github.seahuang.log.formatter.SuccessLogFormatter;
import com.github.seahuang.log.formatter.ThrowableLogFormatter;
import com.github.seahuang.log.formatter.type.CompositeScriptTypeFormatter;
import com.github.seahuang.log.formatter.type.DefaultTypeFormatter;
import com.github.seahuang.log.formatter.type.DefaultTypeFormatterAdapter;
import com.github.seahuang.log.formatter.type.FastJsonTypeFormatter;
import com.github.seahuang.log.formatter.type.GsonTypeFormatter;
import com.github.seahuang.log.formatter.type.IgnoreTypeFormatter;
import com.github.seahuang.log.formatter.type.JacksonTypeFormatter;
import com.github.seahuang.log.formatter.type.LengthTypeFormatter;
import com.github.seahuang.log.formatter.type.ScriptEngineTypeFormatter;
import com.github.seahuang.log.formatter.type.ScriptTypeFormatter;
import com.github.seahuang.log.formatter.type.SpelTypeFormatter;
import com.github.seahuang.log.formatter.type.TypeFormatter;
import com.github.seahuang.log.formatter.type.TypeFormatterAdaper;
import com.github.seahuang.log.printer.DefaultLogPrinter;
import com.github.seahuang.log.printer.LogPrinter;

@Configuration
@ConditionalOnProperty(prefix="loggable", name="enabled", havingValue="true", matchIfMissing=true)
public class LoggableAutoConfiguration implements ImportAware {
	@Value("${loggable.globalLogDuration:false}")
	protected Boolean globalLogDuration = false;
	
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public LoggableAspect loggableAspect(){
		return new LoggableAspect();
	}
	
	@Bean
	@ConditionalOnMissingBean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public LogPrinter logPrinter(){
		return new DefaultLogPrinter();
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@ConditionalOnMissingBean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public DurationRecorder durationRecorder(){
		DefaultDurationRecorder recorder =  new DefaultDurationRecorder();
		recorder.setDefaultLogDuration(globalLogDuration);
		return recorder;
	}
	
	@Bean
	@ConditionalOnMissingBean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public DurationFormatter durationFormatter(){
		return new DefaultDurationFormatter();
	}
	
	@Bean(name="c.g.s.l.f.SuccessLogFormatter")
	@ConditionalOnMissingBean(name="c.g.s.l.f.SuccessLogFormatter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public LogFormatter<Success> successLogFormatter(){
		return new SuccessLogFormatter();
	}
	
	@Bean(name="c.g.s.l.f.ThrowableLogFormatter")
	@ConditionalOnMissingBean(name="c.g.s.l.f.ThrowableLogFormatter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public LogFormatter<Throwable> throwableLogFormatter(){
		return new ThrowableLogFormatter();
	}
	
	@Bean(name="c.g.s.l.f.ConstraintViolationException")
	@ConditionalOnMissingBean(name="c.g.s.l.f.ConstraintViolationException")
	@ConditionalOnClass(Validator.class)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public LogFormatter<ConstraintViolationException> constraintViolationExceptionLogFormatter(){
		return new ConstraintViolationExceptionLogFormatter();
	}
	
	@Bean(name="c.g.s.l.f.t.LengthTypeFormatter")
	@ConditionalOnMissingBean(name="c.g.s.l.f.t.LengthTypeFormatter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TypeFormatter lengthFormatter(){
		return new LengthTypeFormatter();
	}
	
	@Bean(name="c.g.s.l.f.t.IgnoreTypeFormatter")
	@ConditionalOnMissingBean(name="c.g.s.l.f.t.IgnoreTypeFormatter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TypeFormatter ignoreFormatter(){
		return new IgnoreTypeFormatter();
	}
	
	@Bean(name="c.g.s.l.f.t.ScriptTypeFormatter")
	@ConditionalOnMissingBean(name="c.g.s.l.f.t.ScriptTypeFormatterter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public ScriptTypeFormatter scriptTypeFormatter(){
		return new CompositeScriptTypeFormatter();
	}
	
	@Bean(name="c.g.s.l.f.t.SpelTypeFormatter")
	@ConditionalOnMissingBean(name="c.g.s.l.f.t.SpelTypeFormatterter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public ScriptTypeFormatter spelTypeFormatter(){
		return new SpelTypeFormatter();
	}
	
	@Bean(name="c.g.s.l.f.t.ScriptEngineTypeFormatter")
	@ConditionalOnMissingBean(name="c.g.s.l.f.t.ScriptEngineTypeFormatter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public ScriptTypeFormatter scriptEngineTypeFormatter(){
		return new ScriptEngineTypeFormatter();
	}
	
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	@ConditionalOnMissingBean
	public TypeFormatterAdaper typeFormatterAdapter(
		@Qualifier("c.g.s.l.f.t.LengthTypeFormatter")TypeFormatter lengthFormatter,
		@Qualifier("c.g.s.l.f.t.IgnoreTypeFormatter")TypeFormatter ignoreFormatter, 
		@Qualifier("c.g.s.l.f.t.DefaultTypeFormatter")TypeFormatter defaultFormatter,
		@Qualifier("c.g.s.l.f.t.ScriptTypeFormatter")ScriptTypeFormatter scriptTypeFormatter){
		return new DefaultTypeFormatterAdapter(lengthFormatter
				, ignoreFormatter, defaultFormatter, scriptTypeFormatter);
	}
	
	@Order(value = Ordered.LOWEST_PRECEDENCE - 3)
	@Bean(name="c.g.s.l.f.t.DefaultTypeFormatter")
	@ConditionalOnClass(name="com.alibaba.fastjson.JSON")
	@ConditionalOnMissingBean(name="c.g.s.l.f.t.DefaultTypeFormatter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TypeFormatter fastJsonTypeFormatter(){
		return new FastJsonTypeFormatter();
	}
	
	@Order(value = Ordered.LOWEST_PRECEDENCE - 2)
	@Bean(name="c.g.s.l.f.t.DefaultTypeFormatter")
	@ConditionalOnClass(name="com.fasterxml.jackson.databind.ObjectMapper")
	@ConditionalOnMissingBean(name="c.g.s.l.f.t.DefaultTypeFormatter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TypeFormatter jacksonTypeFormatter(){
		return new JacksonTypeFormatter();
	}
	
	@Order(value = Ordered.LOWEST_PRECEDENCE)
	@Bean(name="c.g.s.l.f.t.DefaultTypeFormatter")
	@ConditionalOnClass(name="com.google.gson.Gson")
	@ConditionalOnMissingBean(name="c.g.s.l.f.t.DefaultTypeFormatter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TypeFormatter gsonTypeFormatter(){
		return new GsonTypeFormatter();
	}
	
	@Order(value = Ordered.LOWEST_PRECEDENCE)
	@Bean(name="c.g.s.l.f.t.DefaultTypeFormatter")
	@ConditionalOnMissingBean(name="c.g.s.l.f.t.DefaultTypeFormatter")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TypeFormatter defaultTypeFormatter(){
		return new DefaultTypeFormatter();
	}
	
	@Bean
	@ConditionalOnMissingBean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public ParameterNameDiscoverer parameterNameDiscoverer(){
		return new LocalVariableTableParameterNameDiscoverer();
	}

	public void setImportMetadata(AnnotationMetadata importMetadata) {
		AnnotationAttributes enableLTSScheduling = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableLoggable.class.getName()));
		if(enableLTSScheduling != null){
			globalLogDuration = enableLTSScheduling.getBoolean("logDuration");
		}
	}

	public Boolean getGlobalLogDuration() {
		return globalLogDuration;
	}

	public void setGlobalLogDuration(Boolean globalLogDuration) {
		this.globalLogDuration = globalLogDuration;
	}
	
}
