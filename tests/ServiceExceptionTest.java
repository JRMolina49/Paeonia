import static org.junit.Assert.*;
import org.junit.Test;

/** Checks service error messages and cause preservation. */
public class ServiceExceptionTest
{
    @Test
    public void preservesMessageAndCause()
    {
        RuntimeException cause = new RuntimeException("offline");
        ServiceException error = new ServiceException("Unavailable", cause);
        assertEquals("Unavailable", error.getMessage());
        assertSame(cause, error.getCause());
        assertEquals("Unavailable", new ServiceException("Unavailable").getMessage());
        assertNull(new ServiceException(null).getMessage());
        assertNull(new ServiceException(null, null).getCause());
    }
}
