# Volley Ball

Introducing Volley Ball for Android, an extension library built on top of Volley. For those who didn't hear about Volley, it's a networking library used by Google engineers in their Android apps and presented to public during the Google IO 2013.

You can find more about Volley in the Google IO talk: <http://www.youtube.com/watch?v=yhv8l9F44qo>.


## Why Volley Ball ?

The Android API and its ecosystem offer a lot of great libraries for working with background threads and networking task: AsyncTasks, Loaders, Robospice, Retrofit and so on. Volley is great for performing network tasks very easily. However, none of these libraries offer a full support for a very common use case we developers encounter while making Android apps and working with some web service (REST or not): Display some data from www.foobar.com/api/data (in whatever format we want).

The very first request of the app will look like:

1. Show the activity / fragment
2. Make a network request in a background thread on www.foobar.com/api/data
3. Parse the response
4. Call the UI thread with the data and display it


Volley works great with this process. But the next requests will look more like:

1. Show the activity / fragment
2. Make a local database request to get the data associated to www.foobar.com/api/data stored on the device (content available offline). In a background thread of course.
3. Make a network request in another background thread on www.foobar.com/api/data
4. The request to local database will often finish first. Call the UI thread with the data retreived from local database and display it.
5. The network request finish next. Call the UI thread with the data retreived from network and display it.


Volley do offer a local cache of requests out of the box, but:
- It only caches the raw data retreived from the web service, so we still have to parse it and stuff.
- If we want to use the data somewhere else in the app without making another request, we can't.
- There are always some cases where we don't have the control over the webservice API and the cache header are wrong.

In summary, even if there are some workarounds to fix these drawbacks, it's often more convinient to have our data stored in a local database. Here comes the main use case of Volley Ball, it provides a local request processing along with the network one.

PICS


In Volley Ball, a request is composed of two kind of "sub" request:

- Local request: Request from local database OR from network cache. These two requests are executed in parallel, and it's important to understand that only the first that finishes (the fastest) returns to the UI thread. The second one is ignored, because obviously both requests will return the same content.
- Network request.


## Different scenarios

Volley Ball introduces concurrency with parallel worker threads, something the original Volley library does not have (basically, it's a sequential process of: request -> cache -> maybe response -> network -> response). With Volley Ball we have the local worker thread and the cache/network one running in parallel, which result in a more complicated logic. Let's see the possible scenarios when running a volley ball request and its implications.

NB1: Each time we start a request, the request queue dispatches it to the two following worker thread that will run in parallel:

- The local worker thread
- The cache thread OR the network worker. We have 3 possibilities:
	- The cache thread returns the response if there is a cache hit (network thread ignored).
	- OR
	- The cache thread forward to the network thread if there is a cache miss.
	- OR
	- The cache thread is ignored and the network thread is called directly if the request cannot cache.
	
NB2: Only the first intermediate and valid response is taken in account, either from local or from cache.

Scenario 1

1. Start the request
2. Local thread returns valid response -> post an intermediate response
3. Cache thread hits and returns a valid response -> post a final response
4. End

Scenario 2

1. Start the request
2. Cache thread hits and returns a valid response -> post a final response
3. Local thread returns valid response -> ignored
4. End

Scenario 3

1. Start the request
2. Cache thread misses
3. Network thread returns valid response -> post a final response
4. Local thread returns valid response -> ignored
5. End

Scenario 4 (points 2 and 3 can be inverted)

1. Start the request
2. Local thread returns valid response -> post an intermediate response
3. Cache thread misses
4. Network thread returns valid response -> post a final response
5. End 

Scenario 5 (points 2 and 3 can be inverted)

1. Start the request
2. Local thread returns valid response -> post an intermediate response
3. Cache thread misses
4. Network thread returns error response -> post an error response
5. End

Scenario 6 (points 2 and 3 can be inverted)

1. Start the request
2. Local thread returns valid response -> post an intermediate response
3. Cache thread hits soft cache -> intermediate response ignored
4. Network thread returns valid response -> post a final response
5. End 


Soft cache // 304 response

Scenario 7 

1. Start the request
2. Local thread returns valid response -> post an intermediate response
3. Cache thread hits soft cache -> intermediate response
4. ignored
4. Network thread returns 304 response not modified -> post a final identical response
5. End 


Scenario 7

1. Start the request
2. Local thread returns valid response -> post an intermediate response
3. Cache thread hits soft cache -> intermediate response ignored
4. Network thread returns 304 response not modified -> post a final identical response
5. End 


Scenario 8 (points 2 and 3 can be inverted)

1. Start the request
2. Local thread returns valid response -> post an intermediate response
3. Cache thread hits soft cache -> intermediate response ignored
4. Network thread returns valid response  -> post a final response
5. End 


Scenario 1

1. Start the request


Scenario 1

1. Start the request


Scenario 1

1. Start the request






## Misc

Enable detailled request logs in the same than with Volley : `adb shell setprop log.tag.Volley VERBOSE`




## TODO

- check the request cancelation
	- what happens when request is finished from local dispatcher for example ?
- local request only
- several worker threads for local dispatcher
- compatibility for adding volley request to the ball queue
- marker log finished
    - finished volatile ?
    - what happen if request finished from one thread but adding marker on another
- cancel, finish from several parallel work threads
    - what to do
- test coverage (0% so far)
- scenario 7-8 soft cache request has been delivered shouldn't be volatile ?
- request finished volatile -> finished only from executor
- cancellation 
- local and network only request