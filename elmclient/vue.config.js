module.exports = {
  devServer: {
    port: 8080,
    historyApiFallback: true,
    proxy: {
      '/': {
        target: 'http://localhost:14000', // 网关地址
        changeOrigin: true,
        ws: false, // 禁用WebSocket代理
        pathRewrite: {
          '^/': '/'  // 保持路径不变
        },
        logLevel: 'debug',  // 添加调试日志
        bypass: function(req, res, proxyOptions) {
          // 如果请求的是前端路由，不进行代理
          if (req.headers.accept && req.headers.accept.includes('text/html')) {
            return '/index.html';
          }
        }
      }
    }
  }
}
 