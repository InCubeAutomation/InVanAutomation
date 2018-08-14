package testng_config_methods;

import io.appium.java_client.MobileElement;
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
    public static String EmulatorName;
    public static String AndroidVersion;
    public static String EmployeeCode;
    public static WebDriverWait wait;

    AppiumDriverLocalService service = AppiumDriverLocalService.buildDefaultService();

    @Parameters({"EmulatorName", "DeviceName", "AndroidVersion", "ApplicationPath","EmployeeCode"})
    @BeforeSuite
    public void setup(@Optional String EmulatorName, String DeviceName, String AndroidVersion, String ApplicationPath, String EmployeeCode) {
        service.start();

        DesiredCapabilities MobileDevice = DesiredCapabilities.android();
        MobileDevice.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        TestNGConfig.EmulatorName = EmulatorName;
        TestNGConfig.EmployeeCode = EmployeeCode;
        TestNGConfig.AndroidVersion = AndroidVersion;
        if (EmulatorName != null) {
            MobileDevice.setCapability("avd", EmulatorName);
        }
        MobileDevice.setCapability(MobileCapabilityType.DEVICE_NAME, DeviceName);
        MobileDevice.setCapability(MobileCapabilityType.PLATFORM_VERSION, AndroidVersion);
        MobileDevice.setCapability(MobileCapabilityType.NO_RESET, "True");
        MobileDevice.setCapability(MobileCapabilityType.APP, ApplicationPath);
        //MobileDevice.setCapability("appPackage","com.incube.invan");
        //MobileDevice.setCapability("appActivity",".SplashScreen");
        MobileDevice.setCapability("autoGrantPermissions", "true");
        MobileDevice.setCapability("disableAndroidWatchers","true");
        MobileDevice.setCapability("skipUnlock","true");
        driver = new AndroidDriver<MobileElement> (service.getUrl(), MobileDevice);
        wait = new WebDriverWait(driver, 15);

    }

    @AfterSuite
    public void teardown() {
       driver.quit();
        if (EmulatorName != null) {
            try {
                Runtime.getRuntime().exec("adb -s emulator-5554 emu kill");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Runtime.getRuntime().exec("adb kill-server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        service.stop();
    }
}
