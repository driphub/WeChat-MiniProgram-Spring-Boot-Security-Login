//app.js

// 导入 weapp-cookie
// 小程序原生的 request 网络请求接口并不支持传统的 Cookie，但有时候我们现有的后端接口确于依赖 Cookie（比如服务器用户登录态），这个库可用一行代码为你的小程序实现 Cookie 机制，以保证基于 cookie 的服务会话不会失效，与 web 端共用会话机制
// GitHub：https://github.com/charleslo1/weapp-cookie
import 'js/weapp-cookie';

App({
  onLaunch: function() {
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    // 登录
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId

        ////////////////////////////// 登录开始 //////////////////////////////

        var code = res.code;

        // 重要提示：首先建立一次连接，获取 Session Cookie，直接登录不能保持登录。此URL中，仅需要创建一个Session即可。
        // 注意这里的 res 的范围
        wx.request({
          url: 'http://127.0.0.1/cookie/getCookie.do',
          success: res => {

            // 建立连接后登陆
            wx.request({
              url: 'http://127.0.0.1/xcx/login.do',
              data: {
                code: code
              },
              success: res => {

                var code = res.data.code;
                var msg = res.data.msg;
                var data = res.data.data;
                var wxMaUserInfo = data.wxMaUserInfo;

                if (code == 0) {
                  
                  console.log(wxMaUserInfo)

                  wx.showToast({
                    title: msg,
                    icon: 'success',
                    duration: 2000
                  })

                  setTimeout(function() {
                    wx.showToast({
                      title: '仅能获取到OpenID',
                      icon: 'none',
                      duration: 2000
                    })
                  }, 2000);

                }

              }
            })
          }
        })

        ////////////////////////////// 登录结束 //////////////////////////////

      }
    })
    // 获取用户信息
    wx.getSetting({
      success: res => {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            success: res => {
              // 可以将 res 发送给后台解码出 unionId
              this.globalData.userInfo = res.userInfo

              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback(res)
              }
            }
          })
        }
      }
    })
  },
  globalData: {
    userInfo: null
  }
})