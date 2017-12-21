package dk.kb.ginnungagap.testutils;

import java.security.Permission;

public class TestSystemUtils {
    @SuppressWarnings("serial")
    public static class ExitTrappedException extends SecurityException { }

    public static void forbidSystemExitCall() {
      final SecurityManager securityManager = new SecurityManager() {
        public void checkPermission( Permission permission ) {
          if( permission.getName().startsWith("exitVM") ) {
            throw new ExitTrappedException() ;
          }
        }
      } ;
      System.setSecurityManager( securityManager ) ;
    }

    public static void enableSystemExitCall() {
      System.setSecurityManager( null ) ;
    }
}
