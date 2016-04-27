package cz.muni.fi.pv260.productfilter;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class ControllerTest {
    @Captor
    private ArgumentCaptor<Collection<Product>> captor;

    @Test
    public void it_sends_exactly_the_products_selected_by_the_provided_filter_to_output() throws ObtainFailedException {
        // Use this for cleaner captor syntax - we do not have to do messy retyping manually
        MockitoAnnotations.initMocks(this);

        // We need to verify that Controller.select(Product[], Filter<Product>) returns only those products that passed
        // the filtering stage.
        Logger logger = mock(Logger.class);
        Input input = mock(Input.class);
        Output output = mock(Output.class);

        Filter<Product> filter = (Filter<Product>) mock(Filter.class);

        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);
        Product p3 = mock(Product.class);

        List<Product> products = asList(p1, p2, p3);

        when(input.obtainProducts()).thenReturn(products);

        when(filter.passes(p1)).thenReturn(true);
        when(filter.passes(p2)).thenReturn(false);
        when(filter.passes(p3)).thenReturn(false);

        Controller controller = new Controller(input, output, logger);
        controller.select(filter);

        // Next we need to verify, if those products are passed as argument to Output.postSelectedProducts(Product[])
        verify(output).postSelectedProducts(captor.capture());
        assertThat(captor.getValue()).containsExactly(p1);
    }

    @Test
    public void it_logs_the_message_in_documented_format_on_success() throws ObtainFailedException {
        Logger logger = mock(Logger.class);
        Input input = mock(Input.class);
        Output output = mock(Output.class);

        Filter<Product> filter = (Filter<Product>) mock(Filter.class);

        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);
        Product p3 = mock(Product.class);

        List<Product> products = asList(p1, p2, p3);

        when(input.obtainProducts()).thenReturn(products);

        when(filter.passes(p1)).thenReturn(true);
        when(filter.passes(p2)).thenReturn(false);
        when(filter.passes(p3)).thenReturn(false);

        Controller controller = new Controller(input, output, logger);
        controller.select(filter);

        verify(logger, never()).setLevel("ERROR");
        verify(logger).setLevel("INFO");
        verify(logger).log(Controller.TAG_CONTROLLER, "Successfully selected 1 out of 3 available products.");
    }

    @Test
    public void it_behaves_and_logs_correctly_when_exception_was_thrown_while_obtaining_products() throws ObtainFailedException {
        Logger logger = mock(Logger.class);
        Input input = mock(Input.class);
        Output output = mock(Output.class);
        // noinspection ThrowableInstanceNeverThrown
        ObtainFailedException exception = new ObtainFailedException();
        when(input.obtainProducts()).thenThrow(exception);

        Controller controller = new Controller(input, output, logger);
        Filter<Product> filter = (Filter<Product>) mock(Filter.class);
        controller.select(filter);

        verify(logger).setLevel("ERROR");
        verify(logger, never()).setLevel("INFO");
        verify(logger).log(Controller.TAG_CONTROLLER, "Filter procedure failed with exception: " + exception);
        verifyZeroInteractions(output);
    }
}
