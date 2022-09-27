import io.javalin.http.Context;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class DaoTest {

    @Test
    public void GET_get_list_of_users() {
        Context ctx = mock(Context.class);
        when(ctx.queryParam("userId")).thenReturn("Amaury");
        verify(ctx).status(200);
    }
}
