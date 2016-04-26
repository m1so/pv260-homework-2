package cases;

import org.junit.After;
import org.junit.Before;
import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;
import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import java.lang.reflect.InvocationTargetException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;

/**
 * Basic Selenium Case class which uses HtmlUnitDriver and mutes CSS errors.
 *
 * @author Michal Baumgartner
 */
public class SeleniumTestCase {
    protected WebDriver webDriver;

    @Before
    public void setUp() {
        webDriver = new HtmlUnitDriver();

        modifyWebClient();
    }

    @After
    public void tearDown() {
        webDriver.quit();
    }

    /**
     * Disable CSS error handling to have a nicer console output without the errors
     */
    private void modifyWebClient() {
        try {
            // Get the protected method from HtmlUnitDriver called getWebClient and make it accessible
            Method method = webDriver.getClass().getDeclaredMethod("getWebClient");
            method.setAccessible(true);
            // Invoke the method and get WebClient
            Object o = method.invoke(webDriver);
            WebClient webClient = (WebClient) o;
            // Shut the WebClient up
            webClient.setCssErrorHandler(new SilentCssErrorHandler());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("Failed to mute the goddamn HtmlUnitDriver.");
            // e.printStackTrace();
        }
    }
}
