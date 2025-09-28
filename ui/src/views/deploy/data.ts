//已部署界面配置
export const deployExistConfig = {
  props: {
    cols: 'type:100,key:160,name:300,description',
    operateColWidth: 180,
    buttonsConfig: {
      list: [
        '_delete',
        {
          key: 'suppress',
          label: '禁用'
        }
      ]
    },
    emptyButtonsConfig: { list: [] }
  },
  children: [
    {
      key: 'type',
      label: '类型'
    },
    {
      key: 'key',
      label: '键'
    },
    {
      key: 'name',
      label: '名称'
    },
    {
      key: 'description',
      label: '描述'
    },
    {
      key: 'suppress',
      label: '禁用'
    }
  ]
}

//禁用部署界面配置
export const deploySuppressConfig = {
  props: {
    cols: 'type:100,key:160,name:300,description',
    operateColWidth: 100,
    buttonsConfig: {
      list: [
        {
          key: 'unsuppress',
          type: 'danger',
          label: '取消禁用'
        }
      ]
    },
    emptyButtonsConfig: { list: [] }
  },
  children: [
    {
      key: 'type',
      label: '类型'
    },
    {
      key: 'key',
      label: '键'
    },
    {
      key: 'name',
      label: '名称'
    },
    {
      key: 'description',
      label: '描述'
    },
    {
      key: 'suppress',
      label: '禁用'
    }
  ]
}

export const criteriaConfig = {
  sizeButtons: 6,
  children: [
    {
      key: 'type',
      mode: 'select',
      label: '类型',
      size: 4,
      props: {
        options: 'FOLDER,ACTION,SERVICE,RESOURCE,PLUGIN,MAPPER_METHOD_FOLDER,MAPPER_METHOD'
      }
    },
    { key: 'key', label: '键', size: 4 },
    { key: 'name', label: '名称', size: 4 }
  ]
}
