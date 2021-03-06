package top.candyboy.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.candyboy.constant.Constant;
import top.candyboy.utils.IMOOCJSONResult;
import top.candyboy.utils.JsonUtils;
import top.candyboy.redis.RedisOperator;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class UserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    RedisOperator redisOperator;

    /**
     * 拦截的请求，在访问controller访问之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = request.getHeader("userId");
        String headerUserToken = request.getHeader("headerUserToken");

        if (StringUtils.isNoneBlank(userId) && StringUtils.isNoneBlank(headerUserToken)) {
            String redisUserToken = redisOperator.get(Constant.REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(redisUserToken)) {
                //System.out.println("请登录");
                returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录"));
                return false;
            } else {
                if (!redisUserToken.equals(headerUserToken)) {
                    //System.out.println("登录信息不一致");
                    returnErrorResponse(response, IMOOCJSONResult.errorMsg("登录信息不一致"));
                    return false;
                }
            }
        } else {
            //System.out.println("请登录");
            returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录"));
            return false;
        }

        /*
        false：请求被拦截，被驳回，验证出现问题
        true：请求在经过校验之后，是OK的
         */
        //return false;
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, IMOOCJSONResult result) {
        ServletOutputStream outputStream = null;
        try {
            response.setCharacterEncoding("utf-8");
            // json的形式
            response.setContentType("text/json");
            outputStream = response.getOutputStream();
            outputStream.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 在请求controller之后，渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 渲染视图之后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
