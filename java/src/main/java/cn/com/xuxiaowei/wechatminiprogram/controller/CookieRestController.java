package cn.com.xuxiaowei.wechatminiprogram.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Cookie RestController
 *
 * @author xuxiaowei
 */
@RestController
@RequestMapping("/cookie")
public class CookieRestController {

    /**
     * 响应 Cookie，用户微信小程序登录前，Spring Boot 创建 Session 并响应给小程序 Cookie
     */
    @RequestMapping("/getCookice.do")
    public Map<String, Object> getCookie(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> map = new HashMap<>(4);
        Map<String, Object> data = new HashMap<>(4);
        map.put("data", data);

        // 必须存在，创建 Session 并响应 Cookie
        HttpSession session = request.getSession();

        return map;
    }

}
