const useTable = (api, option) => {
	const { formatResult, onSuccess, immediate, rowKey } = option || {}
	const { pagination, setTotal } = usePagination(() => getRecords())
	// 加载
	const loading = ref(false)
	// 数据项
	const records = ref([])

	// 查询数据项
	const getRecords = () => {
		loading.value = true
		api({ pageNo: pagination.current, pageSize: pagination.pageSize })
			.then(res => {
				const list = Array.isArray(res) ? res : res.records
				records.value = formatResult ? formatResult(list) : list
				const total = Array.isArray(res) ? res.length : res.total
				setTotal(total)
				onSuccess && onSuccess()
			})
			.finally(() => {
				loading.value = false
			})
	}

	// 是否即刻触发
	const isImmediate = immediate ?? true
	isImmediate && getRecords()

	// 选中项
	const selectedKeys = ref([])
	// 单选
	const single = ref(true)
	// 多选
	const multiple = ref(true)

	// 选择
	const handleSelectionChange = selection => {
		const key = rowKey ?? 'id'
		selectedKeys.value = selection.map(item => toRaw(item)[key])
		single.value = selection.length !== 1
		multiple.value = !selection.length
	}

	// 删除
	const onDelete = (deleteApi, option) => {
		console.log('删除', option)
		deleteApi().then(() => {
			ElMessage.success('删除成功！')
			selectedKeys.value = []
			getRecords()
		})
	}

	return {
		loading,
		records,
		getRecords,
		pagination,
		selectedKeys,
		single,
		multiple,
		handleSelectionChange,
		onDelete
	}
}
