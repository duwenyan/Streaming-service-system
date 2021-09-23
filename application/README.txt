The applications were compiled and packed in executable JAR files. They are composed of two parts, the server and the client. The detailed instructions for running the application are as follows.

(1) Run server

The following files are required in the directory where server.jar is located:
lib/sqlite-jdbc-3.34.0.jar (it provides support for SQL database)
server.config (it provides the port and encryptionkey)

The server can be run in a command line interface with following command:
java -jar server.jar
The database folder will be created automatically. If you want to empty the database, delete the database folder and restart the server.

(2) Run client

The following file is required in the directory where client.jar is located:
client.config (it provides the server ip and port)

The client can be run in a command line interface with following command:
java -jar client.jar
If the required JAVA environment is set up, you can run it by double-clicking the JAR file.

(3) Compiling

The existing JAR files were compiled and packed using JDK 16. They can be recompiled by run makejar.bat(Windows) or makejar.sh(Linux) in the source code folder. The JAR file will be automatically generated.