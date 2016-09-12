# Example application using play 2.5 and reactive couchbase

# Configure Couchbase

Modify the data in application.conf
```
couchbase {
  buckets = [
    {
      host = "192.168.99.100"
      port = "8091"
      base = "pools"
      bucket = "default"
      user = ""
      pass = ""
      timeout = "0"
    }
  ]
}
```

# Run it
```
sbt run
```
# Films

Film controller handles film related data using the play-json framework

## Add films

```
curl -H "Content-Type: application/json;charset=UTF-8" -X POST -d '{"isbn": "tt0470752", "name": "Ex Machina", "release": 2015}' http://localhost:9000/film
curl -H "Content-Type: application/json;charset=UTF-8" -X POST -d '{"isbn": "tt1139797", "name": "Låt den rätte komma", "release": 2008}' http://localhost:9000/film
```

## Search films by a field

```
curl http://localhost:9000/film?isbn=tt0470752
```

