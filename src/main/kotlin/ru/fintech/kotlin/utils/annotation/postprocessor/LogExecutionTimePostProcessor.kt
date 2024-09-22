package ru.fintech.kotlin.utils.annotation.postprocessor

import org.aopalliance.intercept.MethodInterceptor
import org.slf4j.LoggerFactory
import org.springframework.aop.framework.ProxyFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import ru.fintech.kotlin.utils.annotation.LogExecutionTime
import kotlin.system.measureTimeMillis

@Component
class LogExecutionTimePostProcessor : BeanPostProcessor {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val beanClass = bean.javaClass

        val isClassAnnotated = beanClass.isAnnotationPresent(LogExecutionTime::class.java)
        val isMethodAnnotated = beanClass.methods.any { it.isAnnotationPresent(LogExecutionTime::class.java) }

        if (!isClassAnnotated && !isMethodAnnotated) {
            return bean
        }

        val proxyFactory = ProxyFactory(bean)
        proxyFactory.isProxyTargetClass = true

        proxyFactory.addAdvice(MethodInterceptor { invocation ->
            val method = invocation.method
            val isMethodLogExecutionTime = method.isAnnotationPresent(LogExecutionTime::class.java)
            var result: Any?

            val timeTaken = measureTimeMillis {
                result = invocation.proceed()
            }

            if (isMethodLogExecutionTime || isClassAnnotated) {
                log.info("Вызван метод: ${method.name} класса: ${beanClass.simpleName}, время исполнения: $timeTaken ms")
            }

            return@MethodInterceptor result
        })

        return proxyFactory.proxy
    }
}