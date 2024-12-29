// Edited version of https://stackoverflow.com/a/9976304 and distributed under the same
// license: https://creativecommons.org/licenses/by-sa/3.0/

package pcd.ass_single.part1.threads;

import java.util.Objects;
import java.util.function.Consumer;

public class Either<A, B> {
    private final A left;
    private final B right;

    private Either(A a, B b) {
        left = a;
        right = b;
    }

    public static <A, B> Either<A, B> left(A a) {
        return new Either<>(Objects.requireNonNull(a), null);
    }

    public static <A, B> Either<A, B> right(B b) {
        return new Either<>(null, Objects.requireNonNull(b));
    }

    public void fold(Consumer<A> ifLeft, Consumer<B> ifRight) {
        if (right == null) {
            ifLeft.accept(left);
        }
        else {
            ifRight.accept(right);
        }
    }
}
