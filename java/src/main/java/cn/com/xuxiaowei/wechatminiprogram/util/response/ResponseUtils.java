package cn.com.xuxiaowei.wechatminiprogram.util.response;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 响应数据 工具类
 *
 * @author xuxiaowei
 */
public class ResponseUtils {

    /**
     * 响应数据
     */
    public static void response(HttpServletResponse response, Map map) throws IOException {
        Object o = JSON.toJSON(map);
        response(response, o);
    }

    /**
     * 响应数据
     */
    public static void response(HttpServletResponse response, Object object) throws IOException {
        response.setContentType("text/json;charset=UTF-8");
        response.getWriter().println(object);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();

    }

}
