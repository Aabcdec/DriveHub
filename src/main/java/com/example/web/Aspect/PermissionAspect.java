package com.example.web.Aspect;

import com.example.web.commons.DataScope;
import com.example.web.constant.Constants;
import com.example.web.query.BaseQuery;
import com.example.web.query.TokenWrapper;
import com.example.web.query.User;
import com.example.web.util.TokenUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class PermissionAspect {

    //aspectJ 实现aop
    @Resource
    private TokenUtil tokenUtil;
    //切入点
    @Pointcut(value = "@annotation(com.example.web.commons.DataScope)")
    private void pointCut() {

    }

    @Around(value = "pointCut()")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();

        //拿到方法上的注解
        DataScope dataScope = methodSignature.getMethod().getDeclaredAnnotation(DataScope.class);

        String tableAlias = dataScope.tableAlias();
        String tableField = dataScope.tableField();

        //在spring web容器中，可以拿到当前请求的request对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = request.getHeader(Constants.TOKEN_NAME);
        //从token中解析出该用户是管理员还是普通用户
        TokenWrapper tokenWrapper = tokenUtil.parseToken(token);

        //拿到用户的角色
        String name = tokenWrapper.getValue().getName();
        System.out.println(name);
        if (!name.equals("admin")) {//不包含admin角色，只查当前用户自己的数据，否则查所有数据
            Object params = joinPoint.getArgs()[2];//拿方法的第三个参数
            if (params instanceof BaseQuery) {
                BaseQuery query = (BaseQuery)params;
                //select * from t_user tu where tu.id = 2 （普通用户：于嫣）
                query.setFilterSQL(" and " + tableAlias + "." + tableField + " = " + tokenWrapper.getValue().getId());
                System.out.println("sql----------:"+query.getFilterSQL());

            }
        }

        System.out.println("目标方法执行之前....");
        Object result = joinPoint.proceed();
        System.out.println("目标方法执行之后....");
        return result;
    }
}