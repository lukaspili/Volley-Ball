# Volley Ball

Introducing Volley Ball for Android, an extension library built on top of Volley. For those who didn't hear about Volley, it's a networking library used by Google engineers in their Android apps and presented to public during the Google IO 2013. You can find more about Volley in the Google IO talk: <http://www.youtube.com/watch?v=yhv8l9F44qo>.

## A quick introduction

### Perform local and network request in parallel

Perform the request in your activity / fragment :

```java
mRequestQue.add(new SampleRequest(Request.Method.GET, "http://some.url", new ResponseListener<Object>() {
    @Override
    public void onIntermediateResponse(Object response, BallResponse.ResponseSource responseSource) {
        // intermediate response, such as from local database or soft cached network response
    }

    @Override
    public void onFinalResponse(Object response, BallResponse.ResponseSource responseSource) {
        // final response, which is the network response
    }

    @Override
    public void onFinalResponseIdenticalToIntermediate(BallResponse.ResponseSource responseSource) {
        // final response is identical to intermediate one
        // happens when intermediate is from soft cache and network response is identical (not modified)
    }

}, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        // network response is an error, in the same way than with volley
    }
}
));
```


And the request class looks like :

```java
public class SampleRequest extends CompleteRequest<Object> {

    public SampleRequest(int method, String url, ResponseListener<Object> responseListener, Response.ErrorListener errorListener) {
        super(method, url, responseListener, errorListener);
    }

    @Override
    protected Object getLocalResponse() {
        // query your local database for example
        // return the result or null if there is no result from database
        return new Object();
    }

    @Override
    public void saveNetworkResponseToLocal(Object response) {
        // save the network response to the local database
        // next time the request is performed the local response will return the result faster than the network request
    }

    @Override
    protected BallResponse<Object> parseBallNetworkResponse(NetworkResponse response) {
        // parse the result from the network request, in the same way than with volley
        return Response.success(new Object());
    }
}
```


### Perform network-only and/or local-only request

You can still use Volleball the same way you would use Volley, with a network-only request and exact same features (cache and stuff).
You need to extend NetowkrRequest for that :

```java
public class MyNetworkRequest extends NetworkRequest<String> {

    public MyNetworkRequest(SingleResponseListener<String> listener, ErrorListener errorListener) {
        super(Method.GET, "some.url.com", listener, errorListener);
    }

    @Override
    protected BallResponse<String> parseBallNetworkResponse(NetworkResponse response) {
        return BallResponse.success("some response that you would parse from response.data", HttpHeaderParser.parseCacheHeaders(response));
    }
}
```


You can even use Volleyball to perform non-networking task outside of UI thread, using the powerful thread dispatching of Volley.
You need to extend LocalRequest for that. As you can see, the main difference is that the local request does not have a HTTP method, a url or an error listener. It's up to you to catch any exception and return an adequate result.
One of the purpose of this kind of request is to query your local database outside of UI thread, which is always a good practice.

```java
public class SampleLocalRequest extends LocalRequest<String> {

    public SampleLocalRequest(ResponseListener<String> responseListener) {
        super(responseListener);
    }

    @Override
    public String performLocal() {
        // outside of UI thread
        return "something if you want";
    }
}
```


For either network-only or local-only request, you run the request in the following way, with a simplified response listener:

```java
// network request
NetworkRequest request = new SampleNetworkRequest(Request.Method.GET, "some.url.com", new SingleResponseListener<String>() {
    @Override
    public void onResponse(String response) {
        SimpleLogger.d("response from request %s", response);
    }
}, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        SimpleLogger.d("error from request %s", error.getMessage());
    }
});
Application.getRequestQueue().add(request);


// local request that returns something
LocalRequest localRequest = new SampleLocalRequest(new SingleResponseListener<String>() {
    @Override
    public void onResponse(String response) {
        SimpleLogger.d("response from request %s", response);
    }
});
mRequestQueue.add(localRequest);


// local request that returns nothing
LocalRequest localRequestWithoutResult = new SampleLocalNoResultRequest();
mRequestQueue.add(localRequestWithoutResult);
```

### Samples

You can checkout the samples project for the full code source, and specifically the following three activities:

- CompleteRequestActivity
- NetworkRequestActivity
- LocalRequestActivity


## The deeper explanation

### Why Volley Ball ?

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


### Different scenarios

Volley Ball introduces concurrency with parallel worker threads, something the original Volley library does not have (basically, it's a sequential process of: request -> cache -> maybe response -> network -> response). With Volley Ball we have the local worker thread and the cache/network one running in parallel, which result in a more complicated logic.

Let's see the possible scenarios when running a volley ball request and its implications. These scenarios are tested in working conditions in the sample project.


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

Scenario 7

1. Start the request
2. Cache thread hits soft cache -> post an intermediate response
3. Local thread returns valid response -> intermediate response ignored
4. Network thread returns valid response -> post a final response
5. End 


Scenario 8 (points 2 and 3 can be inverted)

1. Start the request
2. Local thread returns valid response -> post an intermediate response
3. Cache thread hits soft cache -> intermediate response ignored
4. Network thread returns 304 response not modified -> post a final identical response
5. End 

Scenario 9

1. Start the request
2. Cache thread misses
3. Network thread returns error response  -> wait for local response
4. Local thread returns valid response -> post an intermediate response
5. Post the network error afterwards
6. End

Scenario 10

1. Start the request
2. Cache thread misses
3. Network thread returns error response  -> wait for local response
4. Local thread returns empty response -> post the network error response
5. End




### Logs

Enable detailled request logs in the same way than with Volley : `adb shell setprop log.tag.Volley VERBOSE`


### Structure

Volley Ball is built on top of Volley, which is included as a git submodule from <https://android.googlesource.com/platform/frameworks/volley>.
Sadly, Volley uses several times the private and default scopes which obligated me to copy past some pieces of code. It's documented in the source code.
Current version of the Volley sub module: 4c2fe13.


### Tests

You can run the tests with the command line: `./gradlew library:unitTest`



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
- marker log constant methods
- local request final ?