package com.ridko.sk4.common;

/**
 * 环境变量工具
 *
 * @author smitea
 * @since 2018-11-01
 */
public class PropertyTools {
  public static <T> T getProperty(String key, T defaultValue) {
    String value = System.getProperty(key);
    if (!"".equals(value)) {
      if (defaultValue.getClass().equals(Boolean.class)) {
        return (T) Boolean.valueOf(value);
      } else if (defaultValue.getClass().equals(Integer.class)) {
        return (T) Integer.valueOf(value);
      } else if (defaultValue.getClass().equals(String.class)) {
        return (T) value;
      } else if (defaultValue.getClass().equals(Float.class)) {
        return (T) Float.valueOf(value);
      } else if (defaultValue.getClass().equals(Double.class)) {
        return (T) Double.valueOf(value);
      }
    }
    return defaultValue;
  }
}
