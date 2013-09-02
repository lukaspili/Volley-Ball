package com.siu.android.volleyball.mockito;

import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.invocation.Invocation;
import org.mockito.verification.VerificationMode;

import java.util.List;

/**
 * Created by lukas on 9/2/13.
 */
public class LastInteraction implements VerificationMode {

    @Override
    public void verify(VerificationData data) {
        List<Invocation> invocations = data.getAllInvocations();
        InvocationMatcher matcher = data.getWanted();
        Invocation invocation = invocations.get(invocations.size() - 1);

        if (!matcher.matches(invocation)) {
            throw new MockitoException("This is not the last interaction with the mock object");
        }
    }
}
