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
