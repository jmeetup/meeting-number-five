package li.entrix.javafx.shape;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

public class PhantomReferenceCreator implements ReferenceCreator {

    private ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

    @Override
    public Reference<?> createReference(Runnable run) {
        return new CustomPhantomReference<>(run, new Object(), referenceQueue);
    }

    @Override
    public void cleaAll() {
        for (Reference<?> ref; (ref = referenceQueue.poll()) != null;) {
//            if (ref.isEnqueued()) {
                ref.clear();
//            }
        }
    }
}
