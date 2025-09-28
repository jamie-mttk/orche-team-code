<template>
  <CdBase :config="config" :data="data">
    <el-select :clearable="clearable" :multiple="multiple" :multiple-limit="multipleLimit" :filterable="filterable"
      :remote="supportRemoteQuery" :remote-method="remoteQuery" :disabled="isDisabled" :no-data-text="noDataText"
      :placeholder="placeholder" :allow-create="allowCreate" v-model="modelValue" :loading="loading"
      autocomplete="new-password" @change="applyChange" @clear="emit('clear')"
      style="padding-right: 26px; display: inline-block; position: relative"
      v-bind:style="{ paddingRight: paddingWidth + 'px' }">
      <el-option v-for="o in selOptions" :key="o[valueKey]" :label="o[labelKey]" :value="o[valueKey]"></el-option>
      <div style="margin-left: 16px">
        <el-pagination small v-if="supportPagination" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" v-model:current-page="page" :page-size="size"
          layout="sizes,total, prev, pager, next" :total="total">
        </el-pagination>
      </div>
    </el-select>
    <Icon v-if="showButton" name="mdiDotsHorizontal" @click="showTransfer"
      style="margin: 4px 0px; position: absolute; top: 0px; right: 0px; width: 20px; height: 20px"></Icon>
    <el-drawer size="70%" :append-to-body="true" :show-close="true" direction="rtl" :title="props.config.label"
      v-model="transferVisible" :with-header="true" v-if="multiple">
      <el-transfer style="padding: 0px 5%" v-model="modelValue" :data="selOptions" :filterable="filterable"
        :titles="titles" :props="{
          key: valueKey,
          label: labelKey
        }"></el-transfer>
    </el-drawer>
  </CdBase>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, unref, watch } from 'vue'

import debounce from 'lodash/debounce'
import type { BaseProps } from './support'
import { useSupport } from './support'
import CdBase from './Base.vue'
import request from '@/utils/request'
import Icon from '@/components/mdiIicon/index.vue'
const props = defineProps<BaseProps>()
const { modelValue, placeholder, isDisabled, getPropSafe } = useSupport(props)

const emit = defineEmits(['change', 'clear'])
//******************************************************* */
//* data
//******************************************************* */
const selOptions = ref([])
const transferVisible = ref(false) //是否打开了transfer
const noDataText = ref('') //获取出错时会显示出错信息
const loading = ref(false) //是否正在加载标记
const matcher = ref('') //用于远程搜索的关键字
//
const page = ref(1) //当前分页
const size = ref(10) //分页条数
const total = ref(0) //总条数

//******************************************************* */
//* computed
//******************************************************* */
//transfer titles
const titles = computed(() => {
  return ['未选择', '已选择']
})
const allowCreate = computed(() => {
  let result = getPropSafe('allowCreate')
  if (result == undefined) {
    //没有设置认不允许
    return false
  }
  return !!result
})
//是否是多选下拉框，缺省不是
const multiple = computed(() => {
  let multiple = getPropSafe('multiple')
  if (multiple == undefined) {
    //没有设置认为是单选
    return false
  }
  return multiple ? true : false
})
//是否显示transfer按钮
const showButton = computed(() => multiple.value)
//如果显示transfer按钮则选择框部分需要流出合适的位置
const paddingWidth = computed(() => (showButton.value ? 26 : 0))
//是否允许过滤,缺省允许
const filterable = computed(() => {
  let filterable = getPropSafe('filterable')
  if (filterable == undefined) {
    //没有设置认为是允许过滤的
    return true
  }
  return filterable
})
//是否可清除,缺省允许
const clearable = computed(() => {
  let clearable = getPropSafe('clearable')
  if (clearable == undefined) {
    //没有设置认为是允许过滤的
    return true
  }
  return clearable
})
//最多选择的最多项数
const multipleLimit = computed(() => {
  let multipleLimit = getPropSafe('multipleLimit')
  if (multipleLimit == undefined) {
    //没有设置认为是无限
    return 0
  }
  return parseInt(multipleLimit)
})

//选择框的label在json中对应的key,缺省为name
const labelKey = computed(() => {
  let labelKey = getPropSafe('labelKey')
  if (labelKey) {
    return labelKey
  } else {
    return 'name'
  }
})
//选择框的值在json中对应的key,缺省为_id
const valueKey = computed(() => {
  let valueKey = getPropSafe('valueKey')
  if (valueKey) {
    return valueKey
  } else {
    return '_id'
  }
})
//是否加_NAME的值到数据中,缺省是true
const appendSelectLabel = computed(() => {
  let appendSelectLabel = getPropSafe('appendSelectLabel')
  if (appendSelectLabel == undefined) {
    //没有设置认为是 true
    return true
  }
  //
  //return 'false'==appendSelectLabel ? false : true;
  return appendSelectLabel ? true : false
})

//是否支持分页
const supportPagination = computed(() => {
  //必须满足以下条件
  //1. 设置了url属性或 queryPromise
  //2. 设置了pageSize属性且大于0
  //3. 不是multiple
  if (!getPropSafe('url') && !getPropSafe('queryPromise')) {
    return false
  }
  if (!getPropSafe('pageSize') || getPropSafe('pageSize') <= 0) {
    return false
  }
  if (getPropSafe('multiple') != undefined) {
    if (getPropSafe('multiple') == true) {
      return false
    }
  }
  return true
})

const supportRemoteQuery = computed(() => {
  return supportPagination
})
//是否支持刷新后自动选中第一条数据
//缺省是不选中
const autoSelectFirst = computed(() => {
  let autoSelectFirst = getPropSafe('autoSelectFirst')
  if (autoSelectFirst) {
    return true
  } else {
    return false
  }
})
//******************************************************* */
//* methods
//******************************************************* */

//构建URL或queryPromise,返回一个promise
function buildQueryPromise(pp: any) {
  const url = getPropSafe('url')
  if (url) {
    return request.get(url, { params: pp })
  }
  //query promise
  return getPropSafe('queryPromise')(pp)
}

//从远程URL获取列表
function refreshOptions() {
  //否则看看是否有url

  if (!getPropSafe('url') && !getPropSafe('queryPromise')) {
    //没有配置远程获取
    return []
  }
  //
  //检查是否有参数
  //不同参数由,分开,每个参数可以由:分为两部分(也可以没有)
  //参数第一部分给出了在data中的key,第二部分(没有和第一部分相同)给出了url查询时使用的参数名
  let pp = {} as any
  let params = getPropSafe('params')
  if (params) {
    //考虑有多个参数的场景
    let paramsList = params.split(',')
    paramsList.forEach((v) => {
      let vs = v.split(':')
      if (vs.length > 1) {
        pp[vs[1]] = props.data[vs[0]]
      } else {
        pp[v] = props.data[v]
      }
    })
  }
  if (supportPagination.value) {
    pp['page'] = page.value
    pp['size'] = size.value
    pp['total'] = total.value
    pp['_name'] = matcher.value ? '*' + matcher.value + '*' : ''
  }

  //从服务器获取,如果出错设置noDataText
  //
  loading.value = true
  //
  buildQueryPromise(pp).then(function (response) {
    selOptions.value = response.data.list
    noDataText.value = ''
    //
    //总页数
    let t = response.data.total
    if (!t) {
      //可能是不支持分页
      t = selOptions.value.length
    }
    total.value = t
    //当前页数
    let p = response.data.page
    if (!p) {
      //可能是不支持分页
      p = 1
    }
    page.value = p
    //
    let v = modelValue.value
    if (!v && autoSelectFirst.value && selOptions.value.length > 0) {
      let first = selOptions.value[0]
      v = first[valueKey.value]
    }
    //
    modelValue.value = v
    applyChange(v)
    //
    loading.value = false
  })
}
//设置_NAME为key的选择值
function applyChange(v: any) {
  emit('change', v)
  if (multiple.value) {
    return
  }
  if (!appendSelectLabel.value) {
    return
  }
  if (!modelValue.value) {
    unref(props.data)[props.config.key + '_NAME'] = ''
    return
  }
  const obj = selOptions.value.find((item) => {
    //这里的userList就是上面遍历的数据源
    return item[valueKey.value] === modelValue.value //筛选出匹配数据
  })
  if (obj && obj.name) {
    unref(props.data)[props.config.key + '_NAME'] = obj.name
  }
}
//显示Transfer
function showTransfer() {
  if (isDisabled.value) {
    return
  }
  if (!multiple.value) {
    //只有multiple支持打开transfer
    return
  }
  //
  transferVisible.value = true
}
function handleSizeChange(_size: number) {
  size.value = _size
  //
  refreshOptions()
}
function handleCurrentChange(_page: number) {
  page.value = _page
  //
  refreshOptions()
}
//第一次获取起始是重复的，可以通过此方法避免/由于性能影响不大所以暂时不管
// let firstQuery = true
function remoteQuery(query: any) {
  // if (firstQuery) {
  //   firstQuery = false
  //   return
  // }
  matcher.value = query
  total.value = 0

  //
  debounce(refreshOptions, 300)()
}
//******************************************************* */
//* onMounted
//******************************************************* */
onMounted(() => {
  //************************************************************
  //1. 考虑是否有参数 - 参数可以在其他变量被修改时自动刷新本控件列表
  //************************************************************
  //   let params = getPropSafe('params')
  //   if (params) {
  //     //考虑有多个参数的场景
  //     let paramsList = params.split(',')
  //     paramsList.forEach((v) => {
  //       let vs = v.split(':')
  //       watch('data.' + vs[0], function () {
  //         //检测到变化自动刷新列表
  //         refreshOptions()
  //         //
  //         modelValue.value = ''
  //       })
  //     })
  //   }
  let params = getPropSafe('params')
  if (params) {
    const paramsList: string[] = params.split(',')

    paramsList.forEach((v) => {
      if (!v) {
        return
      }
      const kv: string[] = v.split(':')
      const keyReal = kv[0]
      // console.log('add watch', props.config.key, keyReal)
      watch(
        () => unref(props.data)[keyReal],
        () => {
          // console.log('handle watch', props.config.key, keyReal)
          //检测到变化自动刷新列表
          refreshOptions()
          //
          modelValue.value = ''
        }
      )
    })
  }
  //************************************************************
  //2. 从props的options里去找
  //************************************************************
  //
  let options = unref(getPropSafe('options'))
  //console.log('@@@@@@@@@'+JSON.stringify(options))
  if (options) {
    if (Array.isArray(options)) {
      //如果是object则认为时JSON直接使用
      selOptions.value = options
    } else if (typeof options == 'string' && options.length > 0) {
      //认为是通过字符串给出的列表，如
      //val1,val2,val3
      //val1:label1,val2:label2,val3:label3
      selOptions.value = []
      //
      options.split(',').map(function (val) {
        let ss = val.split(':')
        selOptions.value.push({
          name: ss.length > 1 ? ss[1].trim() : ss[0].trim(),
          _id: ss[0].trim()
        })
      })
    } else if (typeof options == 'object') {
      //Object认为
      selOptions.value = []
      for (const k of Object.keys(options)) {
        selOptions.value.push({
          name: options[k],
          _id: k
        })
      }
    }
  }
  //************************************************************
  //3. 认为是从url获取
  //************************************************************
  //
  refreshOptions()
  //************************************************************
  //4. 有可能有原始值是没有_NAME的需要设置
  //由于从服务器load是异步的,所有refreshOptions后又设置了一次
  //refresh后已经设置这里不需要
  //************************************************************
  //applyChange()
})

//Expose
defineExpose({ refreshOptions })
</script>

<style>
/*当前无法把穿梭框设置为高度100% */
.el-transfer-panel {
  width: 40%;
}

.el-transfer-panel__body {
  height: 460px;
}

.el-transfer-panel__list.is-filterable {
  height: 400px;
  padding-top: 0;
}
</style>
