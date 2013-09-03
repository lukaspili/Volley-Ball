package com.siu.android.volleyball.ball;

import com.android.volley.VolleyError;
import com.siu.android.volleyball.BallRequest;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.BallResponseDelivery;
import com.siu.android.volleyball.exception.BallException;
import com.siu.android.volleyball.mockito.LastInteraction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.exceptions.base.MockitoException;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.Executor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by lukas on 9/2/13.
 */
@RunWith(RobolectricTestRunner.class)
public class BallExecutorDeliveryTest {

    private BallResponseDelivery mResponseDelivery;

    private BallRequest mRequest;
    private BallResponse mResponse;

    @Before
    public void setUp() {
        mRequest = mock(BallRequest.class);
        mResponse = mock(BallResponse.class);

        mResponseDelivery = new BallExecutorDelivery(new Executor() {
            @Override
            public void execute(Runnable runnable) {
                runnable.run();
            }
        });
    }


    /* Tests for empty intermediate response delivery */

    @Test
    public void shouldPostEmptyIntermediateResponseAndAddMarkerLog() {
        final BallResponse.ResponseSource responseSource = BallResponse.ResponseSource.LOCAL; // or cache, whatever
        Executor executorMock = mock(Executor.class);
        BallResponseDelivery responseDelivery = new BallExecutorDelivery(executorMock);

        doNothing().when(executorMock).execute(any(Runnable.class));

        responseDelivery.postEmptyIntermediateResponse(mRequest, responseSource);

        verify(mRequest).addMarker(BallExecutorDelivery.MARKER_POST_EMPTY_INTERMEDIATE_RESPONSE);
        verify(executorMock).execute(argThat(new ArgumentMatcher<Runnable>() {
            @Override
            public boolean matches(Object argument) {
                BallExecutorDelivery.EmptyIntermediateDeliveryRunnable r = (BallExecutorDelivery.EmptyIntermediateDeliveryRunnable) argument;
                return r.mRequest == mRequest && r.mResponseSource == responseSource;
            }
        }));

        verifyNoMoreInteractions(mRequest);
    }

    @Test
    public void shouldIgnoreEmptyIntermediateResponseWhenRequestIsFinished() {
        when(mRequest.isFinished()).thenReturn(true);
        when(mRequest.isIntermediateResponseDelivered()).thenReturn(false);

        mResponseDelivery.postEmptyIntermediateResponse(mRequest, BallResponse.ResponseSource.LOCAL);

        verify(mRequest, new LastInteraction()).isFinished();
    }

    @Test
    public void shouldIgnoreEmptyIntermediateResponseWhenIntermediateWasAlreadyDelivered() {
        when(mRequest.isFinished()).thenReturn(false);
        when(mRequest.isIntermediateResponseDelivered()).thenReturn(true);

        mResponseDelivery.postEmptyIntermediateResponse(mRequest, BallResponse.ResponseSource.LOCAL);

        verify(mRequest, new LastInteraction()).isIntermediateResponseDelivered();
    }

    /**
     * Should post final error stored in the request when empty intermediate response is delivered and the final response was
     * already delivered as error
     */
    @Test
    public void shouldPostFinalErrorWhenFromEmptyIntermediateResponseWhenFinalWasAlreadyDelivered() {
        VolleyError error = new VolleyError("Some error");

        when(mRequest.isFinished()).thenReturn(false);
        when(mRequest.isIntermediateResponseDelivered()).thenReturn(false);
        when(mRequest.isFinalResponseDelivered()).thenReturn(true);
        when(mRequest.getFinalResponseError()).thenReturn(error);

        mResponseDelivery.postEmptyIntermediateResponse(mRequest, BallResponse.ResponseSource.LOCAL);

        verify(mRequest).setIntermediateResponseDelivered(true);
        verify(mRequest).deliverError(error);
        verify(mRequest).finish(BallExecutorDelivery.DONE_WITH_INTERMEDIATE_EMPTY_RESPONSE);
    }

    @Test
    public void shouldIgnoreEmptyIntermediateResponseWhenFinalIsNotDelivered() {
        when(mRequest.isFinished()).thenReturn(false);
        when(mRequest.isIntermediateResponseDelivered()).thenReturn(false);
        when(mRequest.isFinalResponseDelivered()).thenReturn(false);

        mResponseDelivery.postEmptyIntermediateResponse(mRequest, BallResponse.ResponseSource.LOCAL);

        verify(mRequest).setIntermediateResponseDelivered(true);
        verify(mRequest, never()).deliverError(any(VolleyError.class));
        verify(mRequest, never()).finish(anyString());
    }


    /* Tests for success or error response delivery */

    @Test
    public void shouldPostResponseAndAddMarkerLog() {
        Executor executorMock = mock(Executor.class);
        BallResponseDelivery responseDelivery = new BallExecutorDelivery(executorMock);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                doThrow(new MockitoException("Runnable should not run"));
            }
        };

        doNothing().when(executorMock).execute(any(Runnable.class));

        responseDelivery.postResponse(mRequest, mResponse);
        responseDelivery.postResponse(mRequest, mResponse, runnable);

        verify(mRequest, times(2)).addMarker(BallExecutorDelivery.MARKER_POST_RESPONSE);

        verify(executorMock).execute(argThat(new ArgumentMatcher<BallExecutorDelivery.ResponseDeliveryRunnable>() {
            @Override
            public boolean matches(Object argument) {
                BallExecutorDelivery.ResponseDeliveryRunnable r = (BallExecutorDelivery.ResponseDeliveryRunnable) argument;
                return r.mRequest == mRequest && r.mResponse == mResponse && r.mRunnable == null;
            }
        }));

        verify(executorMock).execute(argThat(new ArgumentMatcher<BallExecutorDelivery.ResponseDeliveryRunnable>() {
            @Override
            public boolean matches(Object argument) {
                BallExecutorDelivery.ResponseDeliveryRunnable r = (BallExecutorDelivery.ResponseDeliveryRunnable) argument;
                return r.mRequest == mRequest && r.mResponse == mResponse && r.mRunnable == runnable;
            }
        }));
    }

    @Test
    public void shouldIgnoreSecondIntermediateResponse() {
        when(mRequest.isIntermediateResponseDelivered()).thenReturn(true);
        when(mResponse.isIntermediate()).thenReturn(true);
        when(mResponse.getResponseSource()).thenReturn(BallResponse.ResponseSource.LOCAL); // or cache, whatever

        mResponseDelivery.postResponse(mRequest, mResponse);

        verify(mRequest, new LastInteraction()).addMarker(BallExecutorDelivery.MARKER_INTERMEDIATE_RESPONSE_ALREADY_DELIVERED);
    }

    @Test
    public void shouldDeliverIntermediateSuccessResponse() {
        Object response = new Object();
        BallResponse.ResponseSource responseSource = BallResponse.ResponseSource.LOCAL;

        when(mRequest.isIntermediateResponseDelivered()).thenReturn(false);
        when(mResponse.isIntermediate()).thenReturn(true);
        when(mResponse.isSuccess()).thenReturn(true);
        when(mResponse.getResult()).thenReturn(response);
        when(mResponse.getResponseSource()).thenReturn(responseSource); // or cache, whatever

        mResponseDelivery.postResponse(mRequest, mResponse);

        verify(mRequest).setIntermediateResponseDelivered(true);
        verify(mRequest, new LastInteraction()).deliverIntermediateResponse(response, responseSource);
    }

    @Test
    public void shouldDeliverIntermediateSuccessResponseWithPostRunnable() {
        Object response = new Object();
        BallResponse.ResponseSource responseSource = BallResponse.ResponseSource.LOCAL;
        Runnable postRunnable = mock(Runnable.class);

        when(mRequest.isIntermediateResponseDelivered()).thenReturn(false);
        when(mResponse.isIntermediate()).thenReturn(true);
        when(mResponse.isSuccess()).thenReturn(true);
        when(mResponse.getResult()).thenReturn(response);
        when(mResponse.getResponseSource()).thenReturn(responseSource); // or cache, whatever

        mResponseDelivery.postResponse(mRequest, mResponse, postRunnable);

        verify(mRequest).setIntermediateResponseDelivered(true);
        verify(mRequest, new LastInteraction()).deliverIntermediateResponse(response, responseSource);
        verify(postRunnable).run();
    }

    @Test(expected = BallException.class)
    public void shouldThrowWhenIntermediateErrorResponse() {
        when(mRequest.isIntermediateResponseDelivered()).thenReturn(false);
        when(mResponse.isIntermediate()).thenReturn(true);
        when(mResponse.isSuccess()).thenReturn(false);
        when(mResponse.getResponseSource()).thenReturn(BallResponse.ResponseSource.LOCAL); // or cache, whatever

        mResponseDelivery.postResponse(mRequest, mResponse);
    }

    @Test
    public void shouldDeliverFinalSuccessResponse() {
        Object response = new Object();
        BallResponse.ResponseSource responseSource = BallResponse.ResponseSource.NETWORK; // or cache, whatever

        when(mRequest.isIntermediateResponseDelivered()).thenReturn(true);
        when(mResponse.isIntermediate()).thenReturn(false);
        when(mResponse.isSuccess()).thenReturn(true);
        when(mResponse.isIdentical()).thenReturn(false);
        when(mResponse.getResult()).thenReturn(response);
        when(mResponse.getResponseSource()).thenReturn(responseSource); // or cache, whatever

        mResponseDelivery.postResponse(mRequest, mResponse);

        verify(mRequest).setFinalResponseDelivered(true);
        verify(mRequest).deliverFinalResponse(response, responseSource);
        verify(mRequest, new LastInteraction()).finish(String.format(BallExecutorDelivery.MARKER_DONE_WITH_RESPONSE_FROM, mResponse.getResponseSource().toString().toLowerCase()));
    }

    @Test
    public void shouldDeliverFinalIdenticalSuccessResponse() {
        Object response = new Object();
        BallResponse.ResponseSource responseSource = BallResponse.ResponseSource.NETWORK; // or cache, whatever

        when(mRequest.isIntermediateResponseDelivered()).thenReturn(true);
        when(mResponse.isIntermediate()).thenReturn(false);
        when(mResponse.isSuccess()).thenReturn(true);
        when(mResponse.isIdentical()).thenReturn(true);
        when(mResponse.getResult()).thenReturn(response);
        when(mResponse.getResponseSource()).thenReturn(responseSource); // or cache, whatever

        mResponseDelivery.postResponse(mRequest, mResponse);

        verify(mRequest).setFinalResponseDelivered(true);
        verify(mRequest).deliverIdenticalFinalResponse(responseSource);
        verify(mRequest, new LastInteraction()).finish(String.format(BallExecutorDelivery.MARKER_DONE_WITH_RESPONSE_FROM, mResponse.getResponseSource().toString().toLowerCase()));
    }

    @Test
    public void shouldDeliverFinalErrorResponseWhenIntermediateIsDelivered() {
        VolleyError error = new VolleyError("Some error");

        when(mRequest.isIntermediateResponseDelivered()).thenReturn(true);
        when(mResponse.isIntermediate()).thenReturn(false);
        when(mResponse.isSuccess()).thenReturn(false);
        when(mResponse.getError()).thenReturn(error);
        when(mResponse.getResponseSource()).thenReturn(BallResponse.ResponseSource.NETWORK); // or cache, whatever

        mResponseDelivery.postResponse(mRequest, mResponse);

        verify(mRequest).deliverError(error);
        verify(mRequest, new LastInteraction()).finish(String.format(BallExecutorDelivery.MARKER_DONE_WITH_RESPONSE_FROM, mResponse.getResponseSource().toString().toLowerCase()));
    }

    /**
     * Should not deliver final error response when intermediate response is not yet delivered
     * It must rather store the error in the request and wait for the intermediate response to be delivered
     */
    @Test
    public void shouldNotDeliverFinalErrorResponseWhenIntermediateIsNotYetDelivered() {
        VolleyError error = new VolleyError("Some error");

        when(mRequest.isIntermediateResponseDelivered()).thenReturn(false);
        when(mResponse.isIntermediate()).thenReturn(false);
        when(mResponse.isSuccess()).thenReturn(false);
        when(mResponse.getError()).thenReturn(error);
        when(mResponse.getResponseSource()).thenReturn(BallResponse.ResponseSource.NETWORK); // or cache, whatever

        mResponseDelivery.postResponse(mRequest, mResponse);

        verify(mRequest).addMarker(BallExecutorDelivery.MARKER_ERROR_IN_FINAL_RESPONSE_LET_INTERMEDIATE_CONTINUE);
        verify(mRequest, new LastInteraction()).setFinalResponseError(error);
        verify(mRequest, never()).deliverError(any(VolleyError.class));
    }
}
