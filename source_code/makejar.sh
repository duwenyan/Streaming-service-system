javac server/*.java
javac client/*.java
jar -cvmf manifest_server.txt server.jar server
jar -cvmf manifest_client.txt client.jar client
