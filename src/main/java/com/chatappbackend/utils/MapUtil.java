package com.chatappbackend.utils;

import java.util.HashMap;
import java.util.Map;
import org.springframework.cglib.beans.BeanMap;

public class MapUtil {
  public static Map<String, Object> toMap(Object obj) {
    return new HashMap<>(BeanMap.create(obj));
  }
}
