package cz.muni.fi.pv260.productfilter;

import org.junit.Test;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.CatchException.verifyException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AtLeastNOfFilterTest {
    @Test
    public void it_throws_exception_when_creating_with_invalid_arguments() throws Exception {
        // noinspection unchecked
        verifyException(() -> new AtLeastNOfFilter<>(0));
        assertThat((Exception) caughtException()).isInstanceOf(IllegalArgumentException.class);

        verifyException(() -> new AtLeastNOfFilter<>(2, mock(PriceLessThanFilter.class)));
        assertThat((Exception) caughtException()).isInstanceOf(FilterNeverSucceeds.class);
    }

    private interface SampleFilter extends Filter<Boolean> {}
    @Test
    public void it_passes_if_at_least_exactly_n_child_filters_pass() {
        // Anonymous class can be replaced with lambda in Java 8
        SampleFilter f1 = item -> true;
        SampleFilter f2 = item -> false;
        SampleFilter f3 = item -> false;

        AtLeastNOfFilter<Boolean> filter = new AtLeastNOfFilter<>(1, f1, f2, f3);
        assertThat(filter.passes(true)).isTrue();
    }

    @Test
    public void it_fails_if_at_most_n_minus_one_child_filters_pass() {
        PriceLessThanFilter f1 = mock(PriceLessThanFilter.class);
        PriceLessThanFilter f2 = mock(PriceLessThanFilter.class);
        PriceLessThanFilter f3 = mock(PriceLessThanFilter.class);

        when(f1.passes(any(Product.class))).thenReturn(true);
        when(f2.passes(any(Product.class))).thenReturn(false);
        when(f3.passes(any(Product.class))).thenReturn(false);

        // We want 2 to pass, but only f1 passes => n = 2, n-1 = 1 => failure
        AtLeastNOfFilter<Product> filter = new AtLeastNOfFilter<>(2, f1, f2, f3);

        assertThat(filter.passes(mock(Product.class))).isFalse();
    }
}
