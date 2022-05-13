const redis = require("redis");
const client = redis.createClient({url: process.env.REDIS_URL});



client.on("connect", function() {
    console.log("You are now connected");
  });