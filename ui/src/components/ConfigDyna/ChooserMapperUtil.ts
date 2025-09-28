import { unref } from 'vue'
export function clearMapper(data: any) {
  unref(data).inputType = ''
  unref(data).outputType = ''
  unref(data).inputsTable = []
  unref(data).outputsTable = []
}

export function applyMapper(data: any, mapper: any) {
  const sourceMessage = findMesasge(mapper, 'SOURCE')
  unref(data).inputType = parseType(sourceMessage)
  const targetMessage = findMesasge(mapper, 'TARGET')
  unref(data).outputType = parseType(targetMessage)
  //
  applyInputs(data, mapper)
  applyOutputs(data, mapper)
}

function findMesasge(mapper: any, messageCategory: string) {
  for (const comp of mapper.components || []) {
    if ('MESSAGE' == comp.type && comp.messageCategory == messageCategory) {
      return comp
    }
  }
}
function parseType(message) {
  if (!message) {
    return 'DUMMY'
  }
  if (message.messageType == 'SQL') {
    return 'SQL'
  } else {
    return 'FILE'
  }
}
//处理inputs,注意需要考虑保留以前已经输入的信息
function applyInputs(data: any, mapper: any) {
  const result: any[] = []
  for (const comp of mapper.components || []) {
    if ('INPUT' != comp.type) {
      continue
    }
    const existing = findInArrayByKey(data.inputsTable, comp.key)
    result.push({
      key: comp.key,
      name: comp.name,
      value: existing ? existing.value : 'p:' + comp.name
    })
  }
  //
  data.inputsTable = result
}
//处理outputs,注意需要考虑保留以前已经输入的信息
function applyOutputs(data: any, mapper: any) {
  const result: any[] = []
  for (const comp of mapper.components || []) {
    if ('OUTPUT' != comp.type) {
      continue
    }
    const existing = findInArrayByKey(data.outputsTable, comp.key)
    result.push({
      key: comp.key,
      name: comp.name,
      storeKey: existing ? existing.storeKey : comp.name
    })
  }
  //
  data.outputsTable = result
}

function findInArrayByKey(arr: any[], key: string) {
  if (!arr) {
    return
  }
  for (const item of arr) {
    if (key == item.key) {
      return item
    }
  }
}
