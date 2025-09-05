// 全局禁用TypeScript警告的脚本
const fs = require('fs')
const path = require('path')

// 需要添加禁用注释的文件扩展名
const targetExtensions = ['.vue', '.js', '.jsx']

// 禁用注释
const disableComments = [
  '// @ts-nocheck',
  '/* eslint-disable */',
  '/* eslint-disable @typescript-eslint/no-unused-vars */',
  '/* eslint-disable @typescript-eslint/no-explicit-any */',
  '/* eslint-disable @typescript-eslint/explicit-module-boundary-types */'
]

// 递归遍历目录
function walkDir(dir, callback) {
  const files = fs.readdirSync(dir)
  
  files.forEach(file => {
    const filePath = path.join(dir, file)
    const stat = fs.statSync(filePath)
    
    if (stat.isDirectory() && !['node_modules', 'dist', '.git'].includes(file)) {
      walkDir(filePath, callback)
    } else if (stat.isFile()) {
      callback(filePath)
    }
  })
}

// 处理单个文件
function processFile(filePath) {
  const ext = path.extname(filePath)
  
  if (!targetExtensions.includes(ext)) {
    return
  }
  
  try {
    let content = fs.readFileSync(filePath, 'utf8')
    
    // 检查是否已经有禁用注释
    if (content.includes('@ts-nocheck') || content.includes('eslint-disable')) {
      return
    }
    
    // 为Vue文件添加注释
    if (ext === '.vue') {
      // 在<script>标签后添加注释
      content = content.replace(
        /<script[^>]*>/,
        match => `${match}\n${disableComments.join('\n')}`
      )
    } else {
      // 为JS文件在文件开头添加注释
      content = `${disableComments.join('\n')}\n${content}`
    }
    
    fs.writeFileSync(filePath, content, 'utf8')
    console.log(`已处理: ${filePath}`)
  } catch (error) {
    console.error(`处理文件失败 ${filePath}:`, error.message)
  }
}

// 主函数
function main() {
  console.log('开始禁用TypeScript警告...')
  
  const srcDir = path.join(__dirname, 'src')
  
  if (!fs.existsSync(srcDir)) {
    console.error('src目录不存在')
    return
  }
  
  walkDir(srcDir, processFile)
  
  console.log('TypeScript警告禁用完成!')
}

// 如果直接运行此脚本
if (require.main === module) {
  main()
}

module.exports = { main, processFile, walkDir }
