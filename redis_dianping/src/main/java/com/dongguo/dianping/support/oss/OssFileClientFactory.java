package com.dongguo.dianping.support.oss;

import com.dongguo.dianping.support.ApplicationContextHolder;

public class OssFileClientFactory {

    public static OssFileClient build() {
        OssProperties config = ApplicationContextHolder.getBean(OssProperties.class);
        return new OssFileClient(config);
    }
}
