package remote_webview.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StringUtil {

    /**
     *
     * map -> string
     * @param map
     * @return
     */
    public static String getMapToString(Map<String,String> map){
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyArray.length; i++) {
            if (map.get(keyArray[i]).trim().length() > 0) {
                sb.append(keyArray[i]).append("=").append(map.get(keyArray[i]).trim());
            }
            if(i != keyArray.length-1){
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * String -> map
     * @param str
     * @return
     */
    public static Map<String,String> getStringToMap(String str){
        if(null == str || "".equals(str)){
            return null;
        }
        String[] strings = str.split("&");
        int mapLength = strings.length;
        if((strings.length % 2) != 0){
            mapLength = mapLength+1;
        }
        Map<String,String> map = new HashMap<>(mapLength);
        for (String string : strings) {
            String[] strArray = string.split("=");
            //strArray[0]->KEY  strArray[1]->value
            map.put(strArray[0], strArray[1]);
        }
        return map;
    }


}
