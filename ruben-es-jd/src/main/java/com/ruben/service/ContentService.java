package com.ruben.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ContentService
 * @Date: 2020/4/15 21:53
 * @Description:
 */
public interface ContentService {

    Boolean parseContent(String keywords) throws IOException;

    List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException;

    List<Map<String, Object>> searchPageHighLight(String keyword, int pageNo, int pageSize) throws IOException;
}
