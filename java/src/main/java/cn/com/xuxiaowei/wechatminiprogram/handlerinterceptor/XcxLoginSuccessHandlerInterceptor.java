package cn.com.xuxiaowei.wechatminiprogram.handlerinterceptor;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.com.xuxiaowei.wechatminiprogram.filter.XcxLoginAbstractAuthenticationProcessingFilter;
import cn.com.xuxiaowei.wechatminiprogram.util.response.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序 登录成功 拦截器
 * <p>
 * 登录成功，默认会跳转到首页，这里根据登录成功的标识 loginSuccess 进行判断并返回数据
 * <p>
 * 不可在 {@link XcxLoginAbstractAuthenticationProcessingFilter} 中响应数据（如果响应了，则登录不成功）
 *
 * @author xuxiaowei
 */
@Slf4j
public class XcxLoginSuccessHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        Object loginSuccess = session.getAttribute("loginSuccess");

        if (loginSuccess instanceof Boolean) {

            session.setAttribute("loginSuccess", null);

            WxMaUserInfo wxMaUserInfo = (WxMaUserInfo) session.getAttribute("wxMaUserInfo");

            // 响应数据
            Map<String, Object> map = new HashMap<>(4);
            Map<String, Object> data = new HashMap<>(4);
            map.put("data", data);

            map.put("code", 0);
            map.put("msg", "微信小程序登录成功");
            data.put("wxMaUserInfo", wxMaUserInfo);
            log.debug(map.toString());

            ResponseUtils.response(response, map);

            return false;
        } else {
            return true;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
