import redis.clients.jedis.Jedis;

public class JedisExample {
    public static void main(String[] args) {

        Jedis jedis = new Jedis("192.168.99.100", 6379);

        System.out.println("Ping redis: " + jedis.ping());
        jedis.disconnect();
    }
}