//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    motto: 'Hello World',
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  //事件处理函数
  bindViewTap: function() {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  onLoad: function() {
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true
      })
    } else if (this.data.canIUse) {
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
            hasUserInfo: true
          })
        }
      })
    }
  },
  getUserInfo: function(e) {
    console.log(e)

    ////////////////////////////// 获取用户详细信息开始 //////////////////////////////

    var detail = e.detail;
    var userInfo = detail.userInfo;

    // 装载数据
    userInfo.encryptedData = detail.encryptedData;
    userInfo.iv = detail.iv;

    // 将用户详细信息发送到后台进行保存（仅在第一次微信授权时进行，如需测试本连接，请清理 授权数据或全部清理）
    // 方式一：可通过 e.detail.userInfo 直接将用户的详细信息发送到后台进行保存
    // 方式二：使用 appId、secret、encryptedData、iv、sessionKey 进行解密
    wx.request({
      url: 'http://127.0.0.1/userInfo/update.do',
      data: userInfo,
      success: res => {
        console.log(res)
        console.log(res.data)
        console.log(res.data.code)
        console.log(res.data.msg)
        console.log(res.data.data)
      }
    })

    ////////////////////////////// 获取用户详细信息结束 //////////////////////////////

    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
  }
})