package com.example.demo;

import java.net.URI;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;

import ch.qos.logback.classic.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class GoogleSeleniumTest {

	static final Logger LOGGER = (Logger) LoggerFactory.getLogger(GoogleSeleniumTest.class);

	static final String provider = "google";

	@Autowired
	DemoController demeController;

	String email =  "";
	String pwd = "";
	

	String res = "";

	@BeforeTestExecution
	public void setup() {
		LOGGER.info("setup webdriver");
		System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver"); // 다운받은 ChromeDriver 위치를 넣어줍니다.
		
	}

	@Test
	@Order(1)
	public void authentication() throws Exception {
		final String provider = "google";
		final ResponseEntity response = demeController.authentication(provider);

		LOGGER.debug("authentication : {}", response.toString());

		final HttpHeaders headers = response.getHeaders();
		final URI uri = headers.getLocation();
		LOGGER.debug("authentication location : {}", uri.toString());

		final ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		options.setExperimentalOption("useAutomationExtension", false);
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

		final WebDriver driver = new ChromeDriver(options); // Driver 생성
		Map resultMap = null;
		try {

			driver.get(uri.toString());
			driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			new WebDriverWait(driver, 10)
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='identifierId']")))
					.sendKeys(email);
			driver.findElement(By.id("identifierNext")).click();
			new WebDriverWait(driver, 10)
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='password']"))).sendKeys(pwd);
			driver.findElement(By.id("passwordNext")).click();
			
			LOGGER.debug("authentication complete!!");

			//waitForPageLoaded(driver);
			new WebDriverWait(driver, 3).until(ExpectedConditions.urlContains("http://localhost:4080/authentication/callback/google"));
			/*
			new WebDriverWait(driver, 5).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					LOGGER.debug("waitting!! {}", driver.getCurrentUrl().startsWith("http://localhost:4080/authentication/callback/google?"));
					return driver.getCurrentUrl().startsWith("http://localhost:4080/authentication/callback/google?");
				}
			});
			*/
			

			//LOGGER.debug("authentication redirect url : {}", driver.getCurrentUrl());

		} catch (final Exception ex) {
			//LOGGER.error("error", ex);
			//throw ex;
		} finally {
			String redirectUrl = driver.getCurrentUrl();
			LOGGER.debug("authentication redirect url : {}", redirectUrl);
			driver.quit(); // Driver 종료
			try{resultMap = queryToMap(redirectUrl);}catch(Exception e) {}
			
		}
		LOGGER.debug("parameter : {}", resultMap.toString());

		final ResponseEntity callbackResponse = demeController.callback(provider, resultMap);

		LOGGER.debug("callback response : {}", callbackResponse.toString());
	}

	/*
	 * @Test
	 * 
	 * @Order(2) public void callback() throws Exception {
	 * 
	 * ResponseEntity response = demeController.callback(provider, new HashMap());
	 * 
	 * LOGGER.debug("callback : {}, res ", res, response.toString()); }
	 */
	@AfterTestClass
	public void tearDown() {

	}

	Map<String, String> queryToMap(String query) throws Exception {

		Map<String, String> map = new HashMap<String, String>();

		String[] splits = query.split("\\?")[1].split("&");

		for(String split : splits ){
			String[] kv = split.split("=");

			if(!kv[0].isEmpty() && !kv[1].isEmpty()){
				map.put(kv[0], URLDecoder.decode(kv[1], "UTF-8"));
			}
		}
		
		return map;
	}

	public void waitForPageLoaded(final WebDriver chromeDriver) throws Exception {
		
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				String r = ((JavascriptExecutor)driver).executeScript("return document.readyState").toString();
				LOGGER.debug("waitForPageLoaded : {}", r);
				return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete") == false;
			}
		};
			
		final WebDriverWait wait = new WebDriverWait(chromeDriver, 30);
		wait.until(pageLoadCondition);
	}
}


