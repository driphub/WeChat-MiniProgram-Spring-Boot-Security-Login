package cn.com.xuxiaowei.wechatminiprogram.controller;

import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
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
    private final String appid;
    private final String secret;

    @Autowired
    public UserInfoRestController(WxMaProperties wxMaProperties) {
        this.wxMaProperties = wxMaProperties;
        appid = wxMaProperties.getAppid();
        secret = wxMaProperties.getSecret();
        wxMaService.setWxMaConfig(wxMaInMemoryConfig);
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
     * @param encryptedData 包括敏感数据在内的完整用户信息的加密数据，详细见 加密数据解密算法
     *                      https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html#%E5%8A%A0%E5%AF%86%E6%95%B0%E6%8D%AE%E8%A7%A3%E5%AF%86%E7%AE%97%E6%B3%95
     * @param iv            加密算法的初始向量，详细见 加密数据解密算法
     *                      https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html#%E5%8A%A0%E5%AF%86%E6%95%B0%E6%8D%AE%E8%A7%A3%E5%AF%86%E7%AE%97%E6%B3%95
     * @param nickName      昵称
     * @param gender        性别
     * @param country       国家
     * @param province      省份
     * @param city          城市
     * @param avatarUrl     头像
     * @param language      语言
     */
    @RequestMapping("/update.do")
    public Map<String, Object> getCookie(HttpServletRequest request, HttpServletResponse response,
                                         String encryptedData, String iv,
                                         String nickName, String gender,
                                         String country, String province, String city,
                                         String avatarUrl,
                                         String language) {

        Map<String, Object> map = new HashMap<>(4);
        Map<String, Object> data = new HashMap<>(4);
        map.put("data", data);

        log.debug("前台发送的用户信息（不需要解密）为：");
        log.debug("昵称：" + nickName);
        log.debug("性别：" + gender);
        log.debug("国家：" + country);
        log.debug("省份：" + province);
        log.debug("城市：" + city);
        log.debug("头像：" + avatarUrl);
        log.debug("语言：" + language);

        ////////////////////////////// 下面是通过解密得到用户数据 //////////////////////////////

        // 会话密钥
        String sessionKey = getUserDetailsPassword();

        if (sessionKey != null) {

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


    /**
     * 获取用户手机号
     * <p>
     * 此方法在用户点击同意获取手机号
     *
     * @param encryptedData 包括敏感数据在内的完整用户信息的加密数据，详细见 加密数据解密算法
     *                      https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html#%E5%8A%A0%E5%AF%86%E6%95%B0%E6%8D%AE%E8%A7%A3%E5%AF%86%E7%AE%97%E6%B3%95
     * @param iv            加密算法的初始向量，详细见 加密数据解密算法
     *                      https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html#%E5%8A%A0%E5%AF%86%E6%95%B0%E6%8D%AE%E8%A7%A3%E5%AF%86%E7%AE%97%E6%B3%95
     */
    @RequestMapping("/getPhoneNumber.do")
    public Map<String, Object> getPhoneNumber(HttpServletRequest request, HttpServletResponse response,
                                              String encryptedData, String iv) {

        Map<String, Object> map = new HashMap<>(4);
        Map<String, Object> data = new HashMap<>(4);
        map.put("data", data);

        ////////////////////////////// 下面是通过解密得到用户数据 //////////////////////////////

        // 会话密钥
        String sessionKey = getUserDetailsPassword();

        // 用户类型转换
        if (sessionKey != null) {

            // 解密手机号
            WxMaPhoneNumberInfo wxMaPhoneNumberInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);

            map.put("code", 0);
            map.put("msg", "获取用户手机号成功");
            data.put("wxMaPhoneNumberInfo", wxMaPhoneNumberInfo);

        } else {
            map.put("code", 1);
            map.put("msg", "获取用户手机号失败！");
            data.put("details", "Security 中未找到用户！");
        }

        return map;
    }

    /**
     * 在 Security 获取 微信小程序 会话密钥
     * <p>
     * 在小程序登录时放入的
     */
    private String getUserDetailsPassword() {

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
            return userDetails.getPassword();

        } else {
            return null;
        }

    }

}
