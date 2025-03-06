/**
 * 添加、修改词条
 * @param data 词条
 * @returns {*} 结果
 */
const saveWord = data => {
    return request({
        url: '/word',
        method: 'POST',
        data: data
    })
}

/**
 * 删除词条
 * @param ids ID列表
 * @returns {*} 结果
 */
const removeWordBatchByIds = ids => {
    return request({
        url: `/word/${ids}`,
        method: 'DELETE'
    })
}

/**
 * 查询词条列表
 * @param params 词条
 * @returns {*} 结果
 */
const getWordList = params => {
    return request({
        url: '/word/list',
        method: 'GET',
        params: params
    })
}

/**
 * 查询词条分页
 * @param params 词条
 * @returns {*} 结果
 */
const getWordPage = params => {
    return request({
        url: '/word/page',
        method: 'GET',
        params: params
    })
}

/**
 * 查询词条
 * @param params 词条
 * @returns {*} 结果
 */
const getWordOne = params => {
    return request({
        url: '/word',
        method: 'GET',
        params: params
    })
}

/**
 * 查询词条
 * @param id 主键ID
 * @returns {*} 结果
 */
const getWordById = id => {
    return request({
        url: `/word/${id}`,
        method: 'GET'
    })
}

/**
 * 导出词条
 * @param params 词条
 * @returns {*} 结果
 */
const exportWordExcel = params => {
    return request({
        url: '/word/export',
        method: 'GET',
        params: params
    })
}
