package yumProxy.utils;


public class PortUtils {
    
    
    public static boolean isPortInUse(int port) {
        try {
            java.net.ServerSocket serverSocket = new java.net.ServerSocket(port);
            serverSocket.close();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    
    
    public static boolean waitForPortRelease(int port, int maxWaitSeconds) {

        
        for (int i = 0; i < maxWaitSeconds; i++) {
            try {
                Thread.sleep(1000);
                if (!isPortInUse(port)) {

                    return true;
                }
                if (i % 5 == 0) {

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        

        return false;
    }
    
    
    public static boolean checkAndWaitForPort(int port) {
        if (!isPortInUse(port)) {
            return true;
        }
        
        return waitForPortRelease(port, 30);
    }
} 
