package top.candysky.kafka.collector.util;

import org.jboss.logging.MDC;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/*
在这里将自定义的东西通过放在MDC里面展示出来
 */
@Component
public class InputMDC implements EnvironmentAware {

	private static Environment environment;
	
	@Override
	public void setEnvironment(Environment environment) {
		InputMDC.environment = environment;
	}
	
	public static void putMDC() {
		MDC.put("hostName", NetUtil.getLocalHostName());
		MDC.put("ip", NetUtil.getLocalIp());
		/*
		applicationName是在配置文件里配置好的，需要通过一个environment来获取
		 */
		MDC.put("applicationName", environment.getProperty("spring.application.name"));
	}

}
