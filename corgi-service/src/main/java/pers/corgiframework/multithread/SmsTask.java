package pers.corgiframework.multithread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.corgiframework.dao.domain.SmsRecord;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.ISmsService;
import pers.corgiframework.tool.utils.DateTimeUtil;

import java.util.Map;

/**
 * 短信任务
 * Created by UncleHey on 2018.10.19.
 */
@Component
public class SmsTask implements ITask<BisPrompt<String>, Integer> {

    @Autowired
    private ISmsService smsService;

    @Override
    public BisPrompt<String> execute(Integer e, Map<String, Object> params) {
        SmsRecord smsRecord = new SmsRecord();
        smsRecord.setMobile("13045759027");
        smsRecord.setType(1);
        String param = String.valueOf(String.valueOf(params.get("param")));
        String s = e.toString().concat(param);
        smsRecord.setContent(s);
        smsRecord.setFlag(1);
        smsRecord.setCreateTime(DateTimeUtil.getNowDateTime());
        smsService.insert(smsRecord);
        BisPrompt bisPrompt = new BisPrompt();
        bisPrompt.setBisObj(s);
        return bisPrompt;
    }
}
