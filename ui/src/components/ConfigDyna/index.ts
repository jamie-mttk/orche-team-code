import { ref, isRef, isReactive } from 'vue'
import { app } from '@/main'
import CdDialog from './Dialog.vue'
import CdDrawer from './Drawer.vue'
import { dynamicRender } from 'mttk-vue-wrap'
export function Dialog(data, config, dataUpdate?) {
  //
  return Popup('dialog', data, config, dataUpdate)
}
export function Drawer(data, config, dataUpdate?) {
  //
  return Popup('drawer', data, config, dataUpdate)
}
//mode: dialog/drawer
export function Popup(mode, data, config, dataUpdate?) {
  //
  let comp = undefined
  if ('dialog' == mode) {
    comp = CdDialog
  } else if ('drawer' == mode) {
    comp = CdDrawer
  } else {
    //invalid mode
    throw new Error('Invalid popup mode:' + mode)
  }
  //
  const show = ref(true)
  //
  if (!isRef(data) && !isReactive(data)) {
    data = ref(data)
  }

  //
  const configWrap = {
    '~': comp,
    '~modelValue': show,
    config,
    data,
    callback: dataUpdate
  }

  return dynamicRender(configWrap, app._context, { removeEvent: 'closed' })
}
