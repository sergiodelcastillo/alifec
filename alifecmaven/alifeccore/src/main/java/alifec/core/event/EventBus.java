package alifec.core.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class EventBus {
    private Logger logger = LogManager.getLogger(getClass());
    private List<Listener> listeners = new ArrayList<>();
    private ExecutorService executor;

    private static EventBus instance;

    public static EventBus get() {
        if (instance == null)
            instance = new EventBus();

        return instance;
    }

    public EventBus() {
        executor = Executors.newFixedThreadPool(1);
    }

    public void register(Listener listener) {
        listeners.add(listener);
    }

    public void unregister(Listener listener) {
        listeners.remove(listener);
    }

    public void post(Event event) {
        executor.submit(new CallableImpl(listeners, event));
    }

}
