package com.siu.android.volleyball.local;

import com.siu.android.volleyball.BallRequest;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.BallResponseDelivery;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.BlockingQueue;

/**
 * Created by lukas on 9/1/13.
 */
@RunWith(RobolectricTestRunner.class)
public class LocalDispatcherTest {

    private LocalDispatcher mLocalDispatcher;
    private BlockingQueue<BallRequest> mRequestQueue;
    private BlockingQueue<BallRequest> mNetworkQueue;
    private BallResponseDelivery mResponseDelivery;
    private LocalRequestProcessor mLocalRequestProcessor;
    private BallRequest mRequest;
    private BallResponse mResponse;

    @Before
    public void setUp() throws Exception {
        mRequestQueue = Mockito.mock(BlockingQueue.class);//new PriorityBlockingQueue<BallRequest>();
        mNetworkQueue = Mockito.mock(BlockingQueue.class);
        mResponseDelivery = Mockito.mock(BallResponseDelivery.class);
        mLocalDispatcher = new LocalDispatcher(mRequestQueue, mNetworkQueue, mResponseDelivery);

        mLocalRequestProcessor = Mockito.mock(LocalRequestProcessor.class);
        mRequest = Mockito.mock(BallRequest.class);
        mResponse = Mockito.mock(BallResponse.class);

        // request queue returns the mock request object
        Mockito.when(mRequestQueue.take()).thenReturn(mRequest);

        // request not canceled
        Mockito.when(mRequest.isCanceled()).thenReturn(false);

        Mockito.when(mRequest.getLocalRequestProcessor()).thenReturn(mLocalRequestProcessor);
    }

    @Test
    public void shouldDoNothingWhenRequestIsCanceled() throws Exception {
        // request canceled
        Mockito.when(mRequest.isCanceled()).thenReturn(true);

        Assertions.assertThat(mLocalDispatcher.dispatch()).isEqualTo(true);
        Mockito.verify(mRequestQueue).take();
        Mockito.verify(mRequest).addMarker("local-queue-take");
        Mockito.verify(mRequest).finish("local-discard-canceled");
        Mockito.verifyZeroInteractions(mResponseDelivery);
    }


    @Test
    public void shouldPostNoResponseWhenContentIsNull() throws Exception {
        // local request processor returns null reponse
        Mockito.when(mLocalRequestProcessor.getLocalResponse()).thenReturn(null);

        Assertions.assertThat(mLocalDispatcher.dispatch()).isEqualTo(true);
        Mockito.verify(mRequestQueue).take();
        Mockito.verify(mRequest).addMarker("local-queue-take");
        Mockito.verify(mRequest).addMarker("local-response-content-null-exit");
        Mockito.verify(mResponseDelivery).postEmptyIntermediateResponse(mRequest, BallResponse.ResponseSource.LOCAL);
        Mockito.verifyNoMoreInteractions(mResponseDelivery);
    }

    @Test
    public void shouldPostResponse() throws Exception {
        // the response
        final Object responseContent = new Object();

        // local request processor returns non null reponse
        Mockito.when(mLocalRequestProcessor.getLocalResponse()).thenReturn(responseContent);

        Assertions.assertThat(mLocalDispatcher.dispatch()).isEqualTo(true);
        Mockito.verify(mRequestQueue).take();
        Mockito.verify(mRequest).addMarker("local-queue-take");
        Mockito.verify(mRequest).addMarker("local-response-get-content-successful");
        Mockito.verify(mResponseDelivery).postResponse(Mockito.eq(mRequest), Mockito.argThat(new ArgumentMatcher<BallResponse<?>>() {
            @Override
            public boolean matches(Object argument) {
                if (argument instanceof BallResponse ) {
                    BallResponse response = (BallResponse) argument;
                    // response should be local and intermediate
                    if (response.getResponseSource() == BallResponse.ResponseSource.LOCAL &&
                            response.isIntermediate() && response.getResult().equals(responseContent)) {
                        return true;
                    }
                }

                return false;
            }
        }), Mockito.notNull(Runnable.class));
        Mockito.verifyNoMoreInteractions(mResponseDelivery);
    }

//    @Test
//    public void testTwo() throws Exception {
//        Assertions.assertThat(mRequestQueue.take()).isEqualTo(new BallRequestMock());
//    }
}
