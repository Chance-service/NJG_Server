package org.guaji.net.protocol;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：Apr 19, 2019 2:55:03 PM
* 类说明
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE })
public @interface ProtocolTimer {

}
