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
public enum EventBus {

    instance;

    private static Logger logger = LogManager.getLogger(EventBus.class);
    private List<Listener> listeners = new ArrayList<>();
    private ExecutorService executor;
    private boolean singleThread;

    public static void register(Listener listener) {
        instance.listeners.add(listener);
    }

    public static void unregister(Listener listener) {
        instance.listeners.remove(listener);
    }

    public static void post(Event event) {
        for (Listener listener : instance.listeners) {
            try {
                if (instance.singleThread) {
                    listener.handle(event);
                } else {
                    instance.getExecutor().submit(() -> listener.handle(event));
                }
            } catch (Throwable ex) {
                logger.error(ex);
            }
        }
    }

    private ExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    public static void setSingleThread() {
        if (instance.executor == null)
            instance.singleThread = true;
    }

    public static void reset() {
        instance.listeners.clear();
    }
}
