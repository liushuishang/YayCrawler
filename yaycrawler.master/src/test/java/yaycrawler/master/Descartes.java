package yaycrawler.master;

/**
 * Created by Administrator on 2016/5/26.
 */
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.*;

public class Descartes
{

    public static void run(List<List<Map<Object,Object>>> dimvalue, List<Map> result, int layer,Map<Object,Object> curstring)
    {
        //大于一个集合时：
        if (layer < dimvalue.size() - 1)
        {
            //大于一个集合时，第一个集合为空
            if (dimvalue.get(layer).size() == 0)
                run(dimvalue, result, layer + 1, curstring);
            else
            {
                for (int i = 0; i < dimvalue.get(layer).size(); i++)
                {
                    Map tmp = Maps.newHashMap(curstring);
                    for (Map.Entry<Object,Object> entry:dimvalue.get(layer).get(i).entrySet()) {
                        tmp.put(entry.getKey(),entry.getValue());
                    }
                    run(dimvalue, result, layer + 1,tmp);
                }
            }
        }
        //只有一个集合时：
        else if (layer == dimvalue.size() - 1)
        {
            //只有一个集合，且集合中没有元素
            if (dimvalue.get(layer).size() == 0)
                result.add(curstring);
                //只有一个集合，且集合中有元素时：其笛卡尔积就是这个集合元素本身
            else
            {
                for (int i = 0; i < dimvalue.get(layer).size(); i++)
                {
                    Map tmp = Maps.newHashMap(curstring);
                    for (Map.Entry<Object,Object> entry:dimvalue.get(layer).get(i).entrySet()) {
                        tmp.put(entry.getKey(),entry.getValue());
                    }
                    result.add(tmp);
                }
            }
        }
    }


    @Test
    public void Test()
    {
        List<List<Map>> dimvalue = new ArrayList<List<Map>>();
        List<Map> v1 = new ArrayList<Map>();
        v1.add(ImmutableMap.of("p","1"));
        v1.add(ImmutableMap.of("p","1"));
        List<Map> v2 = new ArrayList<Map>();
        v2.add(ImmutableMap.of("work_location","11"));
        v2.add(ImmutableMap.of("work_location","12"));
        v2.add(ImmutableMap.of("work_location","13"));
        List<Map> v3 = new ArrayList<Map>();
        v3.add(ImmutableMap.of("sex","m"));
        v3.add(ImmutableMap.of("sex","f"));
        dimvalue.add(v1);
        dimvalue.add(v2);
        dimvalue.add(v3);

        List<Map> result = new ArrayList<Map>();
//        Descartes.run(dimvalue, result, 0, Maps.newHashMap());
        ImmutableSet<ImmutableMap<String,String>> data1 = ImmutableSet.of(ImmutableMap.of("p","1"),ImmutableMap.of("p","2"));
        ImmutableSet<ImmutableMap<String,String>> data2 = ImmutableSet.of(ImmutableMap.of("work_location","11"),ImmutableMap.of("work_location","12"),ImmutableMap.of("work_location","13"));
        ImmutableSet<ImmutableMap<String,String>> data3 = ImmutableSet.of(ImmutableMap.of("sex","m"),ImmutableMap.of("sex","f"));
        List<Map> tmp1 = Lists.newArrayList();
        List tmpSet1 = Lists.newArrayList();
        tmpSet1.add(data1);
        tmpSet1.add(data2);
        tmpSet1.add(data3);
        Set<List<ImmutableMap<String,String>>> sets = Sets.cartesianProduct(tmpSet1);
        for (List<ImmutableMap<String,String>> datas : sets) {
            Map tmp = Maps.newHashMap();
            for (Map data:datas) {
                if(data != null)
                 tmp.put(data.keySet().iterator().next(),data.values().iterator().next());
            }
            tmp1.add(tmp);
        }
        ImmutableSet<String> charList1 = ImmutableSet.of("p=1", "p=2", "p=3");
        ImmutableSet<String> charList2 = ImmutableSet.of("work_location=11", "work_location=12");
        ImmutableSet<String> charList3 = ImmutableSet.of("sex=m", "sex=f");
        List tmpSet = Lists.newArrayList();
        tmpSet.add(charList1);
        tmpSet.add(charList2);
        tmpSet.add(charList3);
        Set<List<String>> set = Sets.cartesianProduct(tmpSet);

        for (List<String> characters : set) {

        }
        int i = 1;
        for (Map s : result)
        {
            System.out.println(i++ + ":" +s);
        }
    }

}