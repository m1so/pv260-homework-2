import cases.SeleniumTestCase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class WikipediaTest extends SeleniumTestCase {
    @Test
    public void it_checks_if_wikipedia_contains_selenium_article() {
        webDriver.get("https://cs.wikipedia.org");

        WebElement searchField = webDriver.findElement(By.name("search"));
        searchField.sendKeys("Selenium");
        searchField.submit();

        boolean containsString = webDriver.getPageSource().contains("Selenium (software)");
        assertTrue("Wikipedia contains Selenium software string", containsString);
    }

    @Test
    public void it_edits_a_wikipedia_article() {
        // NOTE: By testing "edit" with link we get redirected to Wikipedia's interactive editor,
        //       safer approach is to visit the old edit url
        webDriver.get("https://cs.wikipedia.org/wiki/Selenium_(software)?action=edit&veswitched=1");

        boolean containsString = webDriver.getPageSource().contains("Editace stránky Selenium (software)");
        assertTrue("Wikipedia page can be edited", containsString);
    }

    @Test
    public void it_can_switch_language_on_wikipedia_article() {
        webDriver.get("https://cs.wikipedia.org/wiki/Selenium_(software)");

        WebElement englishLink = webDriver.findElement(By.linkText("English"));
        englishLink.click();

        assertNotNull(webDriver.getCurrentUrl());
        assertEquals(webDriver.getCurrentUrl(), "https://en.wikipedia.org/wiki/Selenium_(software)");
    }

    @Test
    public void it_finds_technology_portal_on_portal_overview_page() {
        webDriver.get("https://en.wikipedia.org/wiki/Portal:Contents/Portals");

        WebElement technologyContentLink = webDriver.findElement(By.linkText("Technology"));
        technologyContentLink.click();

        assertNotNull(webDriver.getCurrentUrl());
        assertEquals(webDriver.getCurrentUrl(), "https://en.wikipedia.org/wiki/Portal:Contents/Technology_and_applied_sciences");

        WebElement technologyPortalLink = webDriver.findElement(By.linkText("The Technology Portal"));
        technologyPortalLink.click();

        assertNotNull(webDriver.getCurrentUrl());
        assertEquals(webDriver.getCurrentUrl(), "https://en.wikipedia.org/wiki/Portal:Technology");
    }

    @Test
    public void it_fails_to_login_into_wikipedia() {
        webDriver.get("https://en.wikipedia.org/wiki/Special:UserLogin");

        WebElement loginField = webDriver.findElement(By.id("wpName1"));
        loginField.sendKeys("Just testing");

        WebElement passwordField = webDriver.findElement(By.id("wpPassword1"));
        passwordField.sendKeys("Just testing");

        passwordField.submit();

        boolean containsString = webDriver.getPageSource().contains("There is no user by the name \"Just testing\"");
        assertTrue("Login attempt fails correctly", containsString);
    }
}
