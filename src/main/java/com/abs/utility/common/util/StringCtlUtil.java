package com.abs.utility.common.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCtlUtil {

    public static String disseverUrlToOnlyPath(String url) {
        Pattern urlPattern = Pattern.compile("^(https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?((\\/[^\\s/\\/]+)*)?\\/([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?$");

        Matcher mc = urlPattern.matcher(url);
        String path = "";
        if(mc.matches()){
//            String domain = mc.group(2);

            if (mc.group(5).length() > 1) {
                if("/".equals(mc.group(5).substring(0, 1))){
                    path = mc.group(5).substring(1, mc.group(5).length())+"/"+mc.group(7);
                }else{
                    path = mc.group(5) + "/" + mc.group(7);
                }
            } else {
                path = mc.group(7);
            }
//            System.out.println("domain = "+domain);
//            System.out.println("path = "+path);
        }
        return path;
    }

    public static boolean isEmptyTrim(String string) {
        if (StringUtils.hasLength(string)){
            if (!string.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static Integer parseInteger(String value) {
        Integer result = null;
        if (StringUtils.hasLength(value)) {
            try {
                result = Integer.parseInt(value.trim());
            } catch (Exception e) {
            }
        }
        return result;
    }

}
