"# redis-test" 
# Setup Redis on Docker

````javascript
docker pull redis
docker run -p 6379:6379 redis
````

On Windows, try connection test by FastoRedis, Test 192.168.99.100 6379.

# Connection and ping test

````java
//Connect to redis
jedis = new Jedis("192.168.99.100", 6379);

//Ping
jedis.ping()
````

# Some Test Cases.
````java
 @Test
    public void testString(){
        jedis.del("name");
        jedis.set("name", "Hao Deng");
        assertEquals("Hao Deng", jedis.get("name"));
    }

    @Test
    public void testListAppend(){
        jedis.del("books");
        jedis.rpush("books", "Redis", "Java", "Spring", "Git");
        assertEquals(Long.valueOf(4L), jedis.llen("books"));

        List<String> list = jedis.lrange("books", 0 , jedis.llen("books")-1);
        assertEquals("Redis", list.get(0));
        assertEquals("Java", list.get(1));
        assertEquals("Spring", list.get(2));
        assertEquals("Git", list.get(3));
    }

    @Test
    public void testListPrepend(){
        jedis.del("books");
        jedis.lpush("books", "Redis", "Java", "Spring", "Git");
        assertEquals(Long.valueOf(4L), jedis.llen("books"));

        List<String> list = jedis.lrange("books", 0 , jedis.llen("books")-1);
        assertEquals("Redis", list.get(3));
        assertEquals("Java", list.get(2));
        assertEquals("Spring", list.get(1));
        assertEquals("Git", list.get(0));
    }

    @Test
    public void testSet(){
        jedis.del("books_set");
        jedis.sadd("books_set", "Java");
        jedis.sadd("books_set", "Rails");
        jedis.sadd("books_set", "Java");

        assertTrue(jedis.sismember("books_set", "Java"));
        assertTrue(jedis.sismember("books_set", "Rails"));

        Set<String> books = jedis.smembers("books_set");
        assertTrue(books.contains("Java"));
        assertTrue(books.contains("Rails"));
        assertEquals(2, books.size());
    }

    @Test
    public void testHash(){
        jedis.del("params");
        jedis.hset("params", "name", "Hao");
        jedis.hset("params", "gender", "male");

        assertEquals("Hao", jedis.hget("params", "name"));

        Map<String, String> params = jedis.hgetAll("params");
        assertEquals("Hao", params.get("name"));
        assertEquals("male", params.get("gender"));
    }

    @Test
    public void testTransaction(){
        jedis.del("T1");
        jedis.del("T2");
        Transaction t = jedis.multi();
        t.sadd("T1", "V1");
        t.sadd("T2", "V2");
        t.exec();

        assertTrue(jedis.sismember("T1", "V1"));
        assertTrue(jedis.sismember("T2", "V2"));
    }

    @Test
    public void testPipeline() throws IOException {
        jedis.del("name");
        jedis.del("books");
        jedis.del("books_set");

        Pipeline p = jedis.pipelined();
        p.set("name", "Hao Deng");
        p.get("name");

        p.rpush("books", "Redis", "Java", "Spring", "Git");

        p.sadd("books_set", "Java");
        p.sadd("books_set", "Rails");
        p.sismember("books_set", "Java");

        List<Object> results = p.syncAndReturnAll();
        assertEquals("OK", results.get(0));
        assertEquals("Hao Deng", results.get(1));
        assertEquals(4L, results.get(2));
        assertEquals(1L, results.get(3));
        assertEquals(1L, results.get(4));
        assertTrue((Boolean)results.get(5));
    }

    @Test
    public void testPublishSubscribe() throws InterruptedException {
        final CountDownLatch messageReceivedLatch = new CountDownLatch(3);
        final CountDownLatch publishLatch = new CountDownLatch(1);
        final Set<String> messages = new HashSet<String>();

        new Thread(new Runnable() {
            public void run() {
                Jedis jSub = new Jedis("192.168.99.100", 6379);
                jSub.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        // handle message
                        System.out.println(message);
                        messages.add(message);
                        messageReceivedLatch.countDown();
                    }
                }, "channel");
                jSub.quit();
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                Jedis jPublisher = new Jedis("192.168.99.100", 6379);
                try {
                    publishLatch.await();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }

                jPublisher.publish("channel", "test message1");
                jPublisher.publish("channel", "test message2");
                jPublisher.publish("channel", "test message3");

                jPublisher.quit();
            }
        }).start();


        publishLatch.countDown();
        messageReceivedLatch.await();

        assertEquals(3, messages.size());
        assertTrue(messages.contains("test message1"));
        assertTrue(messages.contains("test message2"));
        assertTrue(messages.contains("test message3"));
    }
```
