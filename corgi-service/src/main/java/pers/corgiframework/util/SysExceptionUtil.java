package pers.corgiframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.tool.constants.BisPromptConstant;

/**
 * 异常处理类
 * Created by syk on 2017/8/25.
 */
public class SysExceptionUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysExceptionUtil.class);

    /**
     * 异常处理
     * @param e
     * @return
     */
    public static BisPrompt response(Exception e) {
        BisPrompt bisPrompt = new BisPrompt();
        // 系统异常
        LOGGER.error(e.getMessage(), e);
        bisPrompt.setBisStatus(BisPromptConstant.SYSTEM_EXCEPTION_STATUS);
        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.SYSTEM_EXCEPTION_STATUS));
        return bisPrompt;
    }

}
