package cn.com.xuxiaowei.wechatminiprogram.controller;

import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import cn.com.xuxiaowei.wechatminiprogram.propertie.WxMaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息 RestController
 * <p>
 * 本类中的 URL，需要授权通过才能访问
 *
 * @author xuxiaowei
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoRestController {

    private final WxMaProperties wxMaProperties;

    @Autowired
    public UserInfoRestController(WxMaProperties wxMaProperties) {
        this.wxMaProperties = wxMaProperties;
    }

    /**
     * 基于内存的微信配置provider，在实际生产环境中应该将这些配置持久化
     */
    private static WxMaInMemoryConfig wxMaInMemoryConfig = new WxMaInMemoryConfig();
    private static final WxMaServiceImpl wxMaService = new WxMaServiceImpl();

    /**
     * 更新用户详细信息
     * <p>
     * 此方法在用户是首次同意授权后进行，如需测试此方法，请清理 授权数据或全部清理
     */
    @RequestMapping("/update.do")
    public Map<String, Object> getCookie(HttpServletRequest request, HttpServletResponse response,
                                         String encryptedData, String iv) {

        Map<String, Object> map = new HashMap<>(4);
        Map<String, Object> data = new HashMap<>(4);
        map.put("data", data);

        wxMaInMemoryConfig.setAppid(wxMaProperties.getAppid());
        wxMaInMemoryConfig.setSecret(wxMaProperties.getSecret());
        wxMaService.setWxMaConfig(wxMaInMemoryConfig);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principalObj = authentication.getPrincipal();

        if (principalObj instanceof org.springframework.security.core.userdetails.User) {

            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) principalObj;

            //
            String sessionKey = userDetails.getPassword();

            // 解密用户信息
            WxMaUserInfo wxMaUserInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

            // 获取 OpenID 的方式
            String openid = authentication.getName();

            map.put("code", 0);
            map.put("msg", "更新用户详细信息成功");
            data.put("wxMaUserInfo", wxMaUserInfo);

        } else {
            map.put("code", 1);
            map.put("msg", "更新用户详细信息失败！");
            data.put("details", "Security中未获取到用户！");
        }

        return map;
    }

}
