package com.lonbon.lonbon_app.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * Created by PHJ on 2020/6/19.
 */

public class FileUtils {

    /**
     * 解析主机、网页发过来的协议，转为一个map
     * @param action 接收到的协议
     * @return
     */
    public static Map<String, String> parseActionToMap(String action) {

        Map<String, String> priEntityMap = new HashMap<String, String>();
        BufferedReader bReader = new BufferedReader(new StringReader(action));
        String temp;
        try {
            while ((temp = bReader.readLine()) != null) {
                if (temp.contains(":")) {
                    String[] strs;
                    strs = temp.split(":", 2);
                    priEntityMap.put(strs[0].trim(), strs[1].trim());

                    /**
                     * 有重复键的协议，到具体的工厂类中解析
                     * Action:Display17WaitInfo 候诊区辅显示屏4，发送所有病人数据
                     * Action:UpdateUserDefineInfo 病人一览表，发送用户自定义护理级别
                     */
                    if (("Action".equals(strs[0]) && "Display17WaitInfo".equals(strs[1].trim()))
                            ||("InterCmd".equals(strs[0]) && "UpdateUserDefineInfo".equals(strs[1].trim()))
                            || ("InterCmd".equals(strs[0]) && "UpdateUserDefineInfoExpend".equals(strs[1].trim()))) {
                        priEntityMap.put("Data", action);
                    }
                }
            }
            bReader.close();
        } catch (IOException e) {
            Log.d("lonbon_lcd_display", "NetTaskDeal -->  parseActionToMap catch IOException :" + e.toString());
            e.printStackTrace();
        }

        return priEntityMap;
    }


}
