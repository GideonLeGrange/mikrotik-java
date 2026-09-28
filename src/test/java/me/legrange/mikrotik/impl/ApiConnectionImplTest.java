package me.legrange.mikrotik.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import me.legrange.mikrotik.MikrotikApiException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ApiConnectionImplTest {

    @Test(timeout = 1500)
    public void synchronousErrorCompletesImmediatelyAndPreservesOriginalError() throws Exception {
        Class<?> listenerClass = Class.forName("me.legrange.mikrotik.impl.ApiConnectionImpl$SyncListener");
        Constructor<?> constructor = listenerClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object listener = constructor.newInstance();

        Method error = listenerClass.getDeclaredMethod("error", MikrotikApiException.class);
        error.setAccessible(true);
        Method getResults = listenerClass.getDeclaredMethod("getResults", Integer.TYPE);
        getResults.setAccessible(true);

        error.invoke(listener, new MikrotikApiException("unknown parameter"));

        long start = System.nanoTime();
        Throwable thrown = null;
        try {
            getResults.invoke(listener, 1000);
            fail("Expected MikrotikApiException");
        } catch (InvocationTargetException ex) {
            thrown = ex.getCause();
        }
        long elapsedMs = (System.nanoTime() - start) / 1000000L;

        assertTrue("Expected MikrotikApiException", thrown instanceof MikrotikApiException);
        assertEquals("unknown parameter", thrown.getMessage());
        assertTrue("Error handling waited " + elapsedMs + " ms", elapsedMs < 500L);
    }
}
