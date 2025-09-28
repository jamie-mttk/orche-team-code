//change from mdi_dialpad to mdiDialpad
export function toHump(name: string) {
    if(!name){
        return name
    }
  return name.replace(/\-(\w)/g, function (all, letter) {
    return letter.toUpperCase()
  })
}

//change from mdiDialpad to mdi_dialpad
export function toLine(name: string) {
    if(!name){
        return name
    }
  return name.replace(/([A-Z])/g, '-$1').toLowerCase()
}
