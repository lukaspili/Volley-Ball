package com.siu.android.volleyball.samples.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.local.LocalRequest;
import com.siu.android.volleyball.samples.database.EntryDao;
import com.siu.android.volleyball.samples.model.Entry;

import java.util.List;

/**
 * Created by lukas on 8/29/13.
 */
public class EntryRequest extends LocalRequest<List<Entry>> {

    private static final Gson sGson = new Gson();

    /**
     * In this sample we mock the request so we don't care about the url
     */
    private static final String URL = "http://some.url.com/entries?bla=foobar";

    public EntryRequest(BallResponse.ListenerWithLocalProcessing<List<Entry>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, URL, listener, errorListener);
    }


    /*
        Network processing
        We override those methods related to the network processing of the request in the same way we would do with the Request class
        The only difference is the implementation of parseBallNetworkResponse rather than parseNetworkResponse because of some shitty constraints
    */

    @Override
    protected BallResponse<List<Entry>> parseBallNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            List<Entry> entries = sGson.fromJson(json, new TypeToken<List<Entry>>() {}.getType());
            return BallResponse.success(entries, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return BallResponse.error(new ParseError(e));
        }
    }


    /*
        Local processing
        And now comes the new part, the local processing
    */

    @Override
    protected List<Entry> getLocalResponseContent() {
        List<Entry> entries = EntryDao.getEntries();
        return entries.isEmpty() ? null : entries; // we return null if entries list from database is empty to tell volleyball to ignore this
                                                   // local response, we may have more chance with the local cache response.
                                                   // this can happen if for example the database is deleted for some reason but disk cache is still there
    }

    @Override
    public void saveLocalResponse(List<Entry> entries) {
        EntryDao.replaceAll(entries);
    }
}
