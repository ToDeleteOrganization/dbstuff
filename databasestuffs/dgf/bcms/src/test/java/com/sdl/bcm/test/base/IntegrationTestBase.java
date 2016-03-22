package com.sdl.bcm.test.base;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public abstract class IntegrationTestBase {

    public final static String BCM_URL = "bcmURL";
    public final static String BCM_STORAGE_APP_NAME = "bcmStorageAppName";
    public final static String STATISTICS_PATH = "statisticsPath";


    protected static String bcmUrl;
    protected static String bcmStorageAppName;
    protected static String statisticsPath;

    protected static Properties testConfig;

    @BeforeClass
    public static void setUp() {
        testConfig = new Properties();
        InputStream in = IntegrationTestBase.class.getResourceAsStream("/integrationTestsConfig.properties");
        try {
            testConfig.load(in);
            bcmStorageAppName = testConfig.getProperty(BCM_STORAGE_APP_NAME);
            bcmUrl = testConfig.getProperty(BCM_URL);
            statisticsPath = testConfig.getProperty(STATISTICS_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    public static String getBcmUrl() {
        return bcmUrl;
    }

    public static void setBcmUrl(String bcmUrl) {
        IntegrationTestBase.bcmUrl = bcmUrl;
    }
}
