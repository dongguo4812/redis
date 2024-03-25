package com.dongguo.dianping;

import com.dongguo.dianping.entity.POJO.Shop;
import com.dongguo.dianping.service.IShopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dongguo.dianping.utils.RedisConstants.SHOP_GEO_KEY;

@SpringBootTest
public class ShopGeoTest {

    @Autowired
    private IShopService shopService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void loadShopData() {
        // 1.查询店铺信息
        List<Shop> shopList = shopService.list();
        // 2.把店铺分组，按照typeId分组，typeId一致的放到一个集合
        Map<Long, List<Shop>> map = shopList.stream().collect(Collectors.groupingBy(Shop::getTypeId));
        // 3.分批完成写入Redis
        map.entrySet().forEach(shopEntry -> {
            Long typeId = shopEntry.getKey();
            String key = SHOP_GEO_KEY + typeId;
            List<Shop> shops = shopEntry.getValue();
            //同类型店铺集合
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(shops.size());
            shops.forEach(shop -> {
                locations.add(new RedisGeoCommands.GeoLocation<>(
                        shop.getId().toString(),
                        new Point(shop.getX(), shop.getY())
                ));
            });
            //重复数据会更新为最新的坐标
            stringRedisTemplate.opsForGeo().add(key, locations);
        });
    }
}
