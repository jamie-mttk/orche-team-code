import moment from 'moment'
//格式化日期和时间
//把格式2019-05-08T03:29:22.775Z转换成2019-05-08 03:29:22
export function formatDateTime(val) {
  if (val && val.length >= 22) {
    val = val.substr(0, 10) + ' ' + val.substr(11, 8)
  }
  return val
}

/**Create unique string
 * @returns {string}
 */
export function createUniqueString() {
  const timestamp = +new Date() + ''
  const randomNum = parseInt((1 + Math.random()) * 65536 + '')
  //原来使用+(randomNum + timestamp)会导致精度丢失
  const num = BigInt(randomNum + timestamp)
  return num.toString(32)
}
//格式化日期和时间
//把格式2019-05-08T03:29:22.775Z转换成2019-05-08 03:29:22.775
export function formatDateTimeAll(val) {
  if (val && val.length >= 22) {
    val = val.substr(0, 10) + ' ' + val.substr(11, 8) + '.' + val.substr(20, 3)
  }
  return val
}
//把输入日期的时分秒去掉
export function clearHMS(date) {
  date.setHours(0)
  date.setMinutes(0)
  date.setSeconds(0)
  date.setMilliseconds(0)
  return date
}
//把传入日期格式化成yyyyMMdd HH:mm:ss的格式
export function formatDate(date) {
  const month = fillWithZero(date.getMonth() + 1)
  const strDate = fillWithZero(date.getDate())
  const ret = `${date.getFullYear()}-${month}-${strDate} ${fillWithZero(date.getHours())}:${fillWithZero(date.getMinutes())}:${fillWithZero(date.getSeconds())}`
  return ret
}
//转换为2位字符串，不足前面补零
function fillWithZero(obj) {
  return obj.toString().padStart(2, '0')
}
//删除数组中满足条件的所有项
export function spliceByCondition(arr: Array<any>, condition: Function) {
  if (!Array.isArray(arr)) {
    return
  }
  for (let i = 0; i < arr.length; i++) {
    if (condition(arr[i])) {
      arr.splice(i, 1)
      i--
    }
  }
}

// 格式化执行时间
export function formatDuaration(milliseconds: number): string {
  if (milliseconds <= 0) {
    milliseconds = 0
  }
  const seconds = Math.floor(milliseconds / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)

  if (hours > 0) {
    return `${hours}h${minutes % 60}m${seconds % 60}s`
  } else if (minutes > 0) {
    return `${minutes}m${seconds % 60}s`
  } else {
    return `${seconds}s`
  }
}
//深拷贝
export function deepCopy(obj) {
  //防止为undefined
  if (obj == undefined) {
    return obj
  }
  if (!(typeof obj === 'object')) {
    return obj
  }
  //
  if (Array.isArray(obj)) {
    //console.log('2:'+JSON.stringify(obj))
    const result = []
    obj.map((item) => {
      result.push(deepCopy(item))
    })
    //
    return result
  } else {
    // 创建一个新对象
    const result = {}
    Object.keys(obj).map((key) => {
      result[key] = deepCopy(obj[key])
    })
    return result
  }
}
//数字加千位符,只适用于num为整数
export function thousandBitSeparator(num) {
  if (num == undefined) {
    return undefined
  }
  return num.toString().replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,')
}

export function startOfDay() {
  return moment().startOf('day').format('YYYY-MM-DD HH:mm:ss')
}
//输入参数不为空的数组
export function notEmptyArray(data: any) {
  return !!(data && Array.isArray(data) && data.length > 0)
}

// 格式化文件大小
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
