package alifec.core.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private List<Listener> listeners = new ArrayList<>();
    private Logger logger = LogManager.getLogger(getClass());

    private static EventBus instance;

    public static EventBus get() {
        if (instance == null)
            instance = new EventBus();

        return instance;
    }

    public void register(Listener event) {
        listeners.add(event);
    }

    public void unregister(Event event) {
        listeners.remove(event);
    }

    public void post(Event event) {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.submit(new CallableImpl(listeners, event));
    }

}
