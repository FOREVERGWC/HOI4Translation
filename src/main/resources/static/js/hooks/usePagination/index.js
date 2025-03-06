const usePagination = (callback, option = { pageSize: 20 }) => {
	const pagination = reactive({
		current: 1,
		pageSize: option.pageSize,
		total: 0,
		onCurrentChange: val => {
			pagination.current = val
			callback && callback()
		},
		onPageSizeChange: val => {
			pagination.current = 1
			pagination.pageSize = val
			callback && callback()
		}
	})

	const handleCurrentChange = option.onCurrentChange
	const handleSizeChange = option.onPageSizeChange
	const setTotal = val => {
		pagination.total = val
	}

	const { current, pageSize, total } = toRefs(pagination)

	return {
		current,
		pageSize,
		total,
		pagination,
		handleCurrentChange,
		handleSizeChange,
		setTotal
	}
}
