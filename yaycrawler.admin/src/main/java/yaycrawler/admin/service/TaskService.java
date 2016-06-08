package yaycrawler.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yaycrawler.common.utils.CSVUtils;
import yaycrawler.common.utils.ExcelUtils;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Administrator on 2016/6/8.
 */
@Service
public class TaskService {

    public Map insertExcel(MultipartFile file) {
        Map result = new HashMap();
        try {
            InputStream input = file.getInputStream();
            List<Map> requestList = ExcelUtils.importExcel(input);
            List<Map> columns = new ArrayList<>();
            if(requestList != null && requestList.size() > 0) {
                Map<String,String> request = requestList.get(0);

                for (String key:request.keySet()) {
                    Map column = new HashMap();
                    column.put("field", key);
                    column.put("title",key);
                    columns.add(column);
                }
            }
            result.put("columns",columns);
            result.put("data",requestList);
            result.put("total",requestList.size());
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", true);
            e.printStackTrace();
        }
        return result;
    }

    public Map insertCSV(MultipartFile file) {
        Map result = new HashMap();
        try {
            InputStream input = file.getInputStream();
            List<Map> requestList = CSVUtils.importCsv(input);
            List<Map> columns = new ArrayList<>();
            if(requestList != null && requestList.size() > 0) {
                Map<String,String> request = requestList.get(0);
                for (String key:request.keySet()) {
                    Map column = new HashMap();
                    column.put("field", key);
                    column.put("title",key);
                    columns.add(column);
                }
            }
            result.put("columns",columns);
            result.put("data",requestList);
            result.put("total",requestList.size());
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
