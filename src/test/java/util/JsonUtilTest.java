package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.crawl.pojo.RestaurantCode;
import com.crawl.util.JsonUtil;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class JsonUtilTest {

    @Test
    public void toObjectTest(){
        RestaurantCode restaurantCode = JsonUtil.toObject("{\"name\":\"qwe\",\"code\":\"123\"}", new TypeReference<RestaurantCode>() {});
        Assertions.assertEquals(restaurantCode.getName(),"qwe");
        Assertions.assertEquals(restaurantCode.getCode(),"123");

    }

    @Test
    public void toJsonNodeTest(){
        JsonNode jsonNode = JsonUtil.toJsonNode("{\"name\":\"tom\",\"home\":{\"add\":\"beijing\"}}");
        Assertions.assertEquals(jsonNode.get("name").asText(),"tom");
        Assertions.assertEquals(jsonNode.get("home").get("add").asText(),"beijing");

    }

    @Test
    public void toJsonString(){
        String jack = JsonUtil.toJsonString(new RestaurantCode("jack", "234"));
        Assertions.assertEquals(jack,"{\"name\":\"jack\",\"code\":\"234\"}");
    }

}
