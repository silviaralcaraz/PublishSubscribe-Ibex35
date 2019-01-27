# Distributed Computing - Practice 3
 
 ## Model Public-Subscribe
 
 Application that use the model Publish-Subscribe to get and generate alarms on the values of Ibex 35.

 The application use data from  http://www.bolsademadrid.es but, if this site is down, they will be extrated of the static page "page.html".

 **Features**
- Generate alarms (you must indicate the name of the company in uppercases, the value of the alarm and the type of the alarm: buy or sell).
- Delete alarms (same indications)
- See your alarms 
 
 **Execution:**
 
 *Note: firsly, run the server and then, the client.* 
 
 
 - To run the server:
     
         $ cd /out/artifacts/Server_jar
         $ java -jar Server_p3.jar
         
         Then, choose a port number. For example: 2222.

 - To run the client:
    
        $ cd /out/artifacts/Client_jar
        $ java -jar Client_p3.jar
        
        Then, choose "localhost" as host name and select the same number
        port that chose to the server.
