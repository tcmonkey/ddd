package com.ddd.application.ddd.adaptor;

import com.ddd.common.result.Result;
import com.ddd.application.ddd.command.DddExternalReadCommand;
import com.ddd.model.ddd.DddExternalReadDO;

/**
 * DDD 外部数据查询端口模板。
 *
 * <p>端口由 application 定义，具体第三方协议与调用细节由 adaptor output 实现。</p>
 *
 * @author AIGenerator
 */
public interface DddOutputAdaptor {
    /**
     * 查询指定标识对应的外部数据。
     *
     * @param command 外部读取应用命令
     * @return 转换后的项目内部数据对象操作结果
     *
     * @author AIGenerator
     */
    Result<DddExternalReadDO> query(DddExternalReadCommand command);
}
