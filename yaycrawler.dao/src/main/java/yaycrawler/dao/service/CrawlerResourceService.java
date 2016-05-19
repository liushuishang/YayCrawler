package yaycrawler.dao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yaycrawler.dao.repositories.PageSiteRepository;

/**
 * Created by ucs_yuananyun on 2016/5/18.
 */
@Service
public class CrawlerResourceService {

    @Autowired
    private PageSiteRepository siteRepository;


}
