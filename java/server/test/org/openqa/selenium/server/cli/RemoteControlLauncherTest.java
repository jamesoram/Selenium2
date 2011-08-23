package org.openqa.selenium.server.cli;

import org.openqa.selenium.server.RemoteControlConfiguration;

import junit.framework.TestCase;

/**
 * {@link org.openqa.selenium.server.cli.RemoteControlLauncher} unit test class.
 */
public class RemoteControlLauncherTest extends TestCase {

    public void testHonorSystemProxyIsSetWhenProvidedAsAnOption() {
        final RemoteControlConfiguration configuration;
        
        configuration = RemoteControlLauncher.parseLauncherOptions(new String[]{"-honor-system-proxy"});
        assertTrue(configuration.honorSystemProxy());
    }

    public void testHonorSystemProxyIsFalseWhenNotProvidedAsAnOption() {
        final RemoteControlConfiguration configuration;

        configuration = RemoteControlLauncher.parseLauncherOptions(new String[]{});
        assertFalse(configuration.honorSystemProxy());
    }

}
