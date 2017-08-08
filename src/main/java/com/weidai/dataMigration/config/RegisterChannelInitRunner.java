/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.weidai.dataMigration.util.UserMigrationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 初始化渠道信息
 * @author wuqi 2017/8/8 0008.
 */
@Component
public class RegisterChannelInitRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(RegisterChannelInitRunner.class);

    @Override
    public void run(String... args) throws Exception {
        YamlMapFactoryBean yaml = new YamlMapFactoryBean();
        yaml.setResources(new ClassPathResource("registerChannel.yml"));
        Map<String, Object> map = yaml.getObject();
        logger.info("load channel info finished, size: {}", map.size());
        Map<String, Integer> channelMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof List) {
                List<String> list = (List<String>) entry.getValue();
                for (String code : list) {
                    channelMap.put(code, Integer.parseInt(entry.getKey()));
                }
            }
        }
        UserMigrationHolder.bindChannelMap(channelMap);
    }

    public static void main(String[] args) throws FileNotFoundException {
        YamlMapFactoryBean yaml = new YamlMapFactoryBean();
        yaml.setResources(new ClassPathResource("registerChannel.yml"));
        Map<String, Object> map = yaml.getObject();
        System.out.println(map.keySet().contains("1"));
    }
}
