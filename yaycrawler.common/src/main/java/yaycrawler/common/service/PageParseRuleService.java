package yaycrawler.common.service;

import org.springframework.stereotype.Service;
import yaycrawler.common.dao.FieldParseRule;

import java.util.List;

/**
 * Created by yuananyun on 2016/4/24.
 */
@Service
public class PageParseRuleService {

    /**
     * 获取页面字段的解析规则
     *
     * @param pageUrl 页面的Url
     * @return
     */
    public List<FieldParseRule> getPageFieldParseRules(String pageUrl) {
        return null;
    }

}
