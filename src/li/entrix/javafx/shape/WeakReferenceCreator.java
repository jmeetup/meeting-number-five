package li.entrix.javafx.shape;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class WeakReferenceCreator implements ReferenceCreator {

    @Override
    public Reference<?> createReference(Runnable run) {
        return new WeakReference<>(new Object());
    }

    @Override
    public void cleaAll() {
        // nothing
    }
}
