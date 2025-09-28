import { createI18n } from 'vue-i18n'
import { useStorage } from '@vueuse/core'
import { provideGlobalConfig } from 'element-plus'
import localeChooser from './localeChooser.vue'
import zhCN from './locales/zh-CN'
import enUS from './locales/en-US'
import zhCN_element from 'element-plus/es/locale/lang/zh-cn'
import enUS_element from 'element-plus/es/locale/lang/en'
import { ElMessage } from 'element-plus'

//Store choosed locale to local storage
let defaultLang = navigator.language || 'zhCN'

if (defaultLang) {
  //修改zh-CN到zhCN
  defaultLang = defaultLang.replaceAll('-', '')
}
const localCurrent = useStorage('cloudhub-lang', defaultLang)

//Init i18n instance
const i18n = createI18n({
  legacy: false,
  locale: localCurrent.value,
  fallbackLocale: 'zhCN',
  missingWarn: false,
  fallbackWarn: false,
  messages: {
    zhCN: zhCN,
    enUS: enUS
  }
})

function installI18n(app) {
  app.use(i18n)
  //Set current local while system startup
  changeLocale(localCurrent.value, app, true)
}
//ALl the locale supported
const localeList = [
  { key: 'enUS', name: 'English' },
  { key: 'zhCN', name: '简体中文' }
]

function changeLocale(localeNew, app, suppressNotify = false) {
  //console.log('Change locale', localCurrent.value, localeNew)
  //Save
  localCurrent.value = localeNew
  //Change i18n
  i18n.global.locale.value = localeNew
  //Change Element Plus, need to refresh to take affect
  const elementLocale = parseElementLocale()

  provideGlobalConfig({ locale: elementLocale }, app, true)

  //notify
  if (!suppressNotify) {
    ElMessage({
      message: i18n.global.t('_.lang.notify', [localeNew]),
      type: 'success'
    })
  }
}

//Parse element plus locale ,default is english
function parseElementLocale() {
  if (localCurrent.value == 'zhCN') {
    return zhCN_element
  } else if (localCurrent.value == 'enUS') {
    return enUS_element
  } else {
    return enUS_element
  }
}
//short cut of i18n.global.t
const t = i18n.global.t
//
export { i18n, installI18n, localeList, changeLocale, localeChooser, localCurrent, t }
