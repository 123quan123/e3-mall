package cn.e3mall.search.ecxeption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理器
 * @author quan
 * @create 2020-05-10 13:44
 */
public class GlobalExceptionresolver implements HandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionresolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) {
        //打印控制台
        e.printStackTrace();
        //写日志
        logger.debug("测试输出的日志");
        logger.info("系统异常info");
        logger.error("系统发生异常", e);
        //发邮件，发信息

        //显示错误页面
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/exception");
        return modelAndView;
    }
}
