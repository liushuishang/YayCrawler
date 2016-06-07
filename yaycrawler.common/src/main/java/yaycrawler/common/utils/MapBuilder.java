package yaycrawler.common.utils;

import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public class MapBuilder {
    private Map _map;

    public static MapBuilder instance(){
        return new MapBuilder();
    }
    public MapBuilder put(String key, Object value){
        _map.put(key, value);
        return this;
    }
    public Map<String,Object> map()
    {
        return _map;
    }
}
