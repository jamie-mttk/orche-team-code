import { unref, toRaw, computed } from 'vue'
import MonacoEditor from 'monaco-editor-vue3'

// 使用 Vite 的 ?worker 语法导入工作线程
import JsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker'
import CssWorker from 'monaco-editor/esm/vs/language/css/css.worker?worker'
import HtmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker'
import TsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker'
import EditorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker'

// 配置 MonacoEnvironment 以使用正确的 Worker 实例
self.MonacoEnvironment = {
  getWorker: function (workerId, label) {
    switch (label) {
      case 'json':
        return new JsonWorker()
      case 'css':
      case 'scss':
      case 'less':
        return new CssWorker()
      case 'html':
      case 'handlebars':
      case 'razor':
        return new HtmlWorker()
      case 'typescript':
      case 'javascript':
        return new TsWorker()
      default:
        return new EditorWorker()
    }
  }
}
const MONACO_DEFAULT_OPTIONS = {
  colorDecorators: true,
  lineHeight: 24,
  tabSize: 2,
  automaticLayout: true,
  folding: false, //否则左边会出现一些空白
  lineDecorationsWidth: 0, // 设置行装饰的宽度为0
  renderLineHighlight: 'none', // 避免行高亮区域的影响
  overviewRulerLanes: 0 //隐藏右边滚动条上的标尺
}
//config
//value model value
function buildMonacoEditor(value: any, config: any) {
  config = toRaw(unref(config))
  const vueWrapConfig: any = {
    '~': MonacoEditor,
    theme: config.theme || 'vs',
    language: config.language || 'json',
    options: {
      ...MONACO_DEFAULT_OPTIONS,
      ...(unref(config.options) || {})
    }
  }
  //增加modelValue and component
  if (value) {
    vueWrapConfig['~modelValue'] = value
    vueWrapConfig['~modelValueName'] = 'value'
  }
  //其他可能的配置
  for (const k of Object.keys(config)) {
    if (k == 'theme' || k == 'language' || k == 'options') {
      continue
    }
    vueWrapConfig[k] = config[k]
  }

  //
  return vueWrapConfig
}

function buildMonacoEditorComp(value: any, config: any) {
  return computed(() => buildMonacoEditor(value, config))
}

export { MonacoEditor, MONACO_DEFAULT_OPTIONS, buildMonacoEditor, buildMonacoEditorComp }
