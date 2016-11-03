This is the homework given at Adobe (Romania) as first hiring step.


BEWARE: I did not pass the test because:
- accept is not syncronized. On some operating systems accept is already syncronized
and can be safely called from multiple threads. However to be sure to add another mutex ?

- there are supposedly Null Pointer Exceptions on each request. I did not manage to
reproduce this issue.

- The HTTP requests which are not implemented. I implemented GET, POST, PUT, DELETE.
The client doesn't receive any response with an error code and keeps loading forever.
I supposed this was acceptable as the requirement didn't said anything about them.

- If the request is not valid the client doesn't receive any response and there are
exceptions on the server. These however don't stop the application.



My review of the Adobe hiring procedure:

- they gave me half a page requirement and had too much expectations from a simple
homework.
- don't invest too much time in it. They have too many demands from candidates and
there are high chances you won't pass this step. There are a lot of candidates, 
number of opened positions are scarce. From my previous experience with corporations
they even close the positions after they publish them as open, because their 
American counterparts demand them so.


This hiring process brought me close to the conclusion that I hate corporations.
They are only intrested in obtaining profit with a minimum of investion and they are
reluctant in investing in developing countries, especially if the market salary 
requests become higher. This are the rules of capitalism, but this doesn't make
me hate them less.


I was quite bothered by the fact that I worked a lot for this homework, they gave
me 2 weeks deadline to work on it. So I thought I had to invest more time in it.
After I sent it they bothered to answer only after 2 weeks and then with a negative
response without any feedback. So I wasn't even asked to come to an interview. 


In case you manage to pass the full recruitment process be sure to accept a worthy
sallary offer. 


Good luck.




===============================================================================


Adobe hiring homework


To start the application you need to run in the root of the project:

java -cp bin/ server.Server

The web server will start on port 8000 and can be accessed in the browser. I
tried both Chrome and Firefox and they work fine.

The project can be also opened in eclipse. This is the IDE I used.

Next I will explain the implementation of my solution.

A. A multi-threaded (file-based) web-server with thread pooling in Java.

The main class is located in the Server.java file. When the application is
started a socket is created listening for connections on the configured port. 
By default this is 8000. Then multiple threads are created which are accepting
connections in parallel (class Connection). The same threads are running right
from the start until the server is manually stopped by the user. In the default
configuration 10 threads are started and will accept connections from incoming
clients. Having the threads right from the start eliminates the overhead of
creating threads for each new client. I also tought there is no need to start
with one thread and go up to 10 (or the maximum configured) threads as
connections count keep growing. Because once I started one thead I didn't want to
stop them anymore. Recreating them every time just added overhead time. Also
the number of threads can be easily configured from a properties file.

The run method contains a while (true) loop which will accept connection 
indefinetly. When an incoming connection is found the accept() method will
return. Then the request will be read, interpreted and a response will be given
accordingly. After a request is read the timeout of the socket will be set to
15000 ms (15 seconds) and only then it will disconnect from the client. In this
manner keep-alive behaviour is implemented. So the client might send another 
request frame without the need to initialise a new connection (by entering the
accept method). Time is saved and the server responses faster to an already
connected client. If 15 seconds pass a new connection will be initialised.

The HTTP server contains the following methods implemented:

- GET
- POST
- PUT
- DELETE

For the GET method only the header of the HTTP request is parsed. The path of
the request represents the path of a file located on file disk. The effect of
this request will be reading from the given file and send a HTTP response with
the content set to the content of the file. In case the file specified in the 
request is not found a 404 response will be returned.

When parsing the POST request the path of the request will also represent the
location of the file relative to the root directory. The content of the request
will be written in the file. The same behaviour will be reproduced if the file
is not found: a 404 response will be given.

The PUT request is very similar to the POST method. The only difference is that
the file will be created if it doesn't exist.

The DELETE method will delete the file at the given path. If not found it will
return again a 404 response.

Since all the requests manipulate files and they can be accessed from multiple
threads there is a class which contains a syncronization mechanism. The FileLocks
class contains a map with <filepaths, sempahore> associations. For each filepath
there is a semaphore (with the initial value of 1). A single instance of this 
class is created and is given to all the Connection threads. So for instance if
there is a write request in a thread for a file, and another for a read request
the operation will be executed sequentially depending on which thread will
acquire the mutext(semaphore with value 1) first. This shouldn't represent
however a bottle-neck of the server application since request path vary a lot
for each client.

The threads created when starting the server are checked from time to time to
see if they are still alive. In case one thread is not alive anymore a new one
will be created to replace it. Exceptions are handled correctly in the Connection
threads, so this acts as a last fail-safe mechanism.

The maximum number of connections is not limited to the number of threads 
created when starting the application. But it's limited to the incoming connection
queue length given to the ServerSocket contructor. The second argument called
backlog ensures this. 

The files are read and written as binary files so any kind of files work. To test
this I created a usage scenario in which you can set the image of a digital photo
frame. 

The MIME type of the response is set by reading it using the Files.probeContentType().
From what I've read it's not working in all the cases. But for what I tried it
works well enough.

To be able to accept form-data request I had to detect the boundary used in this
kind of request. The boundary is specified in the header and specifies the limits
of a file inside the http request body.

The server can be configured from the file config/server.properties. Here the
following parameters can be changed:

- root - represent the location of the web application run on the web server
- numThreads - the number of threads in the thread pool
- port - the port on which the server will start
- maxConnections - length of the connection queue

B. A javascript application simulation house automation

Using the provided methods by the server I created a single page web application
which allows the users to turn off some lights, to change the temperature in a
room, to close or open the curtains and even set the image for a digital photo
frame. 

For the design part of the application I used the Bootstrap engine. I also
imported the bootstrap-toggle library which implements a toggle for the user,
which is visually better then the traditional radio button.

For manipulation the DOM and sending ajax requests to the server I used jquery.
The entire application is located in the index.html file. Elements are hidden
or shown depending on what tab/button the user presses. For example if a
user selectes the lights category he will see only the toggles for the lights.

The state of the sensors (lights, temperature, curtains) are kept in json format.
So they will be saved as .json files on the server. I chose this format because
it need no conversion and can be directly manipulated in javascript.

For example the light is stored on the server like this:

{"name":"light1","on":true} in a file called sensors/light1.json

The temperature sensors information is stored in a similar json this way:

{"name":"temperature1","value":"21.7"} in a file called sensors/temperature1.json


To read the values of an existing sensors I make GET request. To change the value
I make POST request using the post and get methods from jquery. Both are wrappers
over specific $.ajax calls.


In the case of the image the situation is similar, but instead of a JSON I
just send the request created by a form html element. To avoid to be redirected
to the path of the file right after submitting the form i created a jquery handler
which prevents the form from actually submitting (e.preventDefault()) and does
an ajax request with the content of the form. Once a response is given the src
of the image displayed in the browser is refreshed (this hover fails to work well
in firefox, probably because it has the image cached. The image is however updated
after a page refresh).

In the case of the image i use the PUT request as the web application might not
contain any file initially. So it will create it with the given name if this is
the case. 

Also I added the feature of deleting the exiting picture. This is done by
creating a an ajax DELETE request.

In case a new sensor or any type of resource needs to be added it's needed just
to create a json file on the server. And by using GET and POST requests it can
be manipulated directly.



===============================================================================


The original requirement:

In order to assess your programming skills and web technology knowledge, we ask
you to provide the following:
A. A multi-threaded (e.g. file-based) web server with thread-pooling
implemented in Java.
Extension: Add proper HTTP/1.1 keep-alive behavior to your implementation
based on the http-client's capabilities exposed through its request headers.
B. A JavaScript application simulating house automation: pressing a button on
a control panel would visually turn on a light, change the temperature or
close the curtains. Some constraints:
• the application must use jQuery
• the components must interact with static server resources (e.g. the
heating component retrieves the current temperature from the server and also
sends the desired one back to the server)
• the solution has to be extensible and documented, so that we can
develop our own components that react to events
The application will be executed on a plain HTTP server with no possibility to run
code server side and is being viewed in 2 major browsers of your choice.
General rules
The extension is optional unless requested (see below).
Please note that Adobe has considerable experience with publicly available
examples as we have been using this assessment for many years. We realize that
many implementations exist on the Internet and are very familiar with them.
We will not hold copying existing source code against you since in real-life
leveraging existing code may, at times, be the best and quickest way to get a good
result. Be aware though that not all publicly available solutions are of good code
quality and we will consider your choice as part of the assessment.
We will also not hold it against you if you choose to write the entire example from
scratch.
If you do decide to copy code from third party resources, please consider the
following when submitting your code:

Declare which parts of the sample is your own code

Reference all copied code and where you took it from: do not remove
copyrights or comments. Any violation of copyrights or obfuscation tactics
will reflect negatively on your assessment.

If after reviewing your example we do not feel we have enough of your own
code to evaluate, we may ask you to add the extension.
Please provide us with a link from where we can download your solutions (you
may for example use https://sendnow.acrobat.com/). For technical reasons we
cannot accept email attachments.
