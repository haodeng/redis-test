import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;

public class JedisTest {
    private static Jedis jedis;

    @BeforeClass
    public static void setup(){
        jedis = new Jedis("192.168.99.100", 6379);
    }

    @AfterClass
    public static void teardown(){
        if(null != jedis){
            jedis.disconnect();
        }
    }

    @Test
    public void testString(){
        jedis.del("name");
        jedis.set("name", "Hao Deng");
        Assert.assertEquals("Hao Deng", jedis.get("name"));
    }

    @Test
    public void testListAppend(){
        jedis.del("books");
        jedis.rpush("books", "Redis", "Java", "Spring", "Git");
        Assert.assertEquals(Long.valueOf(4L), jedis.llen("books"));

        List<String> list = jedis.lrange("books", 0 , jedis.llen("books")-1);
        Assert.assertEquals("Redis", list.get(0));
        Assert.assertEquals("Java", list.get(1));
        Assert.assertEquals("Spring", list.get(2));
        Assert.assertEquals("Git", list.get(3));
    }

    @Test
    public void testListPrepend(){
        jedis.del("books");
        jedis.lpush("books", "Redis", "Java", "Spring", "Git");
        Assert.assertEquals(Long.valueOf(4L), jedis.llen("books"));

        List<String> list = jedis.lrange("books", 0 , jedis.llen("books")-1);
        Assert.assertEquals("Redis", list.get(3));
        Assert.assertEquals("Java", list.get(2));
        Assert.assertEquals("Spring", list.get(1));
        Assert.assertEquals("Git", list.get(0));
    }
}