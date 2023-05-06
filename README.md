1) build server:
`javac -d bin ./src/server/Server.java`

2) build client
`javac -d bin ./src/client/Client.java`

3) run server
`java -cp bin src.server.Server 3020`

4) run Client
`java -cp bin src.client.Client localhost 3020`


compile & run server 
`javac -d bin ./src/server/Server.java ; java -cp bin src.server.Server 3020`

compile & run client 
`javac -d bin ./src/client/Client.java ; java -cp bin src.client.Client localhost 3020`