package li.entrix.javafx.shape;

import javafx.geometry.Point2D;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.function.Function;

public class CustomPhantomReference<T> extends PhantomReference<T> {

    private Runnable f;

    /**
     * Creates a new phantom reference that refers to the given object and
     * is registered with the given queue.
     *
     * <p> It is possible to create a phantom reference with a <tt>null</tt>
     * queue, but such a reference is completely useless: Its <tt>get</tt>
     * method will always return null and, since it does not have a queue, it
     * will never be enqueued.
     *
     * @param referent the object the new phantom reference will refer to
     * @param q        the queue with which the reference is to be registered,
     */
    public CustomPhantomReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

    public CustomPhantomReference(Runnable f, T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
        this.f = f;
    }

    @Override
    public void clear() {
        f.run();
        f = null;
        super.clear();
    }

}
