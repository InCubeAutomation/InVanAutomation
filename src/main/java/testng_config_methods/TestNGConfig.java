package testng_config_methods;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.IOException;

public class TestNGConfig {

    public static  SoftAssert softAssert = new SoftAssert();
    public static AndroidDriver driver;
    public String EmulatorName = null;
    AppiumDriverLocalService service = AppiumDriverLocalService.buildDefaultService();

    @Parameters({"EmulatorName", "DeviceName", "AndroidVersion", "ApplicationPath"})
    @BeforeTest
    public void setup(@Optional String EmulatorName, String DeviceName, String AndroidVersion, String ApplicationPath) {
        service.start();

        DesiredCapabilities MobileDevice = DesiredCapabilities.android();
        MobileDevice.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        this.EmulatorName = EmulatorName;
        if (!EmulatorName.isEmpty()) {
            MobileDevice.setCapability("avd", EmulatorName);
        }
        MobileDevice.setCapability(MobileCapabilityType.DEVICE_NAME, DeviceName);
        MobileDevice.setCapability(MobileCapabilityType.PLATFORM_VERSION, AndroidVersion);
        MobileDevice.setCapability(MobileCapabilityType.NO_RESET, "True");
        MobileDevice.setCapability(MobileCapabilityType.APP, ApplicationPath);
        //MobileDevice.setCapability("appPackage","com.incube.invan");
        //MobileDevice.setCapability("appActivity",".SplashScreen");
        MobileDevice.setCapability("autoGrantPermissions", "true");

        driver = new AndroidDriver(service.getUrl(), MobileDevice);
        WebDriverWait wait = new WebDriverWait(driver, 120);
    }

    @AfterTest
    public void teardown() {
       driver.quit();
        if (!EmulatorName.isEmpty()) {
            try {
                Runtime.getRuntime().exec("adb -s emulator-5554 emu kill");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        service.stop();
    }
}
