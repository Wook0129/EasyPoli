package datacrawl;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class NewsquareCrawler {
   public static void main(String[] args){
      System.setProperty("webdriver.chrome.driver",
    		  "C:/Users/태욱/workspace/EasyPoli/data/chromedriver.exe");

      WebDriver driver = new ChromeDriver();
      driver.get("http://www.newsquare.kr/categories/politics");
      WebElement issues = driver.findElement(By.xpath("//*[@id=\"ember406\"]/main/div[2]"));
      System.out.println(issues.getText());
//      executor.executeScript("javascript:fnLogin();", loginButton);
   }
   
}
