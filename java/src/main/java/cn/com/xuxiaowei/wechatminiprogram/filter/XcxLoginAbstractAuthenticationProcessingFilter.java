package cn.com.xuxiaowei.wechatminiprogram.filter;

import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import cn.com.xuxiaowei.wechatminiprogram.propertie.WxMaProperties;
import cn.com.xuxiaowei.wechatminiprogram.util.response.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序 登录 认证处理过滤器
 * <p>
 * 1、小程序登录：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html
 * 2、UnionID 机制说明：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/union-id.html
 * 3、授权：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/authorize.html
 * 4、服务端获取开放数据：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html
 * 5、获取手机号：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/getPhoneNumber.html
 *
 * @author xuxiaowei
 */
@Slf4j
public class XcxLoginAbstractAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private WxMaProperties wxMaProperties;

    /**
     * 基于内存的微信配置provider，在实际生产环境中应该将这些配置持久化
     */
    private static WxMaInMemoryConfig wxMaInMemoryConfig = new WxMaInMemoryConfig();
    private static final WxMaServiceImpl wxMaService = new WxMaServiceImpl();

    public XcxLoginAbstractAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        // 响应数据
        Map<String, Object> map = new HashMap<>(4);
        Map<String, Object> data = new HashMap<>(4);
        map.put("data", data);

        String code = request.getParameter("code");

        if (StringUtils.isEmpty(code)) {
            map.put("code", 1);
            map.put("msg", "微信小程序登录受到攻击！");
            data.put("details", "未收到 微信小程序登录 的 code！");
            log.debug(map.toString());
            ResponseUtils.response(response, map);
            return null;
        }

        try {

            wxMaInMemoryConfig.setAppid(wxMaProperties.getAppid());
            wxMaInMemoryConfig.setSecret(wxMaProperties.getSecret());
            wxMaService.setWxMaConfig(wxMaInMemoryConfig);

            // 微信小程序 登录 URL（使用 code 获取 OpenID、UnionID）
            // 登录凭证校验。通过 wx.login（https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html） 接口获得临时登录凭证 code 后，
            // 传到开发者服务器调用此接口完成登录流程。更多使用方法详见 小程序登录（https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html）。
            // 官方文档：https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
            // 请求接口：https://api.weixin.qq.com/sns/jscode2session
            // 接口示例：https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
            WxMaJscode2SessionResult wxMaJscode2SessionResult = wxMaService.jsCode2SessionInfo(code);


            log.debug("");
            log.debug("用户登录信息：" + wxMaJscode2SessionResult);
            log.debug("");


            // 普通用户的标识，对当前开发者帐号唯一
            String openid = wxMaJscode2SessionResult.getOpenid();
            // 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
            String unionid = wxMaJscode2SessionResult.getUnionid();
            String sessionKey = wxMaJscode2SessionResult.getSessionKey();

            //////////////////  ////////////////////

            HttpSession session = request.getSession();

            // 根据 微信 OpenID，查看用户是否登陆了
            // 开发、生产为数据库中的数据，这里仅为了方便演示
            Object wxMaUserInfoObj = session.getAttribute(openid);

            // 未登录
            if (!(wxMaUserInfoObj instanceof WxMaUserInfo)) {

                // 用户存放用户数据
                WxMaUserInfo wxMaUserInfo = new WxMaUserInfo();

                wxMaUserInfo.setOpenId(openid);
                wxMaUserInfo.setUnionId(unionid);

                session.setAttribute("openid", openid);

                // 将用户信息放入 Session
                // 登录时，只能获取到 OpenID、会话密钥 sessionKey 以及 UnionID（只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。）
                // 后面将设置用户昵称等信息
                session.setAttribute(openid, wxMaUserInfo);
            }

            //////////////////  ////////////////////

            List<GrantedAuthority> authorities = new ArrayList<>();

            // 给一个权限
            // 此时包含：ROLE_
            authorities.add(new SimpleGrantedAuthority("ROLE_WECHATXCX"));

            // 注意此时的类型 org.springframework.security.core.userdetails.User
            org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(openid, sessionKey, authorities);

            // IP、Session
            WebAuthenticationDetails details = null;
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            Object detailsObj = authentication.getDetails();
            if (detailsObj instanceof WebAuthenticationDetails) {
                details = (WebAuthenticationDetails) detailsObj;
            }

            // principal    用户 org.springframework.security.core.userdetails.User
            // credentials  自定义信息、如：WebAuthenticationDetails
            // authorities  权限
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user, details, authorities);


            //////////////////  ////////////////////

            // 仅使用一次
            session.setAttribute("loginSuccess", true);

            // 返回验证结果
            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        return null;
    }

}
