package alifec.core.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CallableImpl implements Callable<Boolean> {

    private final Event event;
    private final List<Listener> listeners;
    private Logger logger = LogManager.getLogger(getClass());

    public CallableImpl(List<Listener> listeners, Event event) {
        this.listeners = listeners;
        this.event = event;
    }

    @Override
    public Boolean call() throws Exception {
        for (Listener listener : listeners) {
            try {
                listener.handle(event);
            } catch (Throwable ex) {
                logger.error(ex);
            }
        }
        return Boolean.TRUE;
    }
}
