
const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  // 禁用TypeScript检查
  transpileDependencies: true,

  // 禁用ESLint
  lintOnSave: false,

  // 配置webpack
  configureWebpack: {
    resolve: {
      extensions: ['.js', '.vue', '.json']
    }
  },

  // 链式配置webpack
  chainWebpack: config => {
    // 禁用TypeScript检查
    config.plugins.delete('fork-ts-checker')

    // 禁用类型检查
    config.module
      .rule('ts')
      .test(/\.ts$/)
      .use('ts-loader')
      .loader('ts-loader')
      .options({
        transpileOnly: true,
        compilerOptions: {
          noEmit: true,
          skipLibCheck: true
        }
      })
  },

  devServer: {
    proxy: {
      // 代理所有以 /api 开头的请求
      '/api': {
        target: 'http://localhost:8080', // 你需要跨域的地址
        changeOrigin: true,
        pathRewrite: {
          '^/api': '' // 把 URL 中的 /api 转为 去掉
        }
      }
    }
  }
})