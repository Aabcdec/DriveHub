module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es6: true
  },
  extends: [
    'plugin:vue/vue3-essential'
  ],
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module'
  },
  rules: {
    // 禁用所有TypeScript相关的警告
    '@typescript-eslint/no-unused-vars': 'off',
    '@typescript-eslint/no-explicit-any': 'off',
    '@typescript-eslint/explicit-module-boundary-types': 'off',
    '@typescript-eslint/no-non-null-assertion': 'off',
    '@typescript-eslint/ban-ts-comment': 'off',
    '@typescript-eslint/no-var-requires': 'off',
    '@typescript-eslint/no-empty-function': 'off',
    '@typescript-eslint/no-inferrable-types': 'off',
    
    // 禁用Vue相关的TypeScript警告
    'vue/no-unused-vars': 'off',
    'vue/no-unused-components': 'off',
    'vue/require-default-prop': 'off',
    'vue/require-prop-types': 'off',
    
    // 禁用JavaScript相关警告
    'no-unused-vars': 'off',
    'no-console': 'off',
    'no-debugger': 'off',
    'no-undef': 'off',
    'no-unreachable': 'off',
    'no-empty': 'off',
    'prefer-const': 'off',
    'no-var': 'off'
  },
  
  // 忽略TypeScript文件的检查
  ignorePatterns: [
    '*.ts',
    '*.tsx',
    'node_modules/',
    'dist/',
    'build/'
  ]
}
