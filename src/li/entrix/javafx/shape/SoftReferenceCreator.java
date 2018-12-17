package li.entrix.javafx.shape;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

public class SoftReferenceCreator implements ReferenceCreator {

    @Override
    public Reference<?> createReference(Runnable run) {
        return new SoftReference<>(new Object());
    }

    @Override
    public void cleaAll() {
        // nothing
    }
}
