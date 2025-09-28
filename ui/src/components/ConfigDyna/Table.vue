<template>
  <CdBase :config="tableBaseConfig" :data="props.data">
    <el-table ref="rawTableRef" :id="rawTable" :data="tableData" border size="small" v-bind="$attrs"
      :row-key="config.rowKey" style="margin-bottom: 8px" @selection-change="handleSelectionChange">
      <el-table-column v-if="selectionMode == 'multiple'" type="selection" width="32"></el-table-column>
      <el-table-column v-if="showExpandColumn" type="expand">
        <template v-slot="sp">
          <slot name="expandColumn" v-bind:data="sp"></slot>
        </template>
      </el-table-column>
      <el-table-column v-for="child in colsConfig" :width="child._tableWidth" :key="child.key" :label="child.label"
        :column-key="child.key" :type="columnType(child)" :sortable="columnSortable(child)" :prop="child.key">
        <template v-slot="sp" v-if="!columnTypeHasContent(child)">
          <slot name="column" v-bind:data="sp" v-bind:value="calItemValue(child, sp)" v-bind:config="child"
            v-bind:tableData="tableData">
            <MttkWrapComp v-if="isVueWrap(child)" :config="child.props.tableRender(sp, child) || ''"></MttkWrapComp>
            <span v-else v-html="calItemValue(child, sp)"></span>
          </slot>
        </template>
      </el-table-column>
      <el-table-column v-if="operateColWidth > 0" label="操作" :width="operateColWidth">
        <template v-slot="sp">
          <CdButtons :config="buttonsConfig(sp.row, sp.$index)" v-show="!isAutoHideButtons || sp.row == currentMouseRow"
            @click="
              (command) => {
                handleButtonsClick(command, sp.row, sp.$index)
              }
            "></CdButtons>
        </template>
      </el-table-column>
      <template v-slot:empty>
        <slot name="empty">
          <div style="display: inline-block; margin-right: 32px">没有数据</div>

          <CdButtons :config="emptyButtonsConfig" @click="
            (command) => {
              handleButtonsClick(command)
            }
          "></CdButtons>
        </slot>
      </template>
    </el-table>
  </CdBase>
</template>
<script setup lang="ts">
import { ref, computed, unref } from 'vue'
import type { BaseProps } from './support'
import { useSupport } from './support'
import { deepCopy, formatDateTime } from '@/utils/tools'
import { Popup } from '@/components/ConfigDyna/index'
import { ElMessage, ElMessageBox } from 'element-plus'
import CdButtons from './Buttons.vue'

import CdBase from './Base.vue'

import * as XLSX from 'xlsx'
import FileSaver from 'file-saver'
//config配置
//特别注意:config指在config/props下设置
//operateColWidth(操作列宽度) - 如果没有提供系统自动计算
//showTitle: 是否显示标题,缺省显示(在设置了表格label的情况下)
//cols:列列表 - 用逗号分隔,用于给出那些字段显示在表格中,格式为col1,col2:width,col3
//insertAt:新增数据后的数据插入到表格的位置,缺省为底部(bottom),可选值为top/bottom (tbd:应该还可以是refresh) -暂时没使用
//notifyChange(true/false)   如果设置为true,则在数据增删改后调用事件并根据事件处理中是否回调done方法决定是否修改数据
//selectionMode            缺省为none
//                          none或其他值    无特殊设置
//                          single[不支持]    单选,支持抛出选择事件.缺省就是单选，设置了highlight-current-row只是能够显示选择的行
//                          multiple        多选,支持抛出选择事件
//buttonsConfig       缺省值为{'max':3,list:['_add','_edit','_delete','_copy']}
//            max代表最多出现多少个按钮,注意不包括本身就是dropdown的按钮,缺省值为4
//            list每个元素可以是标准的按钮key,也可以是按钮配置
//          每个按钮配置包括key(按钮键),label(按钮显示标题),type(element ui按钮类型,如primary,success),show,disabled,current
//          标准按钮(key以_开头的)系统自动处理，非标准按钮会发出事件
//          disabled/show是表达式，支持参数 tableData(表格数据),row(当前数据行数据),index(当前数据行行号)
//          current为true(缺省为true)说明处理的是当前行,否则认为是-1;用于自动设置回调函数的行数
//emptyButtonsConfig  - 格式定义参考buttonsConfig，缺省值为{list:['_add']}

//
const props = defineProps<BaseProps>()
const emit = defineEmits<{
  (e: 'operate', command: number, row: number, index: number, commandCallback: Function): void
  (e: 'dataDelete', row: object, callback: Function): void
  (e: 'dataUpdate', row: object, callback: Function): void
  (e: 'dataInsert', row: object, callback: Function): void
  (e: 'dataMoveUp', data: object, index: number): void
  (e: 'dataMoveDown', data: object, index: number): void
  (e: 'selectionChange', rows: any): void
}>()

const { modelValue, getPropSafe, getPropWithDefault, isShow } = useSupport(props)
//data
const editIndex = ref(-1)
//computed
const rawTable = computed(() => 'rawTable_' + Date.now())
const popupMode = computed(() => getPropWithDefault('popupMode', 'dialog'))
const selectionMode = computed(() => getPropWithDefault('selectionMode', 'none'))
const showExpandColumn = computed(() => getPropWithDefault('showExpandColumn', false))
import { MttkWrapComp } from 'mttk-vue-wrap'
const tableBaseConfig = computed(() => {
  //label-position固定在上面
  const result = { ...(props.config || {}) }
  if (!result.labelPosition) {
    //如果没有给出labelPostion则使用top，这样对于表格显示最合理
    result.labelPosition = 'top'
  }
  return result
})

//是否显示标题
// const isShowTitle = computed(() => {
//   let showTitle = getPropSafe('showTitle')
//   if (showTitle == undefined) {
//     //没有提供返回true
//     return true
//   } else if (typeof showTitle == 'string') {
//     return 'true' == showTitle
//   } else if (typeof showTitle == 'boolean') {
//     return showTitle ? true : false
//   } else {
//     return true
//   }
// })
//按钮配置
const buttonsConfig = computed(() => {
  return function (row?, index?) {
    let bc = deepCopy(getPropWithDefault('buttonsConfig', {}))
    //
    if (bc.max == undefined) {
      bc.max = 4
    }
    if (!bc.list) {
      bc.list = ['_add', '_edit', '_delete', '_copy']
    }
    //
    bc = normalizeButtons(bc, row, index)

    //
    return bc
  }
})
//获取add按钮
const extractAddFromButtonConfig = computed(() => {
  let bc = getPropSafe('buttonsConfig')
  if (!bc) {
    //没有提供时缺省_add
    return '_add'
  }
  if (!bc.list) {
    return undefined
  }
  let result = bc.list.find(
    (item) => item == 'add' || item == '_add' || item.key == '_add' || item.key == 'add'
  )

  if (result) {
    return result
  } else {
    return undefined
  }
})
//显示没有数据时的按钮配置,没设置时自动从buttonsConfig中获取add
const emptyButtonsConfig = computed(() => {
  let bc = deepCopy(getPropWithDefault('emptyButtonsConfig', {}))

  if (bc.max == undefined) {
    bc.max = 4
  }
  if (!bc.list) {
    bc.list = []
    let add = extractAddFromButtonConfig.value
    if (add) {
      bc.list.push(add)
    }
  }
  //
  bc = normalizeButtons(bc)
  //
  return bc
})

//得到表格列的类型 child.config && child.config.type)
const columnType = computed(() => {
  return function (child: any) {
    return child?.props?.type || ''
  }
})
//得到列是否允许排序
const columnSortable = computed(() => {
  return function (child: any) {
    if (!child.props || child.props.sortable == undefined) {
      if ('_insertTime' == child.key || '_updateTime' == child.key) {
        return false
      }
      //没有设置默认认为可以排序,这是和以前一直的
      return true
    }
    return child.props.sortable
  }
})
//column type是否设置内容
const columnTypeHasContent = computed(() => {
  return function (child: any) {
    let ct = columnType.value(child)
    return 'selection' == ct || 'index' == ct
  }
})
//操作列宽度 - 注意如果有隐藏按钮则计算是错误的
const operateColWidth = computed(() => {
  if (getPropSafe('operateColWidth') != undefined) {
    return getPropSafe('operateColWidth')
  }

  //普通按钮数量
  let cntNormal = 0
  //下拉按钮数量
  let cntDropdown = 0
  //
  let bc = buttonsConfig.value()
  //如果没有按钮
  if (bc.list.length == 0) {
    return 0
  }
  //
  bc.list.forEach((item) => {
    if (item.children && item.children.length > 0) {
      cntDropdown++
    } else {
      cntNormal++
    }
  })

  if (cntNormal > bc.max) {
    //需要有更多
    cntDropdown++
    cntNormal = bc.max
  }
  //50 - 普通按钮宽度(按两个字计算)
  //64 - 下拉按钮宽度(按两个字计算)
  //18 - 余量
  const width = 50 * cntNormal + 64 * cntDropdown + 18
  // console.log('##!!!', cntNormal, cntDropdown, width)
  return width
})

//表格数据
const tableData = computed(() => {
  return modelValue.value
})
//得到表格配置,参考cols列
const colsConfig = computed(() => {
  let cols = getPropSafe('cols')
  if (!cols) {
    //没有提供cols则认为所有列都显示
    return props.config.children
  }
  //
  let colArray = cols.split(',')
  //
  let retArray = [] as any[]
  //
  findMatchCols(colArray, props.config.children, retArray)
  //
  return retArray.sort((col1, col2) => {
    return colArray.indexOf(col1._tableKey) - colArray.indexOf(col2._tableKey)
  })
})
//methods

//从children(表格配置)里找到所有colArray给出的key对应的具体配置到children
function findMatchCols(colArray, children, retArray) {
  if (!children) {
    return
  }
  for (let item of children) {
    // if (colArray.indexOf(item.key) > -1) {
    //   retArray.push(item);
    //   continue
    // }
    let found = false
    for (let cc of colArray) {
      let ccArray = cc.split(':')
      if (item.key == ccArray[0]) {
        if (ccArray.length > 1) {
          //给出了长度
          item['_tableWidth'] = ccArray[1]
        }
        //
        item['_tableKey'] = cc
        retArray.push(item)
        found = true
        //
        break
      }
    }
    //没找到时试图去children里找 - 如果是table则不获取
    if (!found && item.mode != 'table') {
      findMatchCols(colArray, item.children, retArray)
    }
  }
}

//把用string的替换成适当的按钮配置
//处理可能的disable/show配置
function normalizeButtons(bc, row?, index?) {
  //eval时可能需要 -所以需要
  //   let tableData = tableData.value
  //除了eval时,tableData和index没有其他地方使用
  //下面代码没有实际意义，是为了避免编译时出现  is assigned a value but never used (no-unused-vars) 的错误
  //   let tableDataDummy = tableData
  //   let indexDummy = index
  //   tableData = tableDataDummy
  //   index = indexDummy

  //需要先拷贝一份,否则array.map方法修改了config
  //会导致死循环
  //let config = JSON.parse(JSON.stringify(bc))
  let config = deepCopy(bc)
  //把字符串给出的按钮key替换成JSON
  config.list = config.list.map((item: any) => {
    if ('string' == typeof item) {
      let c = supportButton(item, index)
      if (c) {
        return c
      }
      //
      return { key: item, label: item }
    } else {
      //处理
      return item
    }
  })

  //去掉隐藏的
  config.list = config.list.filter((item) => {
    //
    let show = item.show

    if (show == undefined) {
      //没有给出认为是显示
      return true
    }
    //如果明确给出返回
    if (typeof show == 'boolean') {
      return show
    } else if (typeof show == 'string') {
      if (row == undefined) {
        //可能是计算按钮列宽度,所以直接认为是可以显示
        show = true
      } else {
        show = eval(show)
      }
    }
    if (show) {
      return true
    } else {
      return false
    }
  })
  //处理disabled
  config.list = config.list.map((item) => {
    //eval时可能需要
    //
    let disabled = item.disabled

    if (disabled == undefined) {
      //没有给出认为是不禁用
      return item
    } else if (typeof disabled == 'boolean') {
      //如果明确给出返回
    } else if (typeof disabled == 'string') {
      if (row == undefined) {
        //可能是计算按钮列宽度,所以直接认为是不禁用
        disabled = false
      } else {
        disabled = eval(disabled)
      }
    }
    if (disabled) {
      disabled = true
    } else {
      disabled = false
    }
    //
    item.disabled = disabled
    //
    return item
  })
  //
  return config
}
//
//根据key返回系统支持的一些缺省按钮,如果不支持此key返回undefined
//支持_add/_edit/_delete/_copy/_up/_down/_active/_deactive - 系统内置功能
//add/edit - 用于显示增加/编辑按钮 按时不调用缺省操作,而是发出事件
function supportButton(key: any, index: any) {
  if ('_add' == key) {
    return { key: '_add', type: 'primary', label: '增加', current: false }
  } else if ('_edit' == key) {
    return { key: '_edit', type: 'success', label: '编辑' }
  } else if ('_delete' == key) {
    return { key: '_delete', type: 'danger', label: '删除' }
  } else if ('_copy' == key) {
    return { key: '_copy', type: '', label: '拷贝' }
  } else if ('_up' == key) {
    return {
      key: '_up',
      type: 'primary',
      label: '上移',
      disabled: index == 0
    }
  } else if ('_down' == key) {
    return {
      key: '_down',
      type: 'primary',
      label: '下移',
      disabled: index >= tableData?.value?.length - 1
    }
  } else if ('add' == key) {
    return { key: 'add', type: 'primary', label: '增加', current: false }
  } else if ('edit' == key) {
    return { key: 'edit', type: 'success', label: '编辑' }
  } else if ('delete' == key) {
    return { key: 'delete', type: 'danger', label: '删除' }
  } else {
    return undefined
  }
}

//根据key查找到按钮信息
function findButtonConfig(command) {
  let bc = buttonsConfig.value()
  if (!bc || !bc.list) {
    return
  }
  for (let c of bc.list) {
    //key相同或
    if (c.key == command || command.endsWith('#' + c.key)) {
      return c
    }
  }
}

//处理按钮操作-标准按钮自动处理，非标按钮发出事件
function handleButtonsClick(command, row?, index?) {
  if ('_add' == command) {
    handleAdd()
  } else if ('_edit' == command) {
    handleEdit(row, index)
  } else if ('_delete' == command) {
    handleDelete(row, index)
  } else if ('_copy' == command) {
    handleCopy(row, index)
  } else if ('_up' == command) {
    moveUp(index)
  } else if ('_down' == command) {
    moveDown(index)
  } else {
    let c = findButtonConfig(command)
    if (c) {
      //判断是记录当前行还是
      if (c.current == undefined || c.current) {
        editIndex.value = index
      } else {
        editIndex.value = -1
      }
    } else {
      editIndex.value = index
    }
    //
    emit('operate', command, row, index, commandCallback)
  }
}
function tryParseValue(row: any, key: string) {
  // 如果 row 为空或 key 为空，直接返回 undefined
  if (row == null || key == null || key === '') {
    return undefined
  }

  // 分割键路径
  const keys = key.split('.')
  let current = row

  // 逐级遍历对象
  for (let i = 0; i < keys.length; i++) {
    const k = keys[i]
    // 如果当前层级不存在该属性，返回 undefined
    if (!(k in Object(current))) {
      return undefined
    }
    // 深入下一层级
    current = current[k]
    // 如果当前值为 null 或 undefined，提前终止（后续路径无法访问）
    if (current == null) {
      return undefined
    }
  }

  return current
}
//是否提供了表格渲染
const isVueWrap = computed(() => {
  return function (child) {
    return child.props?.tableRender && typeof child.props?.tableRender == 'function'
  }
})
//计算表格单元的值,考虑了select会添加_NAME结尾的场景
function calItemValue(child, sp) {
  let nameCol = child.key + '_NAME'
  if (sp.row[nameCol]) {
    return sp.row[nameCol]
  } else {
    // let val = sp.row[child.key]
    const val = tryParseValue(sp.row, child.key)
    if (typeof val === 'boolean') {
      if (val) {
        return (
          '<span class="el-tag  el-tag--mini el-tag--success el-tag--light">是</span>'
        )
      } else {
        return (
          '<span class="el-tag  el-tag--mini el-tag--info el-tag--light">否</span>'
        )
      }
    } else {
      if (val) {
        //看看是否是日期
        if (val['$date']) {
          return formatDateTime(val['$date'])
        } else {
          return val
        }
      } else {
        return val
      }
    }
  }
}
function buildFormConfig() {
  //Changed by Jamie @2025/01/07
  //拷贝后去掉bindings部分,否则外部针对表格设置的bindings会影响弹出窗口显示
  //如设置了表格按照条件显示,此条件如果不去掉会导致弹出的FORM不显示
  //不能用deepCopy可能导致循环拷贝 - 2027/7/15
  // const formConfig = deepCopy(props.config)
  const formConfig = { ...props.config }
  formConfig.bindings = {}

  return formConfig
}
//增加
function handleAdd() {
  editIndex.value = -1
  //
  const dataRow = ref({})
  Popup(popupMode.value, dataRow, buildFormConfig(), handleDataUpdate)
}
//修改
function handleEdit(row, index) {
  editIndex.value = index
  //数据需要拷贝一份啦
  const dataRow = ref(deepCopy(row))

  Popup(popupMode.value, dataRow, buildFormConfig(), handleDataUpdate)
}
//删除
function handleDelete(row, index) {
  //加入
  ElMessageBox.confirm('确定要删除吗', '确认', {
    confirmButtonText: '是',
    cancelButtonText: '否',
    type: 'warning'
  }).then(() => {
    //触发删除事件
    if (getPropSafe('notifyChange')) {
      emit('dataDelete', row, () => {
        tableData.value.splice(index, 1)
      })
    } else {
      tableData.value.splice(index, 1)
      //
      // this.$emit('update:data', this.data)
    }
    //
  })
}
//用于外部操作回调,修改表格数据
//editIndex说明修改的数据行(负数代表新增),data给出具体数据
function commandCallback(data) {
  handleDataUpdate(data)
}
//增加/修改对话框回调函数
function handleDataUpdate(data) {
  //
  //如果没有数据设置空数据先
  if (!unref(props.data)[props.config.key]) {
    unref(props.data)[props.config.key] = []
  }
  //
  if (editIndex.value >= 0) {
    //edit
    if (getPropSafe('notifyChange')) {
      emit('dataUpdate', data, (d) => {
        unref(props.data)[props.config.key].splice(editIndex.value, 1, data)
      })
    } else {
      unref(props.data)[props.config.key].splice(editIndex.value, 1, data)
    }
  } else {
    //insert
    if (getPropSafe('notifyChange')) {
      emit('dataInsert', data, (d) => {
        insertData(d)
      })
    } else {
      insertData(data)
      //this.$emit('update:data', this.data)
    }
  }
}

//插入数据
function insertData(d) {
  if (getPropSafe('insertAt') == 'top') {
    unref(props.data)[props.config.key].unshift(d)
  } else {
    unref(props.data)[props.config.key].push(d)
  }
}
//拷贝
function handleCopy(row) {
  ElMessageBox.prompt(
    '确认拷贝?' + ':' + (props.config.label ? props.config.label : ''),
    '确认',
    {
      confirmButtonText: '是',
      cancelButtonText: '否',
      inputValidator: copyNameValidator,
      inputValue: row.name
        ? row.name + ' 拷贝成功'
        : '拷贝完成',
      closeOnClickModal: false
    }
  ).then(({ value }) => {
    //let d = JSON.parse(JSON.stringify(row))
    let d = deepCopy(row)

    //删除_id等字段,这样可以创建
    delete d._id
    delete d._insertTime
    delete d._updateTime
    d.name = value
    if (d.active != undefined) {
      d.active = false
    }
    //insert
    if (getPropSafe('notifyChange')) {
      emit('dataInsert', d, (d) => {
        insertData(d)
      })
    } else {
      insertData(d)
    }
  })
}
//拷贝后名称校验
function copyNameValidator(value) {
  if (!value) {
    return '必须输入'
  }
}
//上移
function moveUp(index) {
  if (index == 0) {
    return
  }
  //
  let d = tableData.value[index]
  let d1 = tableData.value[index - 1]
  tableData.value.splice(index - 1, 2, d, d1)
  //added by jamie@2022/10/19,增加事件
  emit('dataMoveUp', tableData.value, index)
}
//下移
function moveDown(index) {
  if (index >= tableData.value.length - 1) {
    return
  }
  //
  let d = tableData.value[index]
  let d1 = tableData.value[index + 1]
  tableData.value.splice(index, 2, d1, d)
  //added by jamie@2022/10/19,增加事件
  emit('dataMoveDown', tableData.value, index)
}

function handleSelectionChange(val) {
  emit('selectionChange', val)
}
//
//导出Excel
function exportExcel(filename = 'table.xlsx') {
  /* 从表生成工作簿对象 */
  var wb = XLSX.utils.table_to_book(document.querySelector('#' + rawTable.value), { raw: true })

  /* 获取二进制字符串作为输出 */
  var wbout = XLSX.write(wb, {
    bookType: 'xlsx',
    bookSST: true,
    type: 'array'
  })
  try {
    FileSaver.saveAs(
      //Blob 对象表示一个不可变、原始数据的类文件对象。
      //Blob 表示的不一定是JavaScript原生格式的数据。
      //File 接口基于Blob，继承了 blob 的功能并将其扩展使其支持用户系统上的文件。
      //返回一个新创建的 Blob 对象，其内容由参数中给定的数组串联组成。
      new Blob([wbout], { type: 'application/octet-stream' }),
      //设置导出文件名称
      filename
    )
  } catch (e) {
    // if (typeof console !== "undefined") console.log(e, wbout);
  }
  return wbout
}
//
defineExpose({ exportExcel })
</script>
