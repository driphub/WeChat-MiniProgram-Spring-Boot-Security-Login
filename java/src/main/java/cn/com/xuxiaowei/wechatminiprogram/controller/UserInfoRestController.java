package cn.com.xuxiaowei.wechatminiprogram.controller;

import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import cn.com.xuxiaowei.wechatminiprogram.propertie.WxMaProperties;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
     *
     * @param encryptedData
     * @param iv
     * @param rawData
     */
    @RequestMapping("/update.do")
    public Map<String, Object> getCookie(HttpServletRequest request, HttpServletResponse response,
                                         String encryptedData, String iv, String rawData) {

        log.debug("前台发送的用户信息为：" + rawData);

        Map<String, Object> map = new HashMap<>(4);
        Map<String, Object> data = new HashMap<>(4);
        map.put("data", data);

        wxMaInMemoryConfig.setAppid(wxMaProperties.getAppid());
        wxMaInMemoryConfig.setSecret(wxMaProperties.getSecret());
        wxMaService.setWxMaConfig(wxMaInMemoryConfig);

        // 获取 Security 中的授权信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 获取 用户（username、password、authorities 等等） Object
        Object principalObj = authentication.getPrincipal();

        // 用户类型转换
        if (principalObj instanceof org.springframework.security.core.userdetails.User) {

            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) principalObj;

            // 获取 OpenID
            // 方式一：<code>String openid = authentication.getName();</code>
            // 方式二：<code>String openid = userDetails.getUsername();</code>

            // 会话密钥
            String sessionKey = userDetails.getPassword();

            // 解密用户信息
            WxMaUserInfo wxMaUserInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

            map.put("code", 0);
            map.put("msg", "更新用户详细信息成功");
            data.put("wxMaUserInfo", wxMaUserInfo);

        } else {
            map.put("code", 1);
            map.put("msg", "更新用户详细信息失败！");
            data.put("details", "Security 中未找到用户！");
        }

        return map;
    }

}
