Memcache-start:
	brew services start memcached
Memcache-restart:
	brew services restart memcached
Memcache-stop:
	brew services stop memcached
Start-app:
	java -jar target/dispatchbuddy-0.0.1-SNAPSHOT.jar fully.qualified.package.Application
