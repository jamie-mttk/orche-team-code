<template>

  <el-tooltip effect="light" :disabled="!description" :raw-content="true" :content="description" :show-after="1000"
    placement="bottom">
    <el-button v-model="buttonAttr" :loading="loading" :disabled="isDisabled" :type="getPropSafe('type')"
      @click="handleClick">
      {{ props.config.label }}</el-button>
  </el-tooltip>
</template>
<script setup lang="ts">
import { ref, computed, unref } from 'vue'
import type { BaseProps } from './support'
import { useSupport } from './support'
import request from '@/utils/request'
import { Dialog } from '@/components/ConfigDyna/index'
import { ElMessage } from 'element-plus'

const props = defineProps<BaseProps>()
const { isDisabled, getPropSafe } = useSupport(props)

const buttonAttr = computed(() => {
  const defaultAttr = {
    size: 'large',
    type: '',
    plain: false,
    round: false,
    circle: false,
    icon: ''
  }
  return { ...defaultAttr, ...(props.config.props || {}) }
})
//
const loading = ref(false)
const result = ref([])
//computed
const description = computed(() => {
  return props.config.description
})
const itemOptions = computed(() => {
  let options = ''
  result.value.forEach((item) => {
    if (options) {
      options = options + ','
    }
    options = options + item.label
  })
  return options
})
const itemChooseConfig = computed(() => {
  return {
    hideExpertMode: true,
    label: '请选择',
    props: {
      width: '30%',
      labelWidth: '32px'
    },
    children: [
      {
        key: 'item',
        mode: 'select',
        mandatory: true,
        props: {
          options: itemOptions.value
        }
      }
    ]
  }
})
//methods
function handleClick() {
  let url = getPropSafe('url')
  if (!url) {
    ElMessage.error('No url is set')
    return
  }
  let requestBody = { config: props.config, data: props.data }
  request({
    method: 'POST',
    url: url,
    data: requestBody
    // baseURL: vm.getBaseURL()
  }).then(function (resp) {
    //

    //console.log("data\n" + JSON.stringify(resp.data));
    //vm.data= { ...vm.data, ...resp.data };
    let r = resp.data._result
    if (r && Array.isArray(r)) {
      //
      if (r.length == 1) {
        //只有一条不显示对话框，直接设置
        //注意这里得到data,去掉了label部分
        applyData(r[0].data)
      } else {
        //
        result.value = r
        //显示对话框让用户选择，选中第一个
        const data = { item: result.value[0].label }

        //
        Dialog(data, itemChooseConfig.value, handleDataChoosed)
      }
    } else {
      //没有result,认为是直接返回数据
      applyData(resp.data)
    }
  })
}
//Copy data in new Data into props.data
function applyData(newData: any) {
  if (!newData) {
    return
  }
  if (newData?.__error) {
    ElMessage.error(newData?.__error)
    return
  }
  if (newData?.__warning) {
    ElMessage.warning(newData?.__warning)

  }
  for (let key in newData) {
    if (key.startsWith('__')) {
      continue
    }
    //vm.data[key]=newData[key]
    unref(props.data)[key] = newData[key]
  }
  //
  ElMessage.success('调用成功')
}

function handleDataChoosed(data) {
  const found = result.value.find((i) => i.label == data.item)
  //
  if (found && found.data) {
    applyData(found.data)
  }
}
</script>
