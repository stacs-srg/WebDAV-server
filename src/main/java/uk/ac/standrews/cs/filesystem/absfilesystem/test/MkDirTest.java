/*
 * Created on May 22, 2005 at 4:19:05 PM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.test;

import junit.framework.TestCase;

/**
 * Test class for creating directories.
 * 
 * @author al, graham
 */
public class MkDirTest extends TestCase {

//    String names[] = { "a", "b", "c", "d", "e", "f" };
//    String threeblindmice = "Three blind mice,Three blind mice, See how they Run, See how they Run.";
//    String fishtext = "Fishy fishy fish fish how I wonder what you are";
//    String twinkletwinkle = "Twinkle, twinkle little star, how I wonder what you are";
//    String f1 = "threeblindmice.dat";
//    String f2 = "twinkle.dat";
//    String f3 = "fishy.fsh";
//        
//    IFileSystem fs;
//    
//    /**
//     * Creates a set of directories
//     * 
//     */
//    public MkDirTest() {
//        Diagnostic.setLevel(Diagnostic.FULL);
//    
//        IGUIDStore store = LocalFileBasedStoreFactory.makeTestStore();
//        Diagnostic.trace( "Store created", Diagnostic.FULL );
//        MakeStore.createRoot( store );
//
//        try {
//            fs = new StoreBasedFileSystem(store);
//     
//            Diagnostic.trace( "Getting root directory", Diagnostic.FULL );
//            IDirectory nextDir = fs.getRootDirectory();
//            for( int index = 0; index < names.length; index++ ) {
//                String nextName = names[index];
//                fs.createNewDirectory( nextDir, nextName, "");
//                nextDir = (IDirectory) nextDir.get(nextName);
//            }
//        }
//        catch (Exception e) { e.printStackTrace(); fail(); }
//    }
//
//    public void testStuite() {
//        testTraverse();
//        testCreateFiles();
//    }
//    
//    public void testTraverse() {
//        Diagnostic.trace( "Getting root directory", Diagnostic.FULL );
//        IDirectory nextDir;
//        try {
//            nextDir = fs.getRootDirectory();
//            for( int index = 0; index < names.length; index++ ) {
//                String nextName = names[index];
//                nextDir = (IDirectory) nextDir.get(nextName);
//                
//            }
//        } catch (Exception e) {
//            Error.exceptionError( "Getting root Directory", e);
//            fail();
//        } 
//
//    }
//    
//    public void testCreateFiles() {
//        try {
//            Diagnostic.trace( "Getting root directory", Diagnostic.FULL );
//            IDirectory nextDir = fs.getRootDirectory(); 
//            for( int index = 0; index < names.length; index++ ) {
//                createFiles( nextDir );
//                readFiles( nextDir );
//                String nextName = names[index];
//                nextDir = (IDirectory) nextDir.get(nextName);
//            }
//        } catch (Exception e) {
//            fail();
//        }
//    }
//    
//    public void createFiles( IDirectory which ) {
//        try {
//            Diagnostic.trace( "Creating file threeblindmice.dat", Diagnostic.FULL );
//            fs.createNewFile( which, f1, "TEXT/TEXT", new StringData( threeblindmice ), "" );
//            Diagnostic.trace( "twinkle.dat", Diagnostic.FULL );
//            fs.createNewFile( which, f2, "TEXT/TEXT", new StringData( twinkletwinkle ), "" );
//            Diagnostic.trace( "fish file", Diagnostic.FULL );
//            fs.createNewFile( which, f3, "TEXT/TEXT", new StringData( fishtext ), "" );
//            Diagnostic.trace( "finished", Diagnostic.FULL );
//        } catch (Exception e) {
//            fail();
//        }
//    }
//    
//    public void readFiles( IDirectory which ) {
//        try {
//            IAttributedStatefulObject apo = which.get( f1 );
//            assertTrue( apo instanceof IFile);
//            assertEquals( new String( apo.reify().getState() ), threeblindmice );
//            apo = which.get( f2 );
//            assertTrue( apo instanceof IFile);
//            assertEquals( new String( apo.reify().getState() ), twinkletwinkle );
//            apo = which.get( f3 );
//            assertTrue( apo instanceof IFile);
//            assertEquals( new String( apo.reify().getState() ), fishtext );
//        } catch (Exception e) {
//            fail();
//        }
//    }
 
}
